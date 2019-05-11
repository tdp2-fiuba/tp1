import * as Knex from 'knex';

export async function up(knex: Knex): Promise<any> {
  return knex.schema.createTable('users', tableBuilder => {
    tableBuilder
      .uuid('id')
      .unique()
      .notNullable()
      .primary()
      .defaultTo(knex.raw('uuid_generate_v4()'));

    tableBuilder
      .string('fbId')
      .unique()
      .notNullable();

    tableBuilder.string('firstName').notNullable();
    tableBuilder.string('lastName').notNullable();
    tableBuilder.string('fbAccessToken').notNullable();

    tableBuilder.string('userType').notNullable();
    tableBuilder.jsonb('rating').notNullable();
    tableBuilder.integer('state').defaultTo(0);

    tableBuilder.boolean('loggedIn').defaultTo(false);

    tableBuilder.string('position').notNullable();
  });
}

export async function down(knex: Knex): Promise<any> {
  return knex.raw('DROP TABLE if exists users cascade');
}
