const express = require("express");
var bodyParser = require("body-parser");
const DB = require("./db");
const app = express();
const port = 3000;

app.use(express.static("public"));
app.use(bodyParser.json());

/********************************Routes********************************/

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

DB.initDB().then(() => {
  app.listen(port, () => {
    console.log(`\nServer listening on http://localhost:${port}`);
  });
});
