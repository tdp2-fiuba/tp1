import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("trips");

  const newTrip = {
    origin: "43.138092,-89.747988",
    destination: "43.017218,-89.831479",
    clientId: "abc123",
    driverId: "def456",
    pets: "S, M, L",
    status: "0",
    payment: "cash",
    price: "100 USD"
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(newTrip);
}

export { seed };
