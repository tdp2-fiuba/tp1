var admin = require('firebase-admin');
import config from "config";
var serviceAccount = config.get("firebase");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: 'https://northern-hope-236009.firebaseio.com',
});

async function sendNotificationPriorityNormal(registrationToken: string, payload: any) {
    const data = {
        data: payload
    };
    try {
        admin.messaging().sendToDevice(registrationToken, data)
            .then((response: any) => {
                // Response is a message ID string.
                console.log('Successfully sent message:', response);
            })
            .catch((error: any) => {
                console.log('Error sending message:', error);
            });

    } catch (e) {
        console.log(`Notification send failed! '${e}'`);
    }
}

export default {
    sendNotificationPriorityNormal,
};
