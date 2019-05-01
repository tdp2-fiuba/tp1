import axios from "axios";

var APP_ID = "456257531845861";
var ACCESS_TOKEN = "EAAGe9rwcnOUBAK5FiSspMkMyXYuSPWK3SfZBGRQO4ri7NS4dWOKZBM3NL0FpMpddR1d5ZBkjUGbZCnu3LCqstZCGLIYGSH1lB8x1wgwhHNfZCn2FZBCtDsObx1S4TYs8rmgzONPd6Rra6YEDOuSmyDUJHYuMWe8nFglRDggBYIno3HYIguLLGseJZAYnUeZCkSrUfDhQrsMrP4AZDZD";
const url = "https://graph.facebook.com/v3.3/";


async function getFacebookFriendCount(fbId: string) {
    const result = await axios.get(`${url}/${fbId}/friends`, { headers: { Authorization: "Bearer " + ACCESS_TOKEN }});

    if (result.status !== 200) {
        throw new Error(result.statusText);
    }


  if (!result.data || !result.data) {
    throw new Error("Google API didn't return data when requesting coordinates");
  }

  const accountData = result.data.summary.total_count;
  return `${accountData}`;
};

export default {
    getFacebookFriendCount
};
