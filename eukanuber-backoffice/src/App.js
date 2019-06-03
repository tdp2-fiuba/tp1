import React from "react";
import { BrowserRouter as Router, Route } from "react-router-dom";
import { Grid, Typography } from "@material-ui/core";
import Trips from "./components/Trips";
import Users from "./components/Users";
import Login from "./components/Login";
import PrivateRoute from "./components/PrivateRoute";

export default class App extends React.PureComponent {
  render() {
    return (
      <Router>
        <>
          <Grid container direction="column" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
            <Typography variant="h2">
              <span>Eukanuber </span>
              <img className="logo" src="images/logo.jpg" alt="Eukanuber logo" />
            </Typography>
          </Grid>
          <Route exact path="/login" component={Login} />
          <PrivateRoute exact path="/users" component={Users} />
          <PrivateRoute exact path="/trips" component={Trips} />
          <PrivateRoute exact path="/" component={Trips} />
        </>
      </Router>
    );
  }
}
