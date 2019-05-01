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
app.post("/users", usersController.createUser);
app.get("/users/:id", usersController.getUserById);
app.put("/users/:id", usersController.updateUser);
app.get("/users/:id/position", usersController.getUserPosition);
app.put("/users/:id/position", usersController.updateUserPosition);
app.post("/users/:id/login", usersController.userLogin);
app.post("/users/:id/logout", usersController.userLogout);


// Trips endpoints
app.get("/trips", tripsController.getAll);
app.get("/trips/:id", tripsController.getById);
app.post("/trips", tripsController.createTrip);
app.put("/trips/:id", tripsController.updateTrip);
app.post("/trips/routes", tripsController.getRoute);

// Status endpoints
app.get("/ping", statusController.ping);
app.get("/ready", statusController.ready);
app.get("/status", statusController.status);



app.listen(port, () => {
  console.log(`Example app listening on http://localhost:${port}!`);
});
