import Express from 'express';
import jwt from 'jsonwebtoken';
import { IUser } from '../models';
import ICreateUserData from '../models/ICreateUserData';
import { userService } from '../services';
import { tripsService } from '../services';
import { map } from 'bluebird';

const secret = 'ALOHOMORA';

async function getUsers(req: Express.Request, res: Express.Response) {
  try {
    const users = await userService.getUsers();
    res.json(users);
  } catch (e) {
    res
      .status(409)
      .json({ message: e.message })
      .send();
  }
}

async function getUserById(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    const user = await userService.getUserById(userId);
    res
      .status(200)
      .json(user)
      .send();
  } catch (e) {
    res.sendStatus(409).json({ message: e });
  }
}

async function updateUser(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    const userData: Partial<IUser> = req.body;
    const updatedUser = await userService.updateUser(userId, userData);
    res.status(200).json(updatedUser);
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
    const userPos = await userService.getUserPosition(req.params.userId);
    res.status(200).json(userPos);
  } catch (e) {
    res
      .status(409)
      .json({ message: e.message })
      .send();
  }
}

async function updateUserPosition(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    const position = req.body;
    const user = await userService.updateUserPosition(userId, position);
    res.status(200).json(user);
  } catch (e) {
    res
      .status(409)
      .json({ message: e.message })
      .send();
  }
}

async function getUserIdIfLoggedWithValidCredentials(req: Express.Request, res: Express.Response) {
  try {
    let userId = '';

    if (req.headers.authorization === undefined) {
      res
        .status(403)
        .json({ message: 'Debe proveer credenciales de autenticación!' })
        .send();
      return '';
    }

    const token = req.headers.authorization.replace('Bearer ', '');
    try {
      const signature = jwt.verify(token, secret);
      userId = (signature as any).id;
      console.log('DECODE USER ID' + userId);
    } catch (e) {
      res
        .status(401)
        .json({ message: 'Credenciales inválidas!' })
        .send();
      return '';
    }
    const isUserLoggedIn = await userService.isUserLogged(userId);
    console.log('CHECK USER LOGGED IN ' + isUserLoggedIn);
    if (!isUserLoggedIn) {
      res
        .status(403)
        .json({ message: 'El usuario debe estar loggeado para realizar esta operación!' })
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

async function deleteUser(req: Express.Request, res: Express.Response) {
  try {
    const fbId = req.params.fbId;
    await userService.deleteUser(fbId);
    return res.sendStatus(201);
  } catch (e) {
    res.sendStatus(500);
  }
}

async function submitUserReview(req: Express.Request, res: Express.Response) {
  try {
    //TODO: this should check the user is not rating him/herself
    //and that rater is user (not driver) who just completed a trip.
    //TODO: missing comment/actual review. This will probably require
    //extracting this to a different table.
    const raterUserId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (raterUserId.length <= 0) {
      return;
    }

    const ratedUserId = req.body.userId;
    const tripId = req.body.tripId;
    const review = req.body.review; //{stars: 5, comment: "..."}
    await userService.submitUserReview(raterUserId, ratedUserId, tripId, review);
    return res.sendStatus(200);
  } catch (e) {
    res.sendStatus(500);
  }
}

async function getUserRating(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }
    const ratedUserId = req.params.userId;
    const reviews: Array<any> = await userService.getUserReviews(ratedUserId);
    let avgRating = 0;
    if (reviews != undefined && reviews.length > 0) {
      let totalStars = reviews.reduce((reviewA: any, reviewB: any) => reviewA.stars + reviewB.stars, { stars: 0 });
      avgRating = totalStars / reviews.length;
    }
    return res.status(200).json({ rating: avgRating });
  } catch (e) {
    res.sendStatus(500);
  }
}

async function createUser(req: Express.Request, res: Express.Response) {
  try {
    const data = req.body;
    const userData: ICreateUserData = req.body;
    userData.images = data.images.map((img: any) => ({
      fileName: img.fileName,
      fileContent: ImgBase64StringToBuffer(img.fileContent),
    }));

    // TODO #2: if user is Client state should be valid if fb account check successful
    // otherwise user approval should remain as PENDING.

    // Create user will create a new user if facebook account is valid
    await userService.createUser(userData);
    const loggedUserAndToken = await loginUserWithFbId(userData.fbId);
    res.send(loggedUserAndToken);
  } catch (err) {
    res
      .status(409)
      .json({ message: err.message })
      .send();
  }
}

function ImgBase64StringToBuffer(img: string) {
  return Buffer.from(img, 'base64');
}

async function userLogin(req: Express.Request, res: Express.Response) {
  try {
    const fbId = req.params.fbId;
    const userAndToken = await loginUserWithFbId(fbId);
    res.status(200).send(userAndToken);
  } catch (e) {
    res
      .status(409)
      .json({ message: e.message })
      .send();
  }
}

async function loginUserWithFbId(fbId: string) {
  const userId = await userService.getUserByFbId(fbId);

  if (!userId) {
    throw new Error('El usuario que desea ingresar no existe! Por favor regístrese!');
  }

  const data = { id: userId };
  const token = jwt.sign(data, secret);
  const user = await userService.userLogin(userId);
  return { token, user };
}

async function userLogout(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    await userService.userLogout(userId);
    res.status(200).send();
  } catch (e) {
    res
      .status(409)
      .json({ message: e.message })
      .send();
  }
}

async function getUserLastTrip(req: Express.Request, res: Express.Response) {
  try {
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    const trip = await tripsService.getUserLastTrip(userId);

    res
      .status(200)
      .json(trip)
      .send();
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
  getUserLastTrip,
  createUser,
  submitUserReview,
  getUserRating,
  updateUser,
  getUserPosition,
  updateUserPosition,
  userLogin,
  userLogout,
  deleteUser,
};
