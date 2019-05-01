import Express from "express";
import { IUser } from "../models";
import { userService } from "../services";
import ICreateUserData  from "../models/ICreateUserData";

var fs = require('fs');
const jwt = require('jsonwebtoken');

var secret = "ALOHOMORA";

async function getUsers(req: Express.Request, res: Express.Response) {
  try {
    const users = await userService.getUsers();
    res.json(users);
  } catch (e) {
    res.status(500).json({message: e}).send();
  }
}

async function getUserById(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const user = await userService.getUserById(userId);
    res.json(user);
  } catch (e) {
    res.status(500).json({message: e}).send();
  }
}

async function updateUser(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const userData: Partial<IUser>  = req.body;
    const updatedUser = await userService.updateUser(userId, userData);
    res.json(updatedUser);
  } catch (e) {
    res.status(500).json({message: e}).send();
  }
}

async function getUserPosition(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const userPos = await userService.getUserPosition(userId);
    res.json(userPos);
  } catch (e) {
    res.status(500).json({message: e}).send();
  }
}

async function updateUserPosition(req: Express.Request, res: Express.Response) {
  try {
    const userId = req.params.id;
    const position = req.body;
    const user = await userService.updateUserPosition(userId, position)
    res.json(user);
  } catch (e) {
    res.status(500).json({message: e}).send();
  }
}

async function createUser(req: Express.Request, res: Express.Response) {
  try {
    const data = req.body;
    let userData: ICreateUserData = req.body;
    userData.images = data.images.map(function(img: any) { return { fileName: img.fileName, file: ImgBase64StringToBuffer(img.file)} });
    //TODO: validate fb account
    //TODO #2: if user is Passenger state should be valid if fb account check successful
    //otherwise user approval should remain as PENDING.

    const newUser = await userService.createUser(userData);
    res.status(201).json(newUser);
  } catch (e) {
    res.status(500).json({message: e}).send();
  }
}

function ImgBase64StringToBuffer(img: string) {
  return Buffer.from(img, "base64");
}

async function userLogin(req: Express.Request, res: Express.Response) {
  try{
    const id = req.params.id;
    const data = { id: id, name: req.body.firstName, lastName: req.body.lastName, fbId: req.body.fbId };
    //TODO: check user has registered.
    const token = jwt.sign(data, secret, {});
    userService.userLogin(id);

    res.status(200).send({ token });
  } catch (e) {
    res.status(500).json({message: e}).send();
  }
}

async function userLogout(req: Express.Request, res: Express.Response) {
  try{
    const id = req.params.id;
    //TODO: check user has registered and validate credentials.
    userService.userLogout(id);
    
    res.status(200).send();
  } catch (e) {
    res.status(500).json({message: e}).send();
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
  userLogout
};
