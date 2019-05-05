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

  if (!result.data) {
    throw new Error("Facebook API didn't return data");
  }

  if (result.status !== 200) {
    throw new Error(result.statusText);
  }

  return { ...result.data };
}

export default {
  getFacebookFriendCount
};
