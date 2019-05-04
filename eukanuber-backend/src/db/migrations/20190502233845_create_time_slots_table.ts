import * as Knex from "knex";

export async function up(knex: Knex): Promise<any> {
  return knex.schema.createTable("timeSlots", tableBuilder => {
    tableBuilder
      .uuid("id")
      .unique()
      .notNullable()
      .primary()
      .defaultTo(knex.raw("uuid_generate_v4()"));

    tableBuilder.integer("hourStart").notNullable();
    tableBuilder
      .integer("minStart")
      .defaultTo(0)
      .notNullable();
    tableBuilder.integer("hourEnd").notNullable();
    tableBuilder
      .integer("minEnd")
      .defaultTo(0)
      .notNullable();
    tableBuilder.float("price").defaultTo(1);
  });
}

export async function down(knex: Knex): Promise<any> {
  return knex.schema.dropTableIfExists("timeSlots");
}
