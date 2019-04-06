import Express from "express";

function ping(req: Express.Request, res: Express.Response) {
  res.sendStatus(200);
}

function ready(req: Express.Request, res: Express.Response) {
  res.sendStatus(200);
}

function status(req: Express.Request, res: Express.Response) {
  const statusInfo = { version: "TBD" };
  res.json(statusInfo);
}

export default {
  ping,
  ready,
  status
};
