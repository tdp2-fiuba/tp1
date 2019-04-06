import { NextFunction, Request, Response } from "express";

function logRequest(req: Request, res: Response) {
  const date = new Date().toISOString();
  const method = req.method;
  const url = req.originalUrl || req.url;
  const body = JSON.stringify(req.body);
  const bodyMessage = body && `[Body: ${body}]`;

  const message = `[Request] [${date}] [${method} ${url}] ${bodyMessage}`.trim();
  console.info(message);
}

function logResponse(req: Request, res: Response) {
  const date = new Date().toISOString();
  const method = req.method;
  const url = req.originalUrl || req.url;
  const contentLength = res.get("Content-Length") || 0;
  const statusCode = res.statusCode;

  const message = `[Response] [${date}] [${method} ${url}] [Status: ${statusCode}] [Content-length: ${contentLength}]`;
  console.info(message);
}

const requestLoggerMiddleware = (req: Request, res: Response, next: NextFunction) => {
  logRequest(req, res);
  res.on("finish", () => logResponse(req, res));

  next();
};

export default requestLoggerMiddleware;
