# EukanUber Backend

- [![Build Status](https://travis-ci.org/tdp2-fiuba/tp1.svg?branch=master)](https://travis-ci.org/tdp2-fiuba/tp1)
- [![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)
- Database: TBC

## Getting started

Install all dependencies by running:

```
npm i
```

Set up your database by [starting a Postgres service](https://www.postgresql.org/download/) locally. Then create an **Eukanuber** database and, finally, run migrations and seed:

```
npm run db:migrate
npm run db:seed
```

Finally, run the backend server with:

```
npm start
```

### Development

If you want to debug the app, install [VSCode](https://code.visualstudio.com/) and launch the **Debug** task. Alternatively, run in your terminal:

```
npm run debug
```

And then, attach your IDE to the port `9229`.
