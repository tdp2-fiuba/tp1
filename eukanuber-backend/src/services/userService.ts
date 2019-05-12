import Express from 'express';
import Knex from 'knex';
import db from '../db/db';
import { IUser } from '../models';
import ICreateUserData from '../models/ICreateUserData';
import facebookService from './facebookService';
var moment = require('moment');

const MIN_FRIEND_COUNT = 10;

interface IPosition {
  lat: string;
  lng: string;
}

async function getUsers() {
  const rows = await db.table('users').select();
  return rows;
}

async function getUserById(id: string) {
  const users = await db('users')
    .leftJoin('cars', 'users.id', '=', 'cars.userId')
    .innerJoin('userMedia', 'users.id', '=', 'userMedia.userId')
    .where('users.id', id)
    .select();

  if (!users) {
    return undefined;
  }

  const userData = users[0];
  const userImages = users.map((u: any) => ({ fileName: u.fileName, fileContent: Buffer.from(u.fileContent).toString('base64') }));

  const user = {
    id,
    userType: userData.userType,
    firstName: userData.firstName,
    lastName: userData.lastName,
    position: userData.position,
    state: userData.state,
    images: userImages,
    loggedIn: userData.loggedIn,
  };

  if (user.userType.toLowerCase() === 'driver') {
    return { ...user, car: { model: userData.model, brand: userData.brand, plateNumber: userData.plateNumber } };
  }

  return user;
}

async function getUserByFbId(fbId: string) {
  try {
    const user = await db
      .table('users')
      .where('fbId', fbId)
      .select()
      .first();

    if (!user) {
      return undefined;
    }

    return user.id;
  } catch (e) {
    return undefined;
  }
}

async function getUserReviews(id: string) {
  try {
    const rating = await db
      .table('userReview')
      .where('reviewee', id)
      .select();

    if (!rating) {
      return undefined;
    }

    return rating;
  } catch (e) {
    return undefined;
  }
}

const createTransaction = () => new Promise(resolve => db.transaction(resolve));

async function createUser(newUser: ICreateUserData) {
  // TODO: ver por que esto no me deja castear usando ...newUser
  const user = {
    userType: newUser.userType,
    firstName: newUser.firstName,
    lastName: newUser.lastName,
    position: newUser.position,
    fbAccessToken: newUser.fbAccessToken,
    fbId: newUser.fbId,
  };

  const validationResult = await validateFacebookAccount(newUser.fbId, newUser.fbAccessToken);

  if (!validationResult.isValid) {
    throw new Error(validationResult.errorMessage);
  }

  const transaction = (await createTransaction()) as Knex.Transaction;

  try {
    const userInsertResult = await transaction('users')
      .insert(user)
      .returning('id');
    const userId = userInsertResult[0];
    const fields = newUser.images.map(img => ({ userId, fileName: img.fileName, fileContent: img.fileContent }));
    await transaction('userMedia').insert(fields);

    if (newUser.userType.toLowerCase() === 'driver') {
      if (!newUser.car) {
        throw new Error('Debe registrar un vehículo!');
      }
      const car = {
        ...newUser.car,
        userId,
      };
      await transaction('cars').insert(car);
    }

    await transaction.commit();
    return { userId, ...user } as any;
  } catch (err) {
    transaction.rollback();
    console.error(`User creation transaction aborted!"${err}"`);
    throw new Error('Creación de usuario abortada!');
  }
}

async function updateUser(id: string, userData: Partial<IUser>) {
  const user = await db
    .table('users')
    .where('id', id)
    .select()
    .first();

  const updateData = {
    firstName: userData.firstName ? userData.firstName : user.firstName,
    lastName: userData.lastName ? userData.lastName : user.lastName,
    position: userData.position ? userData.position : user.position,
  };

  return await updateUserWithData(id, updateData);
}

async function submitUserReview(raterId: string, ratedId: string, tripId: string, review: any) {
  try {
    const newReview = {
      reviewer: raterId,
      reviewee: ratedId,
      tripId: tripId,
      stars: review.stars,
      comment: review.comment,
      dateTime: moment().format('yyyy-mm-dd:hh:mm:ss'),
    };

    await db.table('userReview').insert(newReview);

    return newReview as any;
  } catch (err) {
    throw new Error(err);
  }
}

async function updateUserWithData(id: string, updateData: any) {
  await db
    .table('users')
    .where('id', id)
    .update(updateData);

  return ((await db
    .table('users')
    .where('id', id)
    .select()) as string[])[0];
}

async function getUserPosition(id: string) {
  const currentLoc = await db
    .table('users')
    .where('id', id)
    .select('position')
    .first();

  return currentLoc as any;
}

async function updateUserPosition(id: string, pos: IPosition) {
  const newPos: string = pos.lat + ',' + pos.lng;

  const user = await db
    .table('users')
    .where('id', id)
    .update('position', newPos);

  return await db
    .table('users')
    .where('id', id)
    .select()
    .first();
}

async function userLogin(id: string) {
  try {
    await db
      .table('users')
      .where('id', id)
      .update({ loggedIn: true });

    return getUserById(id);
  } catch (e) {
    return false;
  }
}

async function userLogout(id: string) {
  try {
    const updateData = {
      loggedIn: false,
    };

    await db
      .table('users')
      .where('id', id)
      .update(updateData);

    return true;
  } catch (e) {
    return false;
  }
}

async function isUserLogged(id: string) {
  try {
    console.log(`CHECK USER "${id}" LOGGED IN`);
    return await getUserById(id).then(result => result.loggedIn);
  } catch (e) {
    return false;
  }
}
async function deleteUser(fbId: string) {
  return await db
    .table('users')
    .del()
    .where({ fbId });
}

async function validateFacebookAccount(fbId: string, fbAccessToken: string) {
  const accountData = await facebookService.getFacebookFriendCount(fbAccessToken);
  const isValid = fbId === accountData.id && accountData.friends.summary.total_count >= MIN_FRIEND_COUNT;

  return { isValid, errorMessage: !isValid && `Cuenta de facebook invalida. Debe tener más de ${MIN_FRIEND_COUNT} amigos en la cuenta` };
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
  updateUserPosition,
  deleteUser,
};
