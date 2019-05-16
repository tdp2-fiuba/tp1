import bodyParser from 'body-parser';
import Express from 'express';
import { statusController, tripsController, usersController } from './controllers';
import { requestLoggerMiddleware } from './middlewares';
const app = Express();
const port = process.env.PORT || 3000;

// Initial setup
app.use(bodyParser.json({ limit: '50mb' }));
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true, parameterLimit: 50000 }));

app.use(requestLoggerMiddleware);

// Users endpoints
app.get('/users/all', usersController.getUsers);
app.post('/users/register', usersController.createUser);
app.get('/users', usersController.getUserById);
app.put('/users', usersController.updateUser);
app.put('/users/review', usersController.submitUserReview);
app.get('/users/:userId/rating', usersController.getUserRating);
app.get('/users/:userId/reviews', usersController.getUserReviews);
app.get('/users/lastTrip', usersController.getUserLastTrip);
app.get('/users/position/:userId', usersController.getUserPosition);
app.put('/users/position', usersController.updateUserPosition);
app.post('/users/login/:fbId', usersController.userLogin);
app.delete('/users/:fbId', usersController.deleteUser);
app.post('/users/logout', usersController.userLogout);

//app.post('/users/closest', tr.getProspectiveDrivers);

// Trips endpoints
app.get('/trips', tripsController.getAll);
app.get('/trips/:id', tripsController.getById);
app.post('/trips', tripsController.createTrip);
app.put('/trips/:id', tripsController.updateTrip);
app.post('/trips/:id/accept', tripsController.acceptTrip);
app.post('/trips/:id/reject', tripsController.rejectTrip);
app.post('/trips/routes', tripsController.getRoute);

// Status endpoints
app.get('/ping', statusController.ping);
app.get('/ready', statusController.ready);
app.get('/status', statusController.status);

app.listen(port, () => {
  console.log(`Example app listening on http://localhost:${port}!`);
});
