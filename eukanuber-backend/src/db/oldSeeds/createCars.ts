import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("cars");

  const car = {
    userId: "51bf9cab-d665-4553-8edd-8d2c2ca8f0e4",
    brand: "Bentley",
    model: "Brooklands",
    plateNumber: "AAA123"
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(car);
}

export { seed };
