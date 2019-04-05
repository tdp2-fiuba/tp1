import express from "express";
import { quoteController, statusController, tripsController, usersController } from "./controllers";

const app = express();
const port = process.env.PORT || 3000;

// Users endpoints
app.get("/users", usersController.getUsers);

// Quotes endpoints
app.post("/quote/trip", quoteController.quoteTrip);

// Trips endpoints
app.get("/trips", tripsController.getAll);
app.get("/trips/:id", tripsController.getById);

// Status endpoints
app.get("/ping", statusController.ping);
app.get("/ready", statusController.ready);
app.get("/status", statusController.status);

app.listen(port, () => {
  console.log(`Example app listening on port ${port}!`);
});
