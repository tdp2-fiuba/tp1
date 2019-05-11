import Knex from 'knex';

async function seed(knex: Knex) {
  const providersQueryBuilder = knex('users');

  const newUser = {
    userType: 'Client',
    position: '43.017218,-89.831479',
    firstName: 'Arthur',
    lastName: 'Dent',
    fbAccessToken: '1232323222',
    fbId: 'afa2a272-6b8d-11e9-a923-1681be663d3e',
    state: '0',
    rating: { sum: 0, n: 0 },
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  return providersQueryBuilder.insert(newUser);
}

export { seed };
