module.exports = {
  db: {
    client: "pg",
    connection: {
      charset: "utf8",
      ssl: true,
      host: process.env.DB_HOST || "ec2-54-83-61-142.compute-1.amazonaws.com",
      database: process.env.DB_NAME || "ddncc9hrfk3612",
      user: process.env.DB_USER || "eotvmmiosnivph",
      password: process.env.DB_PASSWORD || "20614610755d9293df0f7fb53cf2a4b8614ed1b05244cc2c51ee43c44a91a524"
    },
    migrations: {
      directory: "./dist/db/migrations"
    },
    seeds: {
      directory: "./dist/db/seeds"
    }
  }
};
