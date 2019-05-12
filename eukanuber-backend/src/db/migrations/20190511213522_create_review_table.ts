import * as Knex from 'knex';

export async function up(knex: Knex): Promise<any> {
  return knex.schema.createTable('userReview', tableBuilder => {
    tableBuilder
      .uuid('id')
      .unique()
      .notNullable()
      .primary()
      .defaultTo(knex.raw('uuid_generate_v4()'));

    tableBuilder
      .uuid('reviewer')
      .references('id')
      .inTable('users')
      .onDelete('CASCADE');

    tableBuilder
      .uuid('reviewee')
      .references('id')
      .inTable('users')
      .onDelete('CASCADE');

    tableBuilder
      .uuid('tripId')
      .references('id')
      .inTable('trips')
      .onDelete('CASCADE');

    tableBuilder.integer('stars').notNullable();
    tableBuilder.string('comment').defaultTo('');
    tableBuilder.string('dateTime').notNullable();
  });
}

export async function down(knex: Knex): Promise<any> {
  return knex.schema.dropTableIfExists('userReview');
}
