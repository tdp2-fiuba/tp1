import axios from 'axios';

const url = 'https://graph.facebook.com/v3.3/';

async function getFacebookFriendCount(fbAccessToken: string) {
  const result = await axios.get(`${url}/me?fields=friends`, { headers: { Authorization: 'Bearer ' + fbAccessToken } });

  if (result.status !== 200) {
    throw new Error(result.statusText);
  }

  if (!result.data || !result.data) {
    throw new Error("Google API didn't return data when requesting coordinates");
  }

  const accountData = result.data.friends.summary.total_count;
  return `${accountData}`;
}

export default {
  getFacebookFriendCount,
};
