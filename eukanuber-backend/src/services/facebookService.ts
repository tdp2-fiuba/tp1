import axios from 'axios';

const url = 'https://graph.facebook.com/v3.3/';

interface FBData {
  id: string;
  friends: {
    data: Array<string>;
    summary: {
      total_count: number;
    };
  };
}

async function getFacebookFriendCount(fbAccessToken: string) {
  const result = await axios.get(`${url}/me?fields=id,friends`, { headers: { Authorization: 'Bearer ' + fbAccessToken } });

  if (result.status !== 200) {
    throw new Error(result.statusText);
  }

  if (!result.data || !result.data) {
    throw new Error("Google API didn't return data when requesting coordinates");
  }

  let fbData: FBData = { ...result.data };

  return fbData;
}

export default {
  getFacebookFriendCount,
};
