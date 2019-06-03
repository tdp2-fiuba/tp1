import React from "react";
import { withRouter } from "react-router-dom";
import userService from "../../userService";
import { Grid, Typography, Button } from "@material-ui/core";
import "./styles.css";

class LoggedUserInfo extends React.PureComponent {
  handleLogoutClick = () => {
    const { history } = this.props;
    userService.logout();
    history.replace("/");
  };

  render() {
    const userInfo = userService.getUserInfo();

    if (!userInfo) {
      return null;
    }

    return (
      <Grid container direction="row" justify="flex-end" alignItems="center" style={{ position: "absolute", right: 50 }}>
        <Typography variant="body2" style={{ paddingRight: 10 }}>
          {userInfo.username}
        </Typography>
        <Button variant="outlined" onClick={this.handleLogoutClick}>
          Logout
        </Button>
      </Grid>
    );
  }
}

export default withRouter(LoggedUserInfo);
