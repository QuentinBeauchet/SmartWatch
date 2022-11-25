module.exports = {
  table: process.env.MYSQLDATABASE || "watch",
  user: process.env.MYSQLUSER || "root",
  password: process.env.MYSQLPASSWORD || "root",
  host: process.env.MYSQLHOST || "localhost",
  port: process.env.MYSQLPORT,
  dialect: process.env.PORT ? "mysql" : "mariadb",
};
