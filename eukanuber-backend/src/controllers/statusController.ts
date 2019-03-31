import Express from "express";

function ping(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  res.sendStatus(200);
  next();
}

function ready(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  res.sendStatus(200);
  next();
}

function status(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  const status = {
    version: "TBD"
  };

  res.json(status);
  next();
}

export default {
  ping,
  ready,
  status
};