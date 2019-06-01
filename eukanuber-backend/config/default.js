module.exports = {
    db: {
        client: 'pg',
        connection: {
            charset: 'utf8',
            ssl: true,
            host: process.env.DB_HOST || 'ec2-54-221-236-144.compute-1.amazonaws.com',
            database: process.env.DB_NAME || 'd50e73l23s0pu2',
            user: process.env.DB_USER || 'crxbrsetvwiibl',
            password: process.env.DB_PASSWORD || 'f373e17218296dac81231aa18a6fb0398c7dca1802606107520f55c9859af597',
        },
        migrations: {
            directory: './dist/db/migrations',
        },
        seeds: {
            directory: './dist/db/seeds',
        },
    },
    firebase: {
        "type": "service_account",
        "project_id": "northern-hope-236009",
        "private_key_id": "97264439844b978751e67f20446d96a0c4326d99",
        "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCUyLwIjeVxaFif\njJzs5xnNNBZc6DRS7Gly2Ojhmrwkko2dVnMBjwhsOxdVftvk4K/0LyCWfseE0nRD\nzDFBmOY+1Z9XxZ+EaN/W86cBdrEIaRH8LQosA1jxYVoCQaIIXYDirUFjdiRId7Gq\nK8TBwnIKGP9TkMe/xv3F/VuDTWvohr2J+NbT228Xrh2Kbo0Dv+W0QCKcxBS9nmcb\nCQtu/AGoFFtDNUix2EYA2S1KWRsbgVdrIdMjzQPZLHTcacM5MfBfEpnrJQlmJx38\nd3+vbgQodPFtIa4oh6STk0GvCEyLbW8Gka6CkT8t3rklg6tJq+tgL2GMLfgFNiVR\n4ilaoDqbAgMBAAECggEAFI2C4Bb4ljPJDkfmg5lMXfrvMz1bwQVpxFTaHWl6dP45\nCX41hoZuLkP0GLoyAR+CtD4bB9Rn67KwyeQip8JJ3W3gGZ1cnaC+ecXlu5TcUWJG\nUD9z3hmbnJiLTlrSWcp6MUCoEufGjQDag5Hml7IsfmBD9moTwIEUxwK/31b/bDrp\nVDtmDMcoHq9s6HYGYJgE1ynjOPA3y5VAiy7QKTlZNGVY0uYenorSd8YZkiv3qhAg\nlEtJjNtZPI7D4oX+Uh4NBjfDqcAheYmY6BM7d6+MgyUP0b96yAmuuZ24Hp65BHfI\nVJoggFQ7wQwVymCCmd9CwHXzKQAaOD1TX/rj5E/FMQKBgQDRnDIyXllwf/36Sw/5\nsBiVhbuxm9ZarELFI16cpIHeLApHp9/kiiA9Ne9tGEfFeP314cCTFK1pNiWuIz4l\ndz0cCxtzgxmCRTSNkLcRhZuXIFwRWLuuyBceoa+KdBrSUOwe9Fedxiya687WJuzy\nDhmApkBhfY9yJnKd69uUjc6ysQKBgQC1tldSCh+MtH8omZJPEQVT9zlPxLpMOtlW\nFj4Mx8koOAMMU+urw1mT/+JYsoRHABpoFJMOS9QWLItLkmHGzcoUU4dE5zbdsy1l\nmDkwEFWK5+uMMz2ZBrPGCV21CxFhkx41/kc/yASXI4qI+qsmX2YKnCDTh/IGSq7l\ntYsuq6idCwKBgGnxLF3Q2hvGVTQAZF6q5oIMScWHIlxJ3KWPLu61Jv6H+9oju0Hj\nnk/RxuW48+2XLaTtKUwtk0guGfVd0rdVAQn/gyxrAqWZHOrATgcJJI7JyD7sPlAk\nyaqtxe+Qp03NoMo0bWRNGZZjwPL0UaY2AiXH4Zpkv+/OBvhSkXUQB3zBAoGAa7Io\nSq1JSaFog+2Q81+JItyxkP9t0uRGD2mbWQPHyxh3ZgUO9nwPSVItGpijIVmETQ90\noNJ8XrAD1sRpQuSQ0j83OrU2pWseisoPitcDfBI8Xpm39kwsnEDM0xI+OPByxVlx\nbPHlFpav8sNuUI3V8o/aPMSjwurcgbR5Sy3d/0UCgYEAnQdwiSaJD2NU3zVHXYjC\nGMfXBDKrpr9FRXYdJ0R0I/QRjmnVMidFLa+sHqVj9CPqhOgpXZn55uniuKwHPNkN\nIGV92SMgDQxTdjT46JgHKNGaKBmrAJmvDU6aoxfkK0luwkC1qdVNAQuneOkSGTBk\n2/XrpJlVTnKFoDJLzNMuXqs=\n-----END PRIVATE KEY-----\n",
        "client_email": "firebase-adminsdk-x0jo9@northern-hope-236009.iam.gserviceaccount.com",
        "client_id": "110357567635586838483",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-x0jo9%40northern-hope-236009.iam.gserviceaccount.com"
    }
};
