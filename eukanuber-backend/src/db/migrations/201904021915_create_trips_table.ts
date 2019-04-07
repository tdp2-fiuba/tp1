import Knex from "knex";

function up(knex: Knex) {
  return knex.schema.createTable("trips", tableBuilder => {
    tableBuilder
      .uuid("id")
      .unique()
      .notNullable()
      .primary()
      .defaultTo(knex.raw("uuid_generate_v4()"));

    tableBuilder.string("origin").notNullable();
    tableBuilder.string("destination").notNullable();
    tableBuilder.string("originCoordinates").notNullable();
    tableBuilder.string("destinationCoordinates").notNullable();
    tableBuilder.string("clientId").notNullable();
    tableBuilder.string("driverId").defaultTo("");
    tableBuilder.string("pets").defaultTo("");
    tableBuilder.boolean("escort").defaultTo(false);
    tableBuilder.integer("status").defaultTo(0);
    tableBuilder.string("payment").defaultTo("cash");
    tableBuilder.string("price").defaultTo("");

    // TODO: Add Client ID and Driver ID indexes
  });
}

function down(knex: Knex) {
  return knex.schema.dropTable("trips");
}

export { up, down };
