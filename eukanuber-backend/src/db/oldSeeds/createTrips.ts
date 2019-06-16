import Knex from "knex";

async function seed(knex: Knex) {
  const providersQueryBuilder = knex("trips");

  const newTrip1 = {
    createdDate: new Date(),
    origin: "Goya 630, Buenos Aires, Argentina",
    destination: "Avenida Paseo Colón 850, Buenos Aires, Argentina",
    originCoordinates: "43.138092,-89.747988",
    destinationCoordinates: "43.017218,-89.831479",
    clientId: "31bf9cab-d665-4553-8edd-8d2c2ca8f0e3",
    driverId: "51bf9cab-d665-4553-8edd-8d2c2ca8f0e4",
    pets: "S, M, L",
    status: 4,
    payment: "cash",
    price: "$300"
  };

  const newTrip2 = {
    createdDate: new Date(),
    origin: "Avenida Paseo Colón 851, Buenos Aires, Argentina",
    destination: "Goya 631, Buenos Aires, Argentina",
    originCoordinates: "43.138092,-89.747988",
    destinationCoordinates: "43.017218,-89.831479",
    clientId: "31bf9cab-d665-4553-8edd-8d2c2ca8f0e3",
    driverId: "51bf9cab-d665-4553-8edd-8d2c2ca8f0e4",
    pets: "S, M, L",
    status: 4,
    payment: "cash",
    price: "$350"
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert([newTrip1, newTrip2]);
}

export { seed };
