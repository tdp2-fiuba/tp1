import React from "react";
import { Link } from "react-router-dom";
import { Grid, Button, Table, TableHead, TableBody, TableFooter, TableCell, TableRow, Paper, TextField } from "@material-ui/core";
import backendService from "../../backendService";
import "./styles.css";

export default class Home extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = { fromDate: undefined, toDate: undefined, clientId: undefined, driverId: undefined, results: undefined };
  }

  getStatus(status) {
    switch (status) {
      case 0:
        return "Pendiente";
      case 1:
        return "Previaje Cancelado";
      case 2:
        return "Esperando confirmación del cliente";
      case 3:
        return "Esperando confirmación de conductor";
      case 4:
        return "Conductor en camino al origen";
      case 5:
        return "En viaje";
      case 6:
        return "Conductor llegó a destino";
      case 7:
        return "Completado";
      case 8:
        return "Viaje rechazado por conductor";
      case 9:
        return "Viaje cancelado por cliente";
    }
  }

  handleButtonClick = async () => {
    const { fromDate, toDate, clientId, driverId } = this.state;
    const results = await backendService.getTrips();
    console.log(results);

    if (!results.length) {
      return;
    }

    const filteredResults = (results || [])
      .filter(item => !clientId || item.clientId === clientId)
      .filter(item => !driverId || item.driverId === driverId)
      .filter(item => {
        if (!fromDate || fromDate.length < 10) {
          return true;
        }

        const from = new Date();
        from.setFullYear(fromDate.substring(0, 4));
        from.setMonth(parseInt(fromDate.substring(5, 7) - 1));
        from.setDate(fromDate.substring(8, 10));
        from.setHours(0);
        from.setMinutes(0);
        from.setSeconds(0);

        return from <= new Date(item.createdDate);
      })
      .filter(item => {
        if (!toDate || toDate.length < 10) {
          return true;
        }

        const to = new Date();
        to.setFullYear(toDate.substring(0, 4));
        to.setMonth(parseInt(toDate.substring(5, 7) - 1));
        to.setDate(toDate.substring(8, 10));
        to.setHours(23);
        to.setMinutes(59);
        to.setSeconds(59);

        return to >= new Date(item.createdDate);
      });

    this.setState({ results: filteredResults });
  };

  renderSearchMenu() {
    const { fromDate, toDate, clientId, driverId } = this.state;

    const handleChange = name => event => {
      this.setState({ [name]: event.target.value });
    };

    return (
      <Grid container direction="row" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
        <TextField
          label="Desde"
          type="date"
          value={fromDate}
          onChange={handleChange("fromDate")}
          margin="normal"
          variant="outlined"
          style={{ marginRight: 10 }}
          InputLabelProps={{ shrink: true }}
        />
        <TextField
          label="Hasta"
          type="date"
          value={toDate}
          onChange={handleChange("toDate")}
          margin="normal"
          variant="outlined"
          style={{ marginRight: 10 }}
          InputLabelProps={{ shrink: true }}
        />
        <TextField label="ID Cliente" value={clientId} onChange={handleChange("clientId")} margin="normal" variant="outlined" style={{ marginRight: 10 }} />
        <TextField label="ID Conductor" value={driverId} onChange={handleChange("driverId")} margin="normal" variant="outlined" style={{ marginRight: 10 }} />
        <Button variant="contained" color="primary" size="large" onClick={this.handleButtonClick}>
          Buscar
        </Button>
      </Grid>
    );
  }

  renderTableResults() {
    const { results } = this.state;

    if (!results) {
      return null;
    }

    const totalPrice = results.reduce((acc, item) => {
      const price = item.price
        .replace("$", "")
        .replace("USD", "")
        .trim();
      acc += parseFloat(price);
      return acc;
    }, 0);

    return (
      <Paper style={{ maxWidth: "100%", maxHeight: "600px", overflowY: "scroll", backgroundColor: "rgba(255, 255, 255, 0.80)" }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Fecha de creación</TableCell>
              <TableCell>ID Cliente</TableCell>
              <TableCell>ID Conductor</TableCell>
              <TableCell>Mascotas</TableCell>
              <TableCell>Acompañante</TableCell>
              <TableCell>Origen</TableCell>
              <TableCell>Destino</TableCell>
              <TableCell>Estado</TableCell>
              <TableCell>Precio</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {results.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.createdDate && new Date(row.createdDate).toLocaleString()}</TableCell>
                <TableCell>
                  <Link to={`/users?userId=${row.clientId}`}>{row.clientId}</Link>
                </TableCell>
                <TableCell>
                  <Link to={`/users?userId=${row.clientId}`}>{row.driverId}</Link>
                </TableCell>
                <TableCell>{row.pets}</TableCell>
                <TableCell>{row.escort === true ? "Si" : "No"}</TableCell>
                <TableCell>{row.origin}</TableCell>
                <TableCell>{row.destination}</TableCell>
                <TableCell>{this.getStatus(row.status)}</TableCell>
                <TableCell align="right">{row.price}</TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TableCell colSpan={6} />
              <TableCell colSpan={2} align="right">
                <b>Ganancia conductor</b>
              </TableCell>
              <TableCell align="right">{`$${(totalPrice * 0.8).toFixed(2)}`}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={6} />
              <TableCell colSpan={2} align="right">
                <b>Ganancia Eukanuber</b>
              </TableCell>
              <TableCell align="right">{`$${(totalPrice * 0.2).toFixed(2)}`}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={6} />
              <TableCell colSpan={2} align="right">
                <b>Total</b>
              </TableCell>
              <TableCell align="right">{`$${totalPrice.toFixed(2)}`}</TableCell>
            </TableRow>
          </TableFooter>
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
