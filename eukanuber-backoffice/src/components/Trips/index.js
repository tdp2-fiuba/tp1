import React from "react";
import { Grid, Button, Table, TableHead, TableBody, TableFooter, TableCell, TableRow, Paper } from "@material-ui/core";
import backendService from "../../backendService";

import "./styles.css";

export default class Home extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = { fromDate: undefined, toDate: undefined, clientId: undefined, driverId: undefined, results: undefined };
  }
  handleButtonClick = async () => {
    const results = await backendService.getTrips();
    this.setState({ results });
  };

  renderSearchMenu() {
    //     clientId: "013eb3ff-6ac9-4d93-b789-b750478b9ed9"
    // createdDate: "2019-06-02T06:42:15.564Z"
    // destination: "Fitz Roy, Buenos Aires, Argentina"
    // destinationCoordinates: "-34.5849069,-58.4372078"
    // distance: "56.5 km"
    // driverId: null
    // desde / hasta / id cliente / id conductor
    return (
      <Grid container direction="row" justify="center" alignItems="center" style={{ marginBottom: 25 }}>
        <Button variant="contained" color="primary" onClick={this.handleButtonClick}>
          Search
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
      <Paper style={{ maxWidth: "100%", maxHeight: "650px", overflowY: "scroll", backgroundColor: "rgba(255, 255, 255, 0.80)" }}>
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
                <TableCell>{row.createdDate}</TableCell>
                <TableCell>{row.clientId}</TableCell>
                <TableCell>{row.driverId}</TableCell>
                <TableCell>{row.pets}</TableCell>
                <TableCell>{row.escort}</TableCell>
                <TableCell>{row.origin}</TableCell>
                <TableCell>{row.destination}</TableCell>
                <TableCell>{row.status}</TableCell>
                <TableCell align="right">{row.price}</TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TableCell colSpan={7} />
              <TableCell>Total</TableCell>
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
