import Express from "express";
import { base64encode } from "nodejs-base64";
import dbRepository from "../db/db";

interface ILoginData {
  username: string;
  password: string;
}

async function loginUser(req: Express.Request, res: Express.Response) {
  const loginData: ILoginData = req.body;

  if (!loginData || !loginData.username || !loginData.password) {
    return res.status(400).json({ error: "You must provide a username and a password" });
  }

  const encryptedPassword = base64encode(loginData.password);
  const userFound = await dbRepository
    .table("admins")
    .where("username", loginData.username)
    .andWhere("password", encryptedPassword)
    .select()
    .first();

  if (userFound) {
    return res.json({ id: userFound.id, username: userFound.username, token: "31bf9cabd66545538edd8d2c2ca8f0e3" });
  }

  return res.status(404).json({ error: "Invalid username or password" });
}

export default {
  loginUser
};
