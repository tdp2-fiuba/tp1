import Express from "express";

async function quoteTrip(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  const result = {
    price: "100 USD",
    routes: [] as any
  };

  res.json(result);
  next();
}

export default {
  quoteTrip
};
