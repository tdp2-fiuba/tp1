import {expect} from "chai";
import Sinon from "sinon";

import firebaseService from "../../services/firebaseService";
import userService from "../../services/userService";

describe("firebaseService", () => {

    describe("#testFirebasePush", () => {
        describe("do call", () => {

            it.only("should call the facebook API to get the data", () => {

                let registrationId = "eSBBiyP8SDA:APA91bHlYFVhyd85-hrcrhs_2aR9Vk1SwrSreqlDnPSB1JOwo4511qpOL64KuIey9dIdgpPKymG_fJOBSnBa5h2tNd9XzGRgPQrHXZBqyjBd9QpSpEuSfthtKMfiFbabGTv8kpQGppxy";

                let dataMessage = {
                    "title": "Tienes un nuevo viaje disponible!",
                    "type": "new_trip",
                    "driverName": "Juan Carlos",
                    "driverScore": "4.6",
                    "pets": "2",
                    "distance": "2.45 km",
                    "duration": "18 min",
                    "price": "$276",
                    "tripId": "aTripId"
                }
                firebaseService.sendNotification(registrationId, dataMessage)

            });

        });
    });
});
