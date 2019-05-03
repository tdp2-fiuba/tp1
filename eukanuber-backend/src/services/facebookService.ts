import axios from 'axios';

var APP_ID = '456257531845861';
var ACCESS_TOKEN = '456257531845861|jF85CHDkdzzC62OzmGJ9xh4nrbw';
const url = 'https://graph.facebook.com/v3.3/';

async function getFacebookFriendCount(fbId: string) {
  const result = await axios.get(`${url}/${fbId}/friends`, { headers: { Authorization: 'Bearer ' + ACCESS_TOKEN } });

  if (result.status !== 200) {
    throw new Error(result.statusText);
  }

  if (!result.data || !result.data) {
    throw new Error("Google API didn't return data when requesting coordinates");
  }

  const accountData = result.data.summary.total_count;
  return `${accountData}`;
}

export default {
  getFacebookFriendCount,
};
