import backendService from "./backendService";

const USER_INFO_KEY = "userInfo";

function getUserInfo() {
  const userInfo = window.localStorage.getItem(USER_INFO_KEY);
  return userInfo && JSON.parse(userInfo);
}

function setUserInfo(userInfo) {
  window.localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
}

async function login(username, password) {
  const userInfo = getUserInfo();

  if (userInfo) {
    return userInfo;
  }

  const result = await backendService.loginUser(username, password);

  if (result.error) {
    return result;
  }

  setUserInfo(result);
  return result;
}

async function logout() {
  window.localStorage.removeItem(USER_INFO_KEY);
  return Promise.resolve();
}

export default {
  getUserInfo,
  login,
  logout
};
