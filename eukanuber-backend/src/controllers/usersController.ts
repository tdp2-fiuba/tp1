import Express from "express";
import { IUser } from "../models";
import { userService } from "../services";
import ICreateUserData  from "../models/ICreateUserData";
import ICreateDriverData  from "../models/ICreateDriverData";

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

async function createDriverUser(req: Express.Request , res: Express.Response) {
  try {
    const data = req.body;
    const userData: ICreateUserData = req.body;
    const driverData: ICreateDriverData = {
      user: userData,
      images: [
        {
          fileName: "license",
          file: base64ImgToBuffer(data['license'])
        },
        {
          fileName: "insurance",
          file: base64ImgToBuffer(data['insurance'])
        },
        {
          fileName: "vehicle",
          file: base64ImgToBuffer(data['vehicle'])
        }
      ]
    }
    const newDriver = await userService.createDriver(driverData);
    res.json(newDriver);
  } catch (e) {
    res.status(500).send(e);
  }
}

function base64ImgToBuffer(img: any) {
  return Buffer.from(img.replace(BASE64_PREFIX, ""), 'base64')
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

export default { 
  getUsers,
  getUserById,
  createUser,
  createDriverUser,
  updateUser,
  getUserPosition,
  updateUserPosition
};
