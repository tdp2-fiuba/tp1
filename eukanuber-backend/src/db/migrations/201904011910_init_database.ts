import Knex from "knex";

function up(knex: Knex) {
  return knex.raw(`CREATE EXTENSION IF NOT EXISTS "uuid-ossp";`);
}

function down(knex: Knex) {
  return knex.raw(`DROP EXTENSION IF EXISTS "uuid-ossp";`);
}

export { up, down };