import Knex from 'knex';

async function seed(knex: Knex) {
  const providersQueryBuilder = knex('trips');

  const newTrip = {
    origin: 'Goya 630, Buenos Aires, Argentina',
    destination: 'Avenida Paseo Colón 850, Buenos Aires, Argentina',
    originCoordinates: '43.138092,-89.747988',
    destinationCoordinates: '43.017218,-89.831479',
    clientId: 'abc123',
    driverId: 'def456',
    pets: 'S, M, L',
    status: 4,
    payment: 'cash',
    price: '100 USD',
    routes: {},
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(newTrip);
}

export { seed };
