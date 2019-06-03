import axios from "axios";

const baseUri = "https://eukanuber-backend.herokuapp.com";
// const baseUri = "http://localhost:3000";

async function loginUser(username, password) {
  const userInfo = { username, password };

  try {
    const result = await axios.post(`${baseUri}/login`, userInfo);
    return result.data;
  } catch (error) {
    console.error(error);
    const dataError = error.response && error.response.data && error.response.data.error;
    return { error: dataError || error.message };
  }
}

async function getTrips() {
  try {
    const result = await axios.get(`${baseUri}/trips`);
    return result.data;
  } catch (error) {
    console.error(error);
    const dataError = error.response && error.response.data && error.response.data.error;
    return { error: dataError || error.message };
  }
}

async function getUsers() {
  try {
    const result = await axios.get(`${baseUri}/users/all`);
    return result.data;
  } catch (error) {
    console.error(error);
    const dataError = error.response && error.response.data && error.response.data.error;
    return { error: dataError || error.message };
  }
}

async function getUser(userId, authToken) {
  try {
    const config = { headers: { authorization: authToken } };
    const result = await axios.get(`${baseUri}/users/${userId}`, config);
    return result.data;
  } catch (error) {
    console.error(error);
    const dataError = error.response && error.response.data && error.response.data.error;
    return { error: dataError || error.message };
  }
}

async function updateUser(user, authToken) {
  try {
    // delete images from user to reduce payload size
    delete user.images;

    const config = { headers: { authorization: authToken } };
    const result = await axios.put(`${baseUri}/users`, user, config);
    return result.data;
  } catch (error) {
    console.error(error);
    const dataError = error.response && error.response.data && error.response.data.error;
    return { error: dataError || error.message };
  }
}

export default {
  loginUser,
  getTrips,
  getUsers,
  getUser,
  updateUser
};
