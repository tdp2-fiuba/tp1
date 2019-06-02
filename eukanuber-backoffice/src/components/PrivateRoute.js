import React from "react";
import { Route, Redirect, Link } from "react-router-dom";
import { Grid } from "@material-ui/core";
import UserInfo from "./UserInfo";
import userService from "../userService";

export default ({ component: Component, ...rest }) => (
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
          <Grid container direction="row" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
            <Link to="/trips" style={{ marginRight: 25 }}>
              Viajes
            </Link>
            <Link to="/users">Usuarios</Link>
          </Grid>
          <Component {...props} />
        </>
      );
    }}
  />
);
