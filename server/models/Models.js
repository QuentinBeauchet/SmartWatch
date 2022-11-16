const { Events } = require("./Events");
const { Types } = require("./Types");
const { Users } = require("./Users");

let models = { Events, Types, Users };

function initModels(connection) {
  let values = Object.values(models);
  values.forEach((model) => model.init(connection));
  values.forEach((model) => model.associate(models));
}

module.exports = {
  ...models,
  initModels,
};
