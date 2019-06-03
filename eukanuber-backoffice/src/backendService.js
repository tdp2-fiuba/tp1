import axios from "axios";

const baseUri = "http://localhost:3000";

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

export default {
  loginUser,
  getTrips,
  getUsers
};
