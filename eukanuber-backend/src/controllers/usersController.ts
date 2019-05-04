import Express from 'express';
import { IUser } from '../models';
import { userService } from '../services';
import ICreateUserData from '../models/ICreateUserData';

var fs = require('fs');
const jwt = require('jsonwebtoken');

var secret = 'ALOHOMORA';

async function getUsers(req: Express.Request, res: Express.Response) {
  try {
    const users = await userService.getUsers();
    res.json(users);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

async function getUserById(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }
    let user = await userService.getUserById(userId);
    if (user == undefined) {
      res
        .status(200)
        .json({})
        .send();
    }
    res
      .status(200)
      .json(user)
      .send();
  } catch (e) {
    res.sendStatus(500).json({ message: e });
  }
}

async function updateUser(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }
    const userData: Partial<IUser> = req.body;
    const updatedUser = await userService.updateUser(userId, userData);
    res.status(201).json(updatedUser);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

async function getUserPosition(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }
    const userPos = await userService.getUserPosition(userId);
    res.status(200).json(userPos);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

async function updateUserPosition(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }
    const position = req.body;
    const user = await userService.updateUserPosition(userId, position);
    res.status(201).json(user);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

async function getUserIdIfLoggedWithValidCredentials(req: Express.Request, res: Express.Response) {
  try {
    let signature, userId;
    if (req.headers.authorization == undefined) {
      res
        .status(403)
        .json({ message: 'Must provide authorization credentials!' })
        .send();
      return '';
    }

    const token = req.headers.authorization.replace('Bearer ', '');
    try {
      signature = jwt.verify(token, secret);
      userId = signature.id;
      console.log('DECODE USER ID' + userId);
    } catch (e) {
      res
        .status(401)
        .json({ message: 'Invalid credentials!' })
        .send();
      return '';
    }
    const isUserLoggedIn = await userService.isUserLogged(userId);
    console.log('CHECK USER LOGGED IN ' + isUserLoggedIn);
    if (!isUserLoggedIn) {
      res
        .status(403)
        .json({ message: 'User must be logged in to perform this operation!' })
        .send();
      return '';
    }
    return userId;
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
    return '';
  }
}

async function createUser(req: Express.Request, res: Express.Response) {
  try {
    const data = req.body;
    let userData: ICreateUserData = req.body;
    userData.images = data.images.map(function(img: any) {
      return { fileName: img.fileName, file: ImgBase64StringToBuffer(img.file) };
    });
    //TODO #2: if user is Passenger state should be valid if fb account check successful
    //otherwise user approval should remain as PENDING.

    //Create user will create a new user if facebook account is valid
    const user = await userService.createUser(userData);
    res.send(user);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

function ImgBase64StringToBuffer(img: string) {
  return Buffer.from(img, 'base64');
}

async function userLogin(req: Express.Request, res: Express.Response) {
  try {
    console.log('LOGIN');
    const fbId = req.params.fbId;
    const userId = await userService.getUserByFbId(fbId);

    if (!userId || userId == undefined) {
      //return 409 if user was not found:
      res.status(409).send({ message: 'User not found! Please register!' });
      return;
    }
    //User exists, so generate token and send it:
    const data = { id: userId };
    const token = jwt.sign(data, secret);
    console.log('USER LOGGED IN ' + userId);
    await userService.userLogin(userId);

    res.status(200).send({ token });
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

async function userLogout(req: Express.Request, res: Express.Response) {
  try {
    console.log('LOGOUT');
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }
    await userService.userLogout(userId);
    console.log('USER LOGGED OUT ' + userId);
    res.status(200).send();
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

export default {
  getUsers,
  getUserById,
  createUser,
  updateUser,
  getUserPosition,
  updateUserPosition,
  userLogin,
  userLogout,
};
