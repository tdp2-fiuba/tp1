import Knex from 'knex';

async function seed(knex: Knex) {
  const providersQueryBuilder = knex('cars');

  const car = {
    brand: 'Bentley',
    model: 'Brooklands',
    plateNumber: 'AAA123',
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(car);
}

export { seed };
