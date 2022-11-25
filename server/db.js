const { Sequelize } = require("sequelize");
const { initModels, Events, Types, Users } = require("./models/Models");
const { table, user, password, host, port, dialect } = require("./config");
/**
 * Initialize Sequelize and the models.
 */
async function initDB() {
  const sequelize = new Sequelize(table, user, password, {
    host,
    port,
    dialect,
  });

  try {
    await sequelize.authenticate();
  } catch (error) {
    throw `Unable to connect to the database: ${error}`;
  }

  initModels(sequelize);
}

/**
 * Return all events.
 * @param {*} req
 * @param {*} res
 */
function getAllEvents(req, res) {
  Events.findAll().then((events) => res.status(200).json(events));
}

/**
 * Insert the event in the DB.
 * @param {*} req
 * @param {*} res
 */
function addEvent(req, res) {
  let { user_id, type_id, latitude, longitude, comment } = req.body;
  if ([user_id, type_id, latitude, longitude].includes(undefined)) {
    res.status(200).json({ success: false });
    return;
  }

  Events.create({ user_id, type_id, latitude, longitude, comment: comment || null, date: Date.now() }).then(() => {
    res.status(200).json({ success: true });
  });
}

/**
 * Delete the event with the id passed as parameter from the DB.
 * @param {*} req
 * @param {*} res
 * @returns
 */
function deleteEvent(req, res) {
  deleteFromId(req, res, Events);
}

/**
 * Return all types of events.
 * @param {*} req
 * @param {*} res
 */
function getAllEventTypes(req, res) {
  Types.findAll().then((types) => res.status(200).json(types));
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
 * Delete the type of event with the id passed as parameter from the DB.
 * @param {*} req
 * @param {*} res
 * @returns
 */
function deleteEventType(req, res) {
  deleteFromId(req, res, Types);
}

/**
 * Return the id from the device_id, create the user if he does not exists and update his name if he does.
 * @param {*} req
 * @param {*} res
 * @returns
 */
function connect(req, res) {
  let { device_id, name } = req.body;
  if (!(device_id != undefined && name)) {
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

/**
 * Delete the user with the id passed as parameter from the DB.
 * @param {*} req
 * @param {*} res
 * @returns
 */
function deleteUser(req, res) {
  deleteFromId(req, res, Users);
}

/**
 * Return all users.
 * @param {*} req
 * @param {*} res
 */
function getAllUsers(req, res) {
  Users.findAll().then((users) => res.status(200).json(users));
}

/**
 * Delete the row from the table with the id passed as parameter from the DB.
 * @param {*} req
 * @param {*} res
 * @param {*} table
 * @returns
 */
function deleteFromId(req, res, table) {
  let id = req.params.id;
  if (!Number.isInteger(Number(id))) {
    res.status(200).json({ success: false });
    return;
  }
  table
    .destroy({
      where: { id },
    })
    .then((nbr) => res.status(200).json({ success: nbr != 0 }));
}

module.exports = {
  initDB,
  connect,
  addEvent,
  addEventType,
  getAllEvents,
  getAllEventTypes,
  getAllUsers,
  deleteEvent,
  deleteEventType,
  deleteUser,
};
