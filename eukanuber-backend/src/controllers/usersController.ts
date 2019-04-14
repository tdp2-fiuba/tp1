import Express from "express";
import { IUser } from "../models";
import { userService } from "../services";

function getUsers(req: Express.Request, res: Express.Response) {
  res.send(userService.getUsers());
}




export default { getUsers };
