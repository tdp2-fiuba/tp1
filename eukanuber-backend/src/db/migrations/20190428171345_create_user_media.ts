import * as Knex from 'knex';

export async function up(knex: Knex): Promise<any> {
  return knex.schema.createTable('userMedia', tableBuilder => {
    tableBuilder
      .uuid('id')
      .unique()
      .notNullable()
      .primary()
      .defaultTo(knex.raw('uuid_generate_v4()'));

    tableBuilder
      .uuid('userId')
      .references('id')
      .inTable('users')
      .onDelete('CASCADE');
    tableBuilder.string('fileName').notNullable();
    tableBuilder.binary('fileContent');
  });
}

export async function down(knex: Knex): Promise<any> {
  return knex.schema.dropTableIfExists('userMedia');
}
