import React from "react";
import { withRouter } from "react-router-dom";
import { Grid, Typography, TextField, Button } from "@material-ui/core";
import userService from "../../userService";

import "./styles.css";

class Login extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = { username: "", password: "", errorMessage: undefined };
  }

  handleLoginClicked = async e => {
    const { history } = this.props;
    const { username, password } = this.state;
    e.preventDefault();

    if (username && password) {
      this.setState({ errorMessage: undefined });
      const result = await userService.login(username, password);

      if (result.error) {
        return this.setState({ errorMessage: result.error });
      }

      history.push("/trips");
    }
  };

  render() {
    const { username, password, errorMessage } = this.state;

    return (
      <Grid container direction="column" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
        <Typography variant="h5">Sign in</Typography>
        <form className="login-form" noValidate autoComplete="off">
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            autoFocus
            id="username"
            name="username"
            label="Usuario"
            placeholder="admin"
            value={username}
            onChange={evt => this.setState({ username: evt.target.value })}
          />
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="password"
            name="password"
            label="Clave"
            type="password"
            placeholder="admin"
            autoComplete="current-password"
            value={password}
            onChange={evt => this.setState({ password: evt.target.value })}
          />
          <Button disabled={!username || !password} type="submit" fullWidth variant="contained" color="primary" onClick={this.handleLoginClicked}>
            Log In
          </Button>
          {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
        </form>
      </Grid>
    );
  }
}

export default withRouter(Login);
