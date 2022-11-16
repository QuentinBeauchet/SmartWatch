const { Sequelize } = require("sequelize");
const { initModels, Events, Types, Users } = require("./models/Models");

async function initdb() {
  const sequelize = new Sequelize("watch", "root", "root", {
    host: "localhost",
    dialect: "mariadb",
  });

  try {
    await sequelize.authenticate();
  } catch (error) {
    console.error("Unable to connect to the database:", error);
  }

  initModels(sequelize);
}

module.exports = {
  initdb,
};
