import Express from "express";
import jwt from "jsonwebtoken";
import { IUser, TripStatus } from "../models";
import ICreateUserData from "../models/ICreateUserData";
import { userService } from "../services";
import { tripsService } from "../services";

const secret = "ALOHOMORA";

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
    const user = await userService.getUserById(userId);
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
        const userPos = await userService.getUserPosition(req.params.userId);
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
    let userId = "";

    if (req.headers.authorization === undefined) {
      res
        .status(403)
        .json({ message: "Debe proveer credenciales de autenticación!" })
        .send();
      return "";
    }

    const token = req.headers.authorization.replace("Bearer ", "");
    try {
      const signature = jwt.verify(token, secret);
      userId = (signature as any).id;
      console.log("DECODE USER ID" + userId);
    } catch (e) {
      res
        .status(401)
        .json({ message: "Credenciales inválidas!" })
        .send();
      return "";
    }
    const isUserLoggedIn = await userService.isUserLogged(userId);
    console.log("CHECK USER LOGGED IN " + isUserLoggedIn);
    if (!isUserLoggedIn) {
      res
        .status(403)
        .json({ message: "El usuario debe estar loggeado para realizar esta operación!" })
        .send();
      return "";
    }
    return userId;
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
    return "";
  }
}

async function deleteUser(req: Express.Request, res: Express.Response) {
  try {
    const fbId = req.params.fbId;
    console.log("DELETE USER " + fbId);
    await userService.deleteUser(fbId);
    return res
      .status(201)
      .json({ message: "SUCCESS" })
      .send();
  } catch (e) {
    console.log(e);
    res.sendStatus(500);
  }
}

async function createUser(req: Express.Request, res: Express.Response) {
  try {
    const data = req.body;
    const userData: ICreateUserData = req.body;
    userData.images = data.images.map((img: any) => ({ fileName: img.fileName, fileContent: ImgBase64StringToBuffer(img.fileContent) }));

    // TODO #2: if user is Passenger state should be valid if fb account check successful
    // otherwise user approval should remain as PENDING.

    // Create user will create a new user if facebook account is valid
    await userService.createUser(userData);
    const loggedUserAndToken = await loginUserWithFbId(userData.fbId);
    res.send(loggedUserAndToken);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

function ImgBase64StringToBuffer(img: string) {
  return Buffer.from(img, "base64");
}

async function userLogin(req: Express.Request, res: Express.Response) {
  try {
    console.log("LOGIN");
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

  if (!userId || userId == undefined) {
    // return 409 if user was not found:
    throw new Error("El usuario que desea ingresar no existe! Por favor regístrese!");
  }
  // User exists, so generate token and send it:
  const data = { id: userId };
  const token = jwt.sign(data, secret);
  console.log("USER LOGGED IN " + userId);
  const user = await userService.userLogin(userId);
  return { token, user };
}

async function userLogout(req: Express.Request, res: Express.Response) {
  try {
    console.log("LOGOUT");
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }
    await userService.userLogout(userId);
    console.log("USER LOGGED OUT " + userId);
    res.status(200).send();
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}
async function activeTripId(req: Express.Request, res: Express.Response) {
  try {
    console.log("GET ACTIVE TRIP ID");
    const userId = await getUserIdIfLoggedWithValidCredentials(req, res);
    if (userId.length <= 0) {
      return;
    }

    const tripId = await tripsService.getTripByUserAndStatus(userId, TripStatus.IN_TRAVEL);

    res
      .status(200)
      .json({ tripId })
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
  createUser,
  updateUser,
  getUserPosition,
  updateUserPosition,
  userLogin,
  userLogout,
  deleteUser,
  activeTripId
};
