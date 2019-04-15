import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("user");

  const newUser = {
    id: "4e994db0-d847-42fc-9929-e738c8aca2e3",
    userType: "Passenger",
    firstName: "Arthur",
    lastName: "Dent",
    rating: 4,
    position: "-33.8696, 151.2094"
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(newUser);
}

export { seed };
