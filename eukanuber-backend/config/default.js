module.exports = {
  db: {
    client: "pg",
    connection: {
      host: process.env.DB_HOST || "127.0.0.1",
      database: process.env.DB_NAME || "Eukanuber",
      user: process.env.DB_USER || "postgres",
      password: process.env.DB_PASSWORD || "postgres",
      charset: "utf8"
    },
    migrations: {
      directory: "./dist/db/migrations"
    },
    seeds: {
      directory: "./dist/db/seeds"
    }
  }
};
