import axios from "axios";

const url = "https://graph.facebook.com/v3.3/";

interface IFBData {
  id: string;
  friends: {
    data: string[];
    summary: {
      total_count: number;
    };
  };
}

async function getFacebookFriendCount(fbAccessToken: string): Promise<IFBData> {
  const result = await axios.get(`${url}/me?fields=id,friends`, { headers: { Authorization: "Bearer " + fbAccessToken } });

  if (result.status !== 200) {
    throw new Error(result.statusText);
  }

  if (!result.data || !result.data) {
    throw new Error("Google API didn't return data when requesting coordinates");
  }

  return { ...result.data };
}

export default {
  getFacebookFriendCount
};
