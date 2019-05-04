import * as Knex from "knex";

export async function up(knex: Knex): Promise<any> {
  return knex.schema.createTable("cars", tableBuilder => {
    tableBuilder
      .uuid("id")
      .unique()
      .notNullable()
      .primary()
      .defaultTo(knex.raw("uuid_generate_v4()"));

    tableBuilder
      .uuid("userId")
      .references("id")
      .inTable("users")
      .onDelete("CASCADE");

    tableBuilder.string("brand").notNullable();
    tableBuilder.string("model").notNullable();
    tableBuilder.string("plateNumber").notNullable();
  });
}

export async function down(knex: Knex): Promise<any> {
  return knex.raw("DROP TABLE if exists cars cascade");
}
