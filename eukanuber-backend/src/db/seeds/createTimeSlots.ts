import Knex from 'knex';

async function seed(knex: Knex) {
  const providersQueryBuilder = knex('timeSlots');

  const timeSlot1 = {
    hourStart: 0,
    hourEnd: 4,
    minEnd: 59,
    price: 20.0,
  };

  const timeSlot2 = {
    hourStart: 5,
    hourEnd: 8,
    minEnd: 59,
    price: 40.0,
  };

  const timeSlot3 = {
    hourStart: 9,
    hourEnd: 12,
    minEnd: 59,
    price: 60.0,
  };

  const timeSlot4 = {
    hourStart: 13,
    hourEnd: 17,
    minEnd: 30,
    price: 30.0,
  };

  const timeSlot5 = {
    hourStart: 17,
    minStart: 31,
    hourEnd: 20,
    minEnd: 59,
    price: 70.0,
  };

  const timeSlot6 = {
    hourStart: 21,
    hourEnd: 12,
    minEnd: 59,
    price: 40.0,
  };

  // Deletes ALL existing entries
  await providersQueryBuilder.del();

  // Inserts new entries
  providersQueryBuilder.insert(timeSlot1);
  providersQueryBuilder.insert(timeSlot2);
  providersQueryBuilder.insert(timeSlot3);
  providersQueryBuilder.insert(timeSlot4);
  providersQueryBuilder.insert(timeSlot5);
  return providersQueryBuilder.insert(timeSlot6);
}

export { seed };
