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
    tableBuilder.string("pets").notNullable();
    tableBuilder.string("status").notNullable();
    tableBuilder.string("clientId").notNullable();
    tableBuilder.string("driverId").notNullable();
    tableBuilder.string("price").notNullable();

    // TODO: Add Client ID and Driver ID indexes
  });
}

function down(knex: Knex) {
  return knex.schema.dropTable("providers");
}

export { up, down };
