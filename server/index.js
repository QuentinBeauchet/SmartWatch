const express = require("express");
var bodyParser = require("body-parser");
const path = require("path");
var os = require("os");
const DB = require("./db");

const app = express();
const port = 3000;


app.set("view engine", "html");
app.set("views", path.join(__dirname, "views"));
app.use(express.static("public"));
app.use("/api/assets", express.static("public/assets"));
app.use(bodyParser.json());

/********************************Routes********************************/

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname+'/views/index.html'));
});


/**************Events**************/

app.get("/api/events", DB.getAllEvents);

app.post("/api/events/add", DB.addEvent);

app.get("/api/events/:id/delete", DB.deleteEvent);

/*************Users**************/

app.post("/api/connect", DB.connect);

app.get("/api/users", DB.getAllUsers);

app.get("/api/users/:id/delete", DB.deleteUser);

/**************Types**************/

app.get("/api/types", DB.getAllEventTypes);

app.post("/api/types/add", DB.addEventType);

app.get("/api/types/:id/delete", DB.deleteEventType);

/**********************************************************************/

/**
 * @returns The Network IP of the express server.
 */
const address = () => {
  return (
    Object.values(os.networkInterfaces()).flatMap((type) =>
      type.filter(({ family, netmask }) => family == "IPv4" && netmask == "255.255.255.0")
    )[0]?.address || "localhost"
  );
};

DB.initDB().then(() => {
  app.listen(port, () => {
    console.log(`\nServer listening on http://${address()}:${port}`);
  });
});
