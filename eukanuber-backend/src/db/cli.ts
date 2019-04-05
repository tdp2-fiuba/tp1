import config from "config";
import knex from "knex";

const dbConfiguration = config.get("db");
const db = knex(dbConfiguration);
const operationArg = process.argv[2] && process.argv[2].substring(2);

function executeOperation(operation: string) {
  if (operation === "migrate") {
    // Runs all migrations that have not yet been run
    // For more information, see https://knexjs.org/#Migrations-latest
    return db.migrate.latest();
  }

  if (operation === "rollback") {
    // Rolls back the latest migration group
    // For more information, see https://knexjs.org/#Migrations-latest
    return db.migrate.rollback();
  }

  if (operation === "seed") {
    // Runs all seed files for the current environment
    // For more information, see https://knexjs.org/#Seeds-API
    return db.seed.run();
  }

  throw new Error(`No operation named ${operation} was found.`);
}

(async function init() {
  try {
    console.log(`Starting the "${operationArg}" operation. Current DB version ${await db.migrate.currentVersion()}`);
    await executeOperation(operationArg);
    console.log(`Done executing the "${operationArg}" operation! . Current DB version ${await db.migrate.currentVersion()}`);
    console.log();
    process.exit();
  } catch (error) {
    console.log(`Error while executing the "${operationArg}" operation.\n`, error);
    console.log();
    process.exit(1);
  }
})();
