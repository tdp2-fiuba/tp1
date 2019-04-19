module.exports = {
  db: {
    client: "pg",
    connection: {
      charset: "utf8",
      ssl: true,
      host: process.env.DB_HOST || "ec2-54-221-236-144.compute-1.amazonaws.com",
      database: process.env.DB_NAME || "d50e73l23s0pu2",
      user: process.env.DB_USER || "crxbrsetvwiibl",
      password: process.env.DB_PASSWORD || "f373e17218296dac81231aa18a6fb0398c7dca1802606107520f55c9859af597"
    },
    migrations: {
      directory: "./dist/db/migrations"
    },
    seeds: {
      directory: "./dist/db/seeds"
    }
  }
};
