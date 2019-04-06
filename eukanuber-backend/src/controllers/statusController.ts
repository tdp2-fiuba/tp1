import Express from "express";
import shell from "shelljs";

function ping(req: Express.Request, res: Express.Response) {
  res.sendStatus(200);
}

function ready(req: Express.Request, res: Express.Response) {
  res.sendStatus(200);
}

function status(req: Express.Request, res: Express.Response) {
  const version = shell.exec("git describe --tag --always").stdout.trim();
  res.json({ version });
}

export default {
  ping,
  ready,
  status
};
