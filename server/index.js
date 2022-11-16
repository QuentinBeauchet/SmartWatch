const express = require("express");
const { initdb } = require("./db");
const app = express();
const port = 3000;

app.use(express.static("public"));

app.listen(port, () => {
  console.log(`\nServer listening on http://localhost:${port}`);
});

initdb();
