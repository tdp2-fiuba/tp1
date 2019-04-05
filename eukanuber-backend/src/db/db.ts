import config from "config";
import Knex from "knex";

const dbConfiguration = config.get("db") as Knex.Config;
const dbRepository = Knex(dbConfiguration);

export default dbRepository;
