import db from "../db/db";
import Express from "express";
import { IUser } from "../models";
import ICreateUserData  from "../models/ICreateUserData";
import facebookService from "./facebookService";

const MIN_FRIEND_COUNT = 10;

interface IPosition {
    lat: string,
    lng: string
}

async function getUsers() {
    const rows = await db
    .table("users")
    .select();
    return rows;
}

async function getUserById(id: string) {
    let users = await db("users").innerJoin("userMedia", "users.id", "=", "userMedia.userId").where("users.id", id).select();
    
    if (!users) {
      return undefined;
    }

    let userData = users[0];
    let userImages = users.map((u:any) => 
        ({"fileName": u.fileName, "fileContent": Buffer.from(u.fileContent).toString("base64")})
    );

    const user = { 
        id: id,
        userType: userData.userType,
        firstName: userData.firstName,
        lastName: userData.lastName,
        position: userData.position,
        state: userData.state,
        images: userImages,
        loggedIn: userData.loggedIn
        
    }
  
    return user;
  }

async function createUser(newUser: ICreateUserData, res: Express.Response) {
    //TODO: ver por que esto no me deja castear usando ...newUser
    const user = { 
        userType: newUser.userType,
        firstName: newUser.firstName,
        lastName: newUser.lastName,
        position: newUser.position,
        fbId: newUser.fbId
    }
    const accountValidation = await validateFacebookAccount(newUser.fbId);
    if (!accountValidation.validAccount) {
        throw new Error(accountValidation.message);
    }
    db.transaction(async function(t: any) {
        return await db("users")
        .transacting(t)
        .insert(user)
        .returning("id").then(async function(resp) {
            var id = resp[0];
            const fields = newUser.images.map(img => 
                        ({"userId": id, "fileName": img.fileName, "fileContent": img.file})
                    );
            return await db("userMedia").transacting(t).insert(fields).returning("userId");
        })
        .then(t.commit)
        .catch(t.rollback);
    }).then(function(resp:any) {
        console.log('Transaction complete.' + resp);
        //workaround:
        res.send({ id: resp[0] });
    })
    .catch(async function(err) {
        console.error(err);
    });
}

async function updateUser(id: string, userData: Partial<IUser> ) {
    const user = await db
        .table("users")
        .where("id", id)
        .select()
        .first();

    const updateData = {
      firstName: userData.firstName? userData.firstName : user.firstName,
      lastName: userData.lastName? userData.lastName : user.lastName,
      position: userData.position? userData.position : user.position,
    }

    await db
        .table("users")
        .where("id", id)
        .update(updateData);

    return (await db
        .table("users")
        .where("id", id)
        .select() as string[])[0];
}

async function getUserPosition(id: string) {
    const currentLoc = await db
        .table("users")
        .where("id", id)
        .select("position")
        .first();

    return currentLoc as any;
}

async function updateUserPosition(id: string, pos: IPosition) {
    const newPos: string = pos.lat + "," + pos.lng;

    const user = await db
        .table("users")
        .where("id", id)
        .update("position", newPos);

    return await db
        .table("users")
        .where("id", id)
        .select()
        .first();
}

async function userLogin(id: string) {
    try {
        const updateData = {
            loggedIn: true
        }

        await db
            .table("users")
            .where("id", id)
            .update(updateData);

        return true;
    } catch (e) {
        return false;
    }
}

async function userLogout(id: string) {
    try {
        const updateData = {
            loggedIn: false
        }

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

interface FacebookData {
    data: [];
    summary: { total_count: number };
}

async function validateFacebookAccount(id: string) {
    const friendCount: string = await facebookService.getFacebookFriendCount(id);
    const validAccount = (parseInt(friendCount, 10) >= MIN_FRIEND_COUNT);
    return { validAccount: validAccount, message: (validAccount)? "" : "Required: Minimum friend count " + MIN_FRIEND_COUNT };
}

export default {
    getUsers,
    getUserById,
    createUser,
    updateUser,
    userLogin,
    userLogout,
    isUserLogged,
    getUserPosition,
    updateUserPosition
};
