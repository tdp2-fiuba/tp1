import Express from "express";
import Knex from "knex";
import db from "../db/db";
import { IUser } from "../models";
import ICreateUserData from "../models/ICreateUserData";
import firebaseService from "../services/firebaseService";
import facebookService from "./facebookService";
require("dotenv").config();

const moment = require("moment");
const { raw } = require("objection");

const MIN_FRIEND_COUNT = 0; // 10;

interface IPosition {
  lat: string;
  lng: string;
}

async function getUsers() {
  const rows = await db.table("users").select();
  return rows;
}

async function getUserById(id: string) {
  const userData = await db("users")
    .where("users.id", id)
    .first();

  if (!userData) {
    return undefined;
  }

  const userCarsResult = await db("cars").where("userId", id);
  const userCar = userCarsResult && userCarsResult[0];

  const userImagesResult = await db("userMedia").where("userId", id);
  const userImages = userImagesResult.map((u: any) => ({
    fileName: u.fileName,
    fileContent: Buffer.from(u.fileContent).toString("base64")
  }));

  return {
    id,
    userType: userData.userType,
    firstName: userData.firstName,
    lastName: userData.lastName,
    position: userData.latitude + "," + userData.longitude,
    access: userData.access,
    state: userData.state,
    images: userImages,
    car: userCar,
    loggedIn: userData.loggedIn
  };
}

async function getUserReviews(id: string) {
  try {
    const reviews = await db
      .table("userReview")
      .where("reviewee", id)
      .andWhereNot("reviewer", id) // If reviewer is user, then those correspond to penalizations, should not be returned.
      .select("*");

    if (!reviews) {
      return undefined;
    }
    return reviews;
  } catch (e) {
    return undefined;
  }
}

async function getUserByFbId(fbId: string) {
  try {
    const user = await db
      .table("users")
      .where("fbId", fbId)
      .select()
      .first();
    console.log("user: ", user);
    if (!user) {
      return undefined;
    }

    return user.id;
  } catch (e) {
    console.log("error: ", e);
    return undefined;
  }
}

async function getUserFirebaseToken(userId: string) {
  console.log("getUserFirebaseToken: " + userId);
  try {
    const firebaseToken = await db
      .table("users")
      .where("id", userId)
      .select("firebaseToken")
      .first();
    console.log(firebaseToken);

    if (!firebaseToken) {
      return undefined;
    }

    return firebaseToken.firebaseToken;
  } catch (e) {
    console.log(e);
    return undefined;
  }
}

async function notifyUser(userId: string, message: any) {
  const registrationToken = await getUserFirebaseToken(userId);
  if (!registrationToken || registrationToken == undefined) {
    console.log(`No firebase token found for user '${userId}'`);
    return;
  }
  firebaseService.sendNotification(registrationToken, message);
}

async function getUserRating(id: string) {
  try {
    const rating = await db
      .table("userReview")
      .where("reviewee", id)
      .sum({ sum: "stars" })
      .count({ count: "stars" })
      .groupBy("reviewee");
    if (!rating || rating === undefined || rating.length === 0) {
      return { sum: 0, count: 0 };
    }

    return rating[0];
  } catch (e) {
    return { sum: 0, count: 0 };
  }
}

const createTransaction = () => new Promise(resolve => db.transaction(resolve));

async function createUser(newUser: ICreateUserData) {
  // TODO: ver por que esto no me deja castear usando ...newUser

  const pos =
    newUser.position
      .trim()
      .replace(" ", "")
      .split(",").length > 1
      ? newUser.position.split(",")
      : ["", ""];

  const user = {
    userType: newUser.userType.toLowerCase(),
    firstName: newUser.firstName,
    lastName: newUser.lastName,
    latitude: pos[0],
    longitude: pos[1],
    fbAccessToken: newUser.fbAccessToken,
    fbId: newUser.fbId
  };

  const validationResult = await validateFacebookAccount(newUser.fbId, newUser.fbAccessToken);

  if (!validationResult.isValid) {
    throw new Error(validationResult.errorMessage);
  }

  const transaction = (await createTransaction()) as Knex.Transaction;

  try {
    const userInsertResult = await transaction("users")
      .insert(user)
      .returning("id");
    const userId = userInsertResult[0];
    const fields = newUser.images.map(img => ({ userId, fileName: img.fileName, fileContent: img.fileContent }));
    await transaction("userMedia").insert(fields);

    if (newUser.userType.toLowerCase() === "driver") {
      if (!newUser.car) {
        throw new Error("Debe registrar un vehículo!");
      }
      const car = {
        ...newUser.car,
        userId
      };
      await transaction("cars").insert(car);
    }

    await transaction.commit();
    return { userId, ...user } as any;
  } catch (err) {
    transaction.rollback();
    console.error(`User creation transaction aborted!"${err}"`);
    throw new Error("Creación de usuario abortada!");
  }
}

async function updateUserState(id: string, state: number) {
  return await updateUserWithData(id, { state });
}

async function updateUser(id: string, userData: Partial<IUser>) {
  const user = await db
    .table("users")
    .where("id", id)
    .select()
    .first();
  const pos = userData.position != undefined ? userData.position.split(",") : "";
  const updateData = {
    firstName: userData.firstName ? userData.firstName : user.firstName,
    lastName: userData.lastName ? userData.lastName : user.lastName,
    latitude: pos.length > 1 ? pos[0] : user.latitude,
    longitude: pos.length > 1 ? pos[1] : user.longitude,
    state: userData.state !== undefined ? userData.state : user.state,
    firebaseToken: userData.firebaseToken && userData.firebaseToken.length > 0 ? userData.firebaseToken : user.firebaseToken,
    access: userData.access !== undefined ? userData.access : user.access
  };
  return await updateUserWithData(id, updateData);
}

