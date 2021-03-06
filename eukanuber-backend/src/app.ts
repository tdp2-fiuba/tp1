require("dotenv").config();
import bodyParser from "body-parser";
import cors from "cors";
import Express from "express";
import { loginController, statusController, tripsController, usersController } from "./controllers";
import { requestLoggerMiddleware } from "./middlewares";
const app = Express();
const port = process.env.PORT || 3000;

// Initial setup
app.use(bodyParser.json({ limit: "50mb" }));
app.use(bodyParser.urlencoded({ limit: "50mb", extended: true, parameterLimit: 50000 }));

app.use(cors());
app.use(requestLoggerMiddleware);

// Users endpoints
app.get("/users/all", usersController.getUsers);
app.post("/users/register", usersController.createUser);
app.get("/users", usersController.getUserById);
app.get("/users/:userId", usersController.getUserById);
app.put("/users", usersController.updateUser);
app.post("/users/review", usersController.submitUserReview);
app.get("/users/:userId/rating", usersController.getUserRating);
app.get("/users/:userId/reviews", usersController.getUserReviews);
app.get("/users/trip/lastTrip", usersController.getUserLastTrip);
app.get("/users/:userId/status", usersController.getUserStatus);
app.get("/users/position/:userId", usersController.getUserPosition);
app.put("/users/position", usersController.updateUserPosition);
app.post("/users/login/:fbId", usersController.userLogin);
app.delete("/users/:fbId", usersController.deleteUser);
app.post("/users/logout", usersController.userLogout);
app.get("/users/drivers/pendingTrips", usersController.getDriverPendingTrips);
app.get("/users/trip/finishedTrips", usersController.getFinishedTrips);
app.put("/users/firebase", usersController.newFirebaseToken);

// Trips endpoints
app.get("/trips", tripsController.getAll);
app.get("/trips/:id", tripsController.getById);
app.get("/trips/:id/full", tripsController.getFullById);
app.post("/trips", tripsController.createTrip);
app.put("/trips/:id", tripsController.updateTrip);
app.post("/trips/:id/accept", tripsController.acceptTrip);
app.post("/trips/:id/reject", tripsController.rejectTrip);
app.post("/trips/:id/cancel", tripsController.rejectTrip);
app.post("/trips/routes", tripsController.getRoute);

// Backend endpoints
app.post("/login", loginController.loginUser);

// Status endpoints
app.get("/ping", statusController.ping);
app.get("/ready", statusController.ready);
app.get("/status", statusController.status);

app.listen(port, () => {
  console.log(`Example app listening on http://localhost:${port}!`);
});
