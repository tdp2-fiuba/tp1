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
        "project_id": "eukanuber",
        "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDP+BtsnxEhLnBY\n9fEulY/YAdUpvRHs1FEZ//8G26DPhEb/f8QkIBpoiQrhQuevk8QWXRneMYvcMwuK\n/HFqjQQJ6mM8Rvwe+lkt1bAGmOk0TU6GJ3RlgKkB4ihrE6G35zbnCdRO7ovsRenO\nTUVCIiuXjpErzQqtgWxzdchUWKZc+hOFby82+6GMxPa7guzqkGgtxhCEI7bNw5pQ\nIJRppFZLVVPVBKMHscoAgaADQDUA29/M/avM88YPrt0XhqPrIrEeI69jHQmkqyK1\norPtYVG50gT4E8HP4XPXV9l9mK9GfKhXJfjtf5C/gy3tOI/gJq/PesntbI3N6I4D\nWXpyJ6hfAgMBAAECggEAAJRWlzkWPIgH/hqpUl6BgrH2nK6PLaBZA+aHsmAx4YIj\nCueZoOdWy39eQJefVG9OXSjJpd0J4IVjFqVyndmViLJWsAtOloa1PhoM6ufJw1ok\nlt3mebkf2QZ+44iv3na0qEFWjkwlDajgNUskn0HzHYsv8lxLsR7dy31cUofXGgYD\nEJUy6nnvNdeAQDstkMDS/hgHvtp7lmwvCeR7Y6BxP3H1nJhbm/+R1pA/NVBy46J3\n4yKdjnMEvEBq0RL6hIu3ArmUWYHSVt0+m76DXuI8Y2+N1YUHA66weM/RUnHRSGPv\n0V/+XttYforYEODwVRT3JJfABU3AJrKb8CNiVEYH+QKBgQDuNEYz1FoI6bqNygZY\nVT0X/yjVbpgilwEsNQhVoILCi73Ft3f0bigmJeIRJs/zyR5ZwIfHTRT9aOzIeZ3T\n4aFA0pAcRVrmSrLgJwvEjxnCD3ApUnfrTJfr519DfeVD4YgNgpKC9JVqscC/jmfn\nWr655RUycZfTzYBZQRDorTtSvQKBgQDfgZQ8DQpXnmX4Ch7u93pdMfJqWnuJ+NUh\nc4e6SYHtodwsCoaBzFGClK+Uhf9AQnRzQd8xPJxaE+XgDhLiFqNtb3sTalilDbmh\nWJVfP/zgCcwHkMX9VXACU/I6yIxEIMrQgmeD69qD3Mbn47cggDjRzaqMwNkizSaL\nMbWMXGRHSwKBgApO0MrlYZAJLT19hJDF/4LOU+IIiyTUDATxto6eB4qLqYaozdQS\nAELGWOYNG+qcxWd04WtKdqIQrE5wlNfaZ7P6aKoQhkJ48QyqI+Q5nQ+8w0nGe9H+\n2oQm2wR8qjMpy01qXRS1qNGZJ5Iig3SLhL1eoHAyYjOULP2vxx+Kf4qxAoGBAMJU\nkiRcWj+wc4xkkFzKRFQ/lzyti3h9KCK7pqlsPFH3R83rIOQ+WCinSrpif3rJzSJh\nhcPzpSYU4uwakISPlm0SXvqUnpp0ApCsysNUiXPqUMHLOLp5zLfZcWV29a5OrMj6\neTKUoRvBnBgHQDQ8CCszHTTggGrn4Llp8SYnNvFdAoGBANJ4Ux2qN86Wc4AdjSmN\nmb+A2XHhw4Nm5MKMF7sWhtHbGobf+4sbyYJwGvW3y9TnpRbb4q3PNUnzDzY95Fr+\nQ5vipgJBXseL737VpeLso8jCHnDcVXnba2frDa0h1nMq0aFzUJUVSBfCRC0seG3z\nwPY/X7JIaMXgSsvsmf72XC2/\n-----END PRIVATE KEY-----\n",
        "client_email": "firebase-adminsdk-knm48@eukanuber.iam.gserviceaccount.com",
    },
    prices: {
        priceKm: 25,
        pricePet: 50,
        priceEscort: 100,
        priceMinute: 5,
        extraNocturnTime: 0.5
    }
};
