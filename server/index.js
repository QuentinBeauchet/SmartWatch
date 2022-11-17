const express = require("express");
var bodyParser = require("body-parser");
const { initdb, getAllEvents, addEvent, connect, addEventType } = require("./db");
const app = express();
const port = 3000;

app.use(express.static("public"));
app.use(bodyParser.json());

/**************Events**************/

app.get("/api/events", getAllEvents);

app.post("/api/events/add", addEvent);

/*************Users**************/

app.post("/api/connect", connect);

/**************Types**************/

app.post("/api/types/add", addEventType);

initdb().then(() => {
  app.listen(port, () => {
    console.log(`\nServer listening on http://localhost:${port}`);
  });
});
