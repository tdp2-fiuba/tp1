import React from "react";
import { Grid, Typography, Button } from "@material-ui/core";
import backendService from "../../backendService";

import "./styles.css";

export default class Home extends React.PureComponent {
  handleButtonClick = async () => {
    const result = await backendService.getTrips();
    console.log(result);
  };

  render() {
    return (
      <Grid container direction="row" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
        Trips
        <Button variant="contained" color="primary" onClick={this.handleButtonClick}>
          Search
        </Button>
      </Grid>
    );
  }
}
