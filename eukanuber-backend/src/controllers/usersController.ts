import Express from "express";
import { IUser } from "../models";
import { userService } from "../services";
import ICreateUserData  from "../models/ICreateUserData";
import ICreateDriverData  from "../models/ICreateDriverData";
import { map } from "bluebird";

var fs = require('fs');
const BASE64_PREFIX = /^data:image\/\w+;base64,/;

async function getUsers(req: Express.Request, res: Express.Response) {
  try {
    const users = await userService.getUsers();
    res.json(users);
  } catch (e) {
    res.status(500).send(e);
  }
}

async function getUserById(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const user = await userService.getUserById(userId);
    res.json(user);
  } catch (e) {
    res.status(500).send(e);
  }
}

async function createUser(req: Express.Request, res: Express.Response) {
  try {
    console.log("HI 1");
    const userData: ICreateUserData = req.body;
    const newUser = await userService.createUser(userData);
    res.json(newUser);
  } catch (e) {
    console.error(e);
    res.status(500).send(e);
  }
}

async function updateUser(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const userData: Partial<IUser>  = req.body;
    const updatedUser = await userService.updateUser(userId, userData);
    res.json(updatedUser);
  } catch (e) {
    res.status(500).send(e);
  }
}

async function getUserPosition(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const userPos = await userService.getUserPosition(userId);
    res.json(userPos);
  } catch (e) {
    res.status(500).send(e);
  }
}

async function updateUserPosition(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const position = req.body;
    const user = await userService.updateUserPosition(userId, position)
    res.json(user);
  } catch (e) {
    res.status(500).send(e);
  }
}

//drivers

async function createDriverUser(req: Express.Request , res: Express.Response) {
  try {
    const data = req.body;
    const userData: ICreateUserData = req.body;
    const driverData: ICreateDriverData = {
      user: userData,
      images: data.images.map(function(img: any) { return { fileName: img.fileName, file: ImgBase64StringToBuffer(img.file)} })
    }
    const newDriver = await userService.createDriver(driverData);
    res.status(201).json(newDriver);
  } catch (e) {
    res.status(500).send(e);
  }
}

function ImgBase64StringToBuffer(img: string) {
  return Buffer.from(img, "base64");
}


export default { 
  getUsers,
  getUserById,
  createUser,
  createDriverUser,
  updateUser,
  getUserPosition,
  updateUserPosition
};
