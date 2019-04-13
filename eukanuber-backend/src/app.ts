import bodyParser from "body-parser";
import Express from "express";
import { statusController, tripsController, usersController } from "./controllers";
import { requestLoggerMiddleware } from "./middlewares";

const app = Express();
const port = process.env.PORT || 3000;

// Initial setup
app.use(bodyParser.json());
app.use(requestLoggerMiddleware);

// Users endpoints
app.get("/users", usersController.getUsers);

// Trips endpoints
app.get("/trips", tripsController.getAll);
app.get("/trips/:id", tripsController.getById);
app.post("/trips", tripsController.createTrip);
app.put("/trips/:id", tripsController.updateTrip);

// Status endpoints
app.get("/ping", statusController.ping);
app.get("/ready", statusController.ready);
app.get("/status", statusController.status);

app.listen(port, () => {
  console.log(`Example app listening on port ${port}!`);
});
