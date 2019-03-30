import express from "express";
import { usersController, statusController } from "./controllers";

const app = express();
const port = process.env.PORT || 3000;

// Users endpoints
app.get("/users", usersController.getUsers);

// Trips endpoints
// TODO

// Status endpoints
app.get("/ping", statusController.ping);
app.get("/ready", statusController.ready);
app.get("/status", statusController.status);

app.listen(port, () => {
  console.log(`Example app listening on port ${port}!`);
});
