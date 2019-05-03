import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("users");

  const newUser = {
    userType: "Passenger",
    position: "43.017218,-89.831479",
    firstName: "Arthur",
    lastName: "Dent",
    fbId: "afa2a272-6b8d-11e9-a923-1681be663d3e",
    state: "0",
    rating: 4
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(newUser);
}

export { seed };
