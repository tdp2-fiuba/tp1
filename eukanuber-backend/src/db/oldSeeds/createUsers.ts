import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("users");

  const newUser = {
    id: "31bf9cab-d665-4553-8edd-8d2c2ca8f0e3",
    userType: "client",
    fbId: "1",
    fbAccessToken: "11111",
    firstName: "Alejandro",
    lastName: "Fantino",
    access: 0,
    state: 0,
    latitude: "-34.778050",
    longitude: "-58.288390"
  };

  const newUser2 = {
    id: "41bf9cab-d665-4553-8edd-8d2c2ca8f0e4",
    userType: "client",
    firstName: "Lionel",
    lastName: "Messi",
    fbId: "2",
    fbAccessToken: "22222",
    access: 0,
    state: 0,
    loggedIn: true,
    latitude: "-34.600040",
    longitude: "-58.511570"
  };

  const newUser3 = {
    id: "51bf9cab-d665-4553-8edd-8d2c2ca8f0e4",
    userType: "driver",
    firstName: "Javier",
    lastName: "Mascherano",
    fbId: "3",
    fbAccessToken: "33333",
    access: 0,
    state: 0,
    loggedIn: true,
    latitude: "-34.616610",
    longitude: "-58.444760"
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert([newUser, newUser2, newUser3]);
}

export { seed };
