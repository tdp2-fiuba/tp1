import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("users");

  const newUser = {
    userType: "Passenger",
    position: "43.017218,-89.831479",
    firstName: "Arthur",
    lastName: "Dent",
    rating: 4,
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(newUser);
}

export { seed };
