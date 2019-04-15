import Express from "express";
import { IUser } from "../models";
import { userService } from "../services";
import ICreateUserData  from "../models/ICreateUserData";


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
    const userData: ICreateUserData = req.body;
    const newUser = await userService.createUser(userData);
    res.json(newUser);
  } catch (e) {
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

export default { 
  getUsers,
  getUserById,
  createUser,
  updateUser,
  getUserPosition,
  updateUserPosition
};
