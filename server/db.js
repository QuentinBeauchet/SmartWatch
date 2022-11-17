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

/**
 * Return all events.
 * @param {*} req
 * @param {*} res
 */
function getAllEvents(req, res) {
  Events.findAll().then((events) => {
    res.status(200).json(events);
  });
}

/**
 * Insert the event in the DB.
 * @param {*} req
 * @param {*} res
 */
function addEvent(req, res) {
  let { user_id, type_id, latitude, longitude, comment } = req.body;
  if (!(user_id && type_id && latitude && longitude && comment)) {
    res.status(200).json({ success: false });
    return;
  }

  Events.create({ user_id, type_id, latitude, longitude, comment, date: Date.now() }).then(() => {
    res.status(200).json({ success: true });
  });
}

/**
 * Insert and event type in the DB.
 * @param {*} req
 * @param {*} res
 */
function addEventType(req, res) {
  let { name } = req.body;
  if (!name) {
    res.status(200).json({ success: false, id: null });
    return;
  }

  Types.create({ name }).then(({ id }) => {
    res.status(200).json({ success: true, id });
  });
}

/**
 * Return the id from the device_id, create the user if he does not exists and update his name if he does.
 * @param {*} req
 * @param {*} res
 * @returns
 */
function connect(req, res) {
  let { device_id, name } = req.body;
  if (!(device_id && name)) {
    res.status(200).json({ success: false, id: null });
    return;
  }

  let query = {
    where: {
      device_id,
    },
  };
  Users.findOne(query).then((user) => {
    if (user == null) {
      Users.create({ device_id, name }).then(({ id }) => res.status(200).json({ success: true, id }));
    } else {
      Users.update({ name }, query);
      res.status(200).json({ success: true, id: user.id });
    }
  });
}

module.exports = {
  initdb,
  getAllEvents,
  addEvent,
  connect,
  addEventType,
};
