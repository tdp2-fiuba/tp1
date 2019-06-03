import Knex from "knex";
import { base64encode } from "nodejs-base64";

// Create an encryptor:
async function seed(knex: Knex) {
  const providersQueryBuilder = knex("admins");
  const newAdmin = {
    id: "31bf9cab-d665-4553-8edd-8d2c2ca8f0e3",
    username: "admin",
    password: base64encode("admin")
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert([newAdmin]);
}

export { seed };
