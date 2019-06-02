import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("users");

  const newUser = {
    id: "31bf9cab-d665-4553-8edd-8d2c2ca8f0e3",
    userType: "client",
    fbId: "1234",
    fbAccessToken: "12345",
    firstName: "Alejandro",
    lastName: "Fantino",
    access: 0,
    state: 0,
    latitude: "12345",
    longitude: "12345"
  };
  const newUser2 = {
    id: "31bf9cab-d665-4553-8edd-8d2c2ca8f0e4",
    userType: "client",
    firstName: "Lionel",
    fbId: "12345",
    fbAccessToken: "12345",
    lastName: "Messi",
    access: 0,
    state: 0,
    loggedIn: true,
    latitude: "12345",
    longitude: "12345"
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert([newUser, newUser2]);
}

export { seed };