async function penalizeDriverIgnoredTrip(driverId: string, tripId: string) {
  // TODO: consider number of reviews the driver has to moderate impact on avg.
  // TODO:if unavailable, driver perhaps signed off or is out of workhours, check with client whether they should
  // be penalized in this case.
  return await submitUserReview(driverId, driverId, tripId, { stars: 1, comment: `IGNORED TRIP '${tripId}'` });
}

async function penalizeDriverRejectTrip(driverId: string, tripId: string) {
  // penalize driver by submitting a review with driverId as reviewer id.
  console.log("REJECTED TRIP: Applying penalization to driver.");
  return await submitUserReview(driverId, driverId, tripId, { stars: 3, comment: `REJECTED TRIP '${tripId}'.` });
}

async function submitUserReview(raterId: string, ratedId: string, tripId: string, review: any) {
  try {
    const newReview = {
      reviewer: raterId,
      reviewee: ratedId,
      tripId,
      stars: review.stars,
      comment: review.comment,
      dateTime: moment().format("LLLL")
    };

    await db.table("userReview").insert(newReview);

    return newReview as any;
  } catch (err) {
    throw new Error(err);
  }
}

async function updateUserWithData(id: string, updateData: any) {
  await db
    .table("users")
    .where("id", id)
    .update(updateData);

  return ((await db
    .table("users")
    .where("id", id)
    .select()) as string[])[0];
}

async function getUserRole(id: string) {
  const role = await db
    .table("users")
    .where("id", id)
    .select("userType")
    .first();

  return role;
}

async function getUserPosition(id: string) {
  const currentLoc = await db
    .table("users")
    .where("id", id)
    .select("longitude", "latitude")
    .first();
  const position = {
    position: currentLoc.latitude + "," + currentLoc.longitude
  };
  return position;
}
async function getUserStatus(id: string) {
  const status = await db
    .table("users")
    .where("id", id)
    .select("state")
    .first();
  return status;
}

async function updateUserPosition(id: string, pos: IPosition) {
  const newPos = { latitude: pos.lat, longitude: pos.lng };

  const user = await db
    .table("users")
    .where("id", id)
    .update(newPos);

  return await db
    .table("users")
    .where("id", id)
    .select()
    .first();
}

async function userLogin(id: string) {
  try {
    await db
      .table("users")
      .where("id", id)
      .update({ loggedIn: true });

    return getUserById(id);
  } catch (e) {
    return false;
  }
}

async function userLogout(id: string) {
  try {
    const updateData = {
      loggedIn: false
    };

    await db
      .table("users")
      .where("id", id)
      .update(updateData);

    return true;
  } catch (e) {
    return false;
  }
}

async function isUserLogged(id: string) {
  try {
    return await getUserById(id).then(result => result.loggedIn);
  } catch (e) {
    return false;
  }
}

async function deleteUser(fbId: string) {
  return await db
    .table("users")
    .del()
    .where({ fbId });
}

async function validateFacebookAccount(fbId: string, fbAccessToken: string) {
  const accountData = await facebookService.getFacebookFriendCount(fbAccessToken);
  const isValid = fbId === accountData.id && accountData.friends.summary.total_count >= MIN_FRIEND_COUNT;

  return {
    isValid,
    errorMessage: !isValid && `Cuenta de facebook invalida. Debe tener más de ${MIN_FRIEND_COUNT} amigos en la cuenta`
  };
}

async function getProspectiveDrivers(tripOrigin: string, startDistance: string, stopDistance: string): Promise<IUser[]> {
  const origin = tripOrigin.split(",");
  const distance = `ACOS(SIN(RADIANS(CAST('${origin[0]}' as float))) * SIN(RADIANS(CAST(users.latitude as float))) + COS(RADIANS(CAST('${
    origin[0]
  }' as float))) * COS(RADIANS(CAST(users.latitude as float)))
   * COS(RADIANS(CAST(users.longitude as float) - CAST('${origin[1]}' as float)))) * 3959 `;

  const distanceCond = distance + " <" + (startDistance == "" ? "0" : startDistance) + (stopDistance == "" ? "" : " and " + distance + " <=" + stopDistance);
  const query = `users.id, avg(stars) as rating, count(*) as count,` + distance + `as distance`;
  const args = [distance];
  try {
    const users = await db
      .table("users")
      .select(db.raw(query))
      .leftJoin("userReview", "users.id", "=", "userReview.reviewee")
      .where(db.raw(distanceCond))
      .andWhere("users.state", "0")
      .andWhere("users.userType", "driver")
      .groupBy("users.id")
      .orderBy(["distance", { column: "rating", order: "desc" }]); // order by distance then by rating
    return users;
  } catch (e) {
    return [];
  }
}

export default {
  getUsers,
  getUserById,
  getUserByFbId,
  createUser,
  updateUser,
  userLogin,
  userLogout,
  isUserLogged,
  submitUserReview,
  getUserReviews,
  getUserPosition,
  getUserStatus,
  getProspectiveDrivers,
  updateUserPosition,
  deleteUser,
  getUserRating,
  updateUserState,
  penalizeDriverIgnoredTrip,
  penalizeDriverRejectTrip,
  getUserRole,
  notifyUser
};
