import React from "react";
import { BrowserRouter as Router, Route, Redirect } from "react-router-dom";
import Home from "./Home";
import Login from "./Login";
import UserInfo from "./UserInfo";
import userService from "./userService";

import { Grid, Typography } from "@material-ui/core";

const PrivateRoute = ({ component: Component, ...rest }) => (
  <Route
    {...rest}
    render={props => {
      const userInfo = userService.getUserInfo();

      if (!userInfo) {
        return <Redirect to={{ pathname: "/login" }} />;
      }

      return (
        <>
          <UserInfo />
          <Component {...props} />
        </>
      );
    }}
  />
);

export default class App extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = { isLoggedIn: !!userService.getUserInfo() };
  }

  handleLogoutClick = () => {
    userService.logout();
    this.setState({ isLoggedIn: false });
  };

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
          <PrivateRoute exact path="/" component={Home} />
          <Route path="/login" component={Login} />
        </>
      </Router>
    );
  }
}
