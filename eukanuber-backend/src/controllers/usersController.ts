import Express from "express";

function getUsers(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  console.log("6");
  res.send("pepe6");
  next();
}

export default { getUsers };
