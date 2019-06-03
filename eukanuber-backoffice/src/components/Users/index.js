import React from "react";
import { Grid, Button, Table, TableHead, TableBody, TableFooter, TableCell, TableRow, Paper, TextField } from "@material-ui/core";
import backendService from "../../backendService";
import "./styles.css";

export default class Home extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = { id: undefined, name: undefined, userType: undefined, state: undefined };
  }

  handleButtonClick = async () => {
    const { id, name, userType, state } = this.state;
    const results = await backendService.getUsers();
    console.log(results);

    if (!results.length) {
      return;
    }

    const filteredResults = (results || [])
      .filter(item => !id || item.id === id)
      .filter(item => !name || `${item.firstName} ${item.lastName}`.toLowerCase().indexOf(name.toLowerCase()) !== -1)
      .filter(item => !userType || this.getUserType(item.userType).toLowerCase() === userType.toLowerCase())
      .filter(item => !state || this.getState(item.state).toLowerCase() === state.toLowerCase());

    this.setState({ results: filteredResults });
  };

  renderSearchMenu() {
    const { id, name, userType, state } = this.state;

    const handleChange = name => event => {
      this.setState({ [name]: event.target.value });
    };

    return (
      <Grid container direction="row" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
        <TextField label="ID" value={id} onChange={handleChange("id")} margin="normal" variant="outlined" style={{ marginRight: 10 }} />
        <TextField label="Nombre" value={name} onChange={handleChange("name")} margin="normal" variant="outlined" style={{ marginRight: 10 }} />
        <TextField label="Tipo" value={userType} onChange={handleChange("userType")} margin="normal" variant="outlined" style={{ marginRight: 10 }} />
        <TextField label="Estado" value={state} onChange={handleChange("state")} margin="normal" variant="outlined" style={{ marginRight: 10 }} />
        <Button variant="contained" color="primary" size="large" onClick={this.handleButtonClick}>
          Buscar
        </Button>
      </Grid>
    );
  }

  getUserType(userType) {
    return userType === "client" ? "Cliente" : "Conductor";
  }

  getState(state) {
    switch (state) {
      case 0:
        return "Libre";
      case 1:
        return "No disponible";
      case 2:
        return "Esperando confirmación de viaje";
      case 3:
        return "En un viaje";
    }
  }

  getUserLocation(lat, lng) {
    if (!lat || !lng) {
      return null;
    }

    const url = `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`;
    return (
      <a href={url} target="_blank">
        Ver en Google Maps
      </a>
    );
  }

  renderTableResults() {
    const { results } = this.state;

    if (!results) {
      return null;
    }

    return (
      <Paper style={{ maxWidth: "100%", maxHeight: "600px", overflowY: "scroll", backgroundColor: "rgba(255, 255, 255, 0.80)" }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Nombre</TableCell>
              <TableCell>Apellido</TableCell>
              <TableCell>Tipo de usuario</TableCell>
              <TableCell>Estado</TableCell>
              <TableCell>Ubicación</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {results.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.id}</TableCell>
                <TableCell>{row.firstName}</TableCell>
                <TableCell>{row.lastName}</TableCell>
                <TableCell>{this.getUserType(row.userType)}</TableCell>
                <TableCell>{this.getState(row.state)}</TableCell>
                <TableCell>{this.getUserLocation(row.latitude, row.longitude)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    );
  }

  render() {
    return (
      <Grid container direction="column" justify="center" alignItems="center">
        {this.renderSearchMenu()}
        {this.renderTableResults()}
      </Grid>
    );
  }
}
