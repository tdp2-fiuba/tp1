import React from "react";
import { Link } from "react-router-dom";
import { Grid, Button, Table, TableHead, TableBody, TableCell, TableRow, Paper, TextField, Modal, Typography, Select, MenuItem } from "@material-ui/core";
import backendService from "../../backendService";
import "./styles.css";
import AccountCircleIcon from "@material-ui/icons/AccountCircle";
import userService from "../../userService";

export default class Home extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = { id: undefined, name: undefined, userType: undefined, state: undefined, selectedUser: undefined, selectedImage: undefined };
  }

  componentDidMount() {
    const queryString = window.location.search.substring(1);
    if (!queryString) {
      return;
    }

    const userIdQueryString = queryString.split("&").find(item => item.indexOf("userId=") !== -1);
    if (!userIdQueryString) {
      return;
    }

    const userId = userIdQueryString.substring("userId=".length);
    this.setState({ id: userId }, this.handleButtonClick);
  }

  handleButtonClick = async () => {
    const { id, name, userType, state } = this.state;
    const userInfo = userService.getUserInfo();
    const results = await backendService.getUsers(userInfo.token);
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

  handleViewDetailsClick = async (e, selectedUser) => {
    e.preventDefault();
    this.setState({ selectedUser });
    const userInfo = userService.getUserInfo();

    await backendService.getUser(selectedUser.id, userInfo.token).then(result => {
      if (result.error) {
        return console.error(result.error);
      }

      console.log(result);
      this.setState({ selectedUser: result });
    });
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

  getAccess(access) {
    switch (access) {
      case 0:
        return "Activo";
      case 1:
        return "Rechazado";
      case 2:
        return "Pendiente";
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
              <TableCell>Estado del registro</TableCell>
              <TableCell>Estado en la aplicación</TableCell>
              <TableCell>Ubicación</TableCell>
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {results.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.id}</TableCell>
                <TableCell>{row.firstName}</TableCell>
                <TableCell>{row.lastName}</TableCell>
                <TableCell>{this.getUserType(row.userType)}</TableCell>
                <TableCell>{this.getAccess(row.access)}</TableCell>
                <TableCell>{this.getState(row.state)}</TableCell>
                <TableCell>{this.getUserLocation(row.latitude, row.longitude)}</TableCell>
                <TableCell>
                  <Link onClick={e => this.handleViewDetailsClick(e, row)} to={`/users?userId=${row.id}`}>
                    Ver detalle
                  </Link>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    );
  }

  handleSelectedUserDataChange = (key, value) => {
    const { selectedUser } = this.state;
    selectedUser[key] = value;

    this.setState({ selectedUser }, async () => {
      const userInfo = userService.getUserInfo();
      await backendService.updateUser(selectedUser, userInfo.token);
    });

    this.forceUpdate();
  };

  renderUserImages = images => {
    const { selectedImage } = this.state;
    if (!images || images.length === 0) {
      return;
    }

    const handleImageLinkClick = (e, imageBase64) => {
      e.preventDefault();
      this.setState({ selectedImage: imageBase64 });
    };

    const profileImageFile = images.find(item => item.fileName === "profileImage");
    const carPictureFile = images.find(item => item.fileName === "carPicture");
    const licensePictureFile = images.find(item => item.fileName === "licensePicture");
    const insurancePictureFile = images.find(item => item.fileName === "insurancePicture");

    return (
      <Grid container direction="column" style={{ marginTop: 10 }}>
        <Typography variant="h6">Imágenes</Typography>
        {profileImageFile && (
          <Typography variant="subtitle1">
            Imagen de perfil:
            <Link onClick={e => handleImageLinkClick(e, profileImageFile.fileContent)} to="/" style={{ marginLeft: 5 }}>
              Ver
            </Link>
          </Typography>
        )}
        {carPictureFile && (
          <Typography variant="subtitle1">
            Imagen del auto:
            <Link onClick={e => handleImageLinkClick(e, carPictureFile.fileContent)} to="/" style={{ marginLeft: 5 }}>
              Ver
            </Link>
          </Typography>
        )}
        {licensePictureFile && (
          <Typography variant="subtitle1">
            Licencia de conducir:
            <Link onClick={e => handleImageLinkClick(e, licensePictureFile.fileContent)} to="/" style={{ marginLeft: 5 }}>
              Ver
            </Link>
          </Typography>
        )}
        {insurancePictureFile && (
          <Typography variant="subtitle1">
            Carnet de seguro:
            <Link onClick={e => handleImageLinkClick(e, insurancePictureFile.fileContent)} to="/" style={{ marginLeft: 5 }}>
              Ver
            </Link>
          </Typography>
        )}
        <Modal open={selectedImage} onClose={() => this.setState({ selectedImage: undefined })}>
          <img className="users-modal" src={`data:image/png;base64,${selectedImage}`} alt="Red dot" />
        </Modal>
      </Grid>
    );
  };

  renderModal() {
    const { selectedUser } = this.state;

    if (!selectedUser) {
      return undefined;
    }

    return (
      <Modal open={selectedUser !== undefined} onClose={() => this.setState({ selectedUser: undefined })}>
        <Paper className="users-modal" style={{ padding: "50px", width: 350 }}>
          <div style={{ width: "100%", height: "150px", textAlign: "center" }}>
            <AccountCircleIcon style={{ fontSize: 120 }} />
          </div>

          <Typography variant="h6" style={{ textAlign: "center", marginBottom: 5 }}>{`${selectedUser.firstName} ${selectedUser.lastName}`}</Typography>
          <Typography variant="subtitle1">Tipo de usuario: {this.getUserType(selectedUser.userType)}</Typography>
          {selectedUser.userType === "driver" && (
            <Typography variant="subtitle1">
              Tipo de auto: {selectedUser.car && `${selectedUser.car.brand} ${selectedUser.car.model} (Patente ${selectedUser.car.plateNumber})`}
            </Typography>
          )}
          <Typography variant="subtitle1">Estado en la aplicación: {this.getState(selectedUser.state)}</Typography>
          <Typography variant="subtitle1">
            Estado del registro:
            <Select
              style={{ marginLeft: 5 }}
              value={selectedUser.access}
              name="Estado del registro"
              onChange={e => this.handleSelectedUserDataChange("access", e.target.value)}
            >
              <MenuItem value={0}>Activo</MenuItem>
              <MenuItem value={1}>Rechazado</MenuItem>
              <MenuItem value={2}>Pendiente</MenuItem>
            </Select>
          </Typography>
          {this.renderUserImages(selectedUser.images)}
        </Paper>
      </Modal>
    );
  }

  render() {
    return (
      <Grid container direction="column" justify="center" alignItems="center">
        {this.renderSearchMenu()}
        {this.renderTableResults()}
        {this.renderModal()}
      </Grid>
    );
  }
}
