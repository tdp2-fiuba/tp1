import db from "../db/db";
import { IUser } from "../models";
import ICreateUserData  from "../models/ICreateUserData";

interface IPosition {
    lat: string,
    lng: string
}

async function getUsers() {
    const rows = await db
    .table("users")
    .select();
    console.log(rows);
    return rows;
}

async function getUserById(id: string) {
  const user = await db
    .table("users")
    .where("id", id)
    .select()
    .first();

  if (!user) {
    return undefined;
  }

  return user;
}

async function createUser(user: ICreateUserData) {
    const newUser = {
        ...user
    }

    const userId = ((await db
        .table("users")
        .returning("id")
        .insert(newUser)) as string[])[0];
    
    return {
        ...newUser,
        id: userId
    } as any;
    
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

export default {
    getUsers,
    getUserById,
    createUser,
    updateUser,
    getUserPosition,
    updateUserPosition
};
