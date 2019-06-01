var admin = require('firebase-admin');

var serviceAccount = require('./keys/northern-hope-236009-firebase-adminsdk-x0jo9-9726443984.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://northern-hope-236009.firebaseio.com',
});

var options = {
  priority: 'normal',
  timeToLive: 60 * 60, //hold notification for 1hour if device is offline.
};

async function sendNotificationPriorityNormal(registrationToken: string, payload: any) {
  try {
    admin
      .messaging()
      .sendToDevice(registrationToken, payload, options)
      .then(function(response: any) {
        console.log(`Successfully notified user:'${response}'`);
      })
      .catch(function(error: any) {
        console.log(`Error sending notification:'${error}'`);
      });
  } catch (e) {
    console.log(`Notification send failed! '${e}'`);
  }
}

export default {
  sendNotificationPriorityNormal,
};
