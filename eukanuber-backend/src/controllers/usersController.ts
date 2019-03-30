import Express from "express";

function getUsers(req: Express.Request, res: Express.Response) {
  res.send([]);
}

export default { getUsers };
