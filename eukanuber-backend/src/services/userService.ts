import Knex from 'knex';
import db from '../db/db';
import Express from 'express';
import { IUser } from '../models';
import ICreateUserData from '../models/ICreateUserData';
import facebookService from './facebookService';

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
  let users = await db('users')
    .innerJoin('userMedia', 'users.id', '=', 'userMedia.userId')
    .where('users.id', id)
    .select();

  if (!users) {
    return undefined;
  }

  let userData = users[0];
  let userImages = users.map((u: any) => ({ fileName: u.fileName, fileContent: Buffer.from(u.fileContent).toString('base64') }));

  const user = {
    id: id,
    userType: userData.userType,
    firstName: userData.firstName,
    lastName: userData.lastName,
    position: userData.position,
    state: userData.state,
    images: userImages,
    loggedIn: userData.loggedIn,
  };

  return user;
}

async function getUserByFbId(fbId: string) {
  try {
    let user = await db
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

const createTransaction = () => new Promise(resolve => db.transaction(resolve));

async function createUser(newUser: ICreateUserData) {
  //TODO: ver por que esto no me deja castear usando ...newUser
  const user = {
    userType: newUser.userType,
    firstName: newUser.firstName,
    lastName: newUser.lastName,
    position: newUser.position,
    fbAccessToken: newUser.fbAccessToken,
    fbId: newUser.fbId,
  };

  const accountValidation = await validateFacebookAccount(newUser.fbId, newUser.fbAccessToken);

  if (!accountValidation.validAccount) {
    throw new Error(accountValidation.message);
  }

  const transaction = <Knex.Transaction>await createTransaction();

  try {
    const userInsertResult = await transaction('users')
      .insert(user)
      .returning('id');
    const userId = userInsertResult[0];
    const fields = newUser.images.map(img => ({ userId, fileName: img.fileName, fileContent: img.file }));

    await transaction('userMedia').insert(fields);
    await transaction.commit();
    return { userId: userId, ...user } as any;
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
    .where({ fbId: fbId });
}

async function validateFacebookAccount(fbId: string, fbAccessToken: string) {
  const accountData = await facebookService.getFacebookFriendCount(fbAccessToken);
  const validAccount: boolean = fbId == accountData.id && accountData.friends.summary.total_count >= MIN_FRIEND_COUNT;
  console.log(`FB VALIDATION WAS ${validAccount} WITH FB DATA ${JSON.stringify(accountData)}.`);
  return {
    validAccount: validAccount,
    message: validAccount ? '' : 'Cuenta de facebook invalida!',
  };
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
  getUserPosition,
  updateUserPosition,
  deleteUser,
};
