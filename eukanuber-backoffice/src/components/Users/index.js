import React from "react";
import { Link } from "react-router-dom";
import { Grid, Typography, Button } from "@material-ui/core";

import "./styles.css";

export default class Home extends React.PureComponent {
  render() {
    return (
      <Grid container direction="row" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
        Users
      </Grid>
    );
  }
}
