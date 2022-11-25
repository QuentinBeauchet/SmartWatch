import { formatDate, reduceObj, insertMarker } from "./utils.js";

function initMap() {
  var map = L.map("leafletMap").setView([43.7101717, 7.2619517], 4);
  L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(map);

  return map;
}

/**
 * Add the buttons for the users.
 * @param {*} users
 */
function addUsers(users) {
  let parent = document.getElementById("users");
  for (let { name } of users) {
    let user = document.createElement("ul");
    user.innerHTML = name;
    user.id = `user_${name}`;
    user.classList.add("unselected");
    parent.appendChild(user);
  }
}

/**
 * Add the buttons for the types.
 * @param {*} types
 */
function addTypes(types) {
  let parent = document.getElementById("types");
  for (let { icon, name } of types) {
    let type = document.createElement("ul");
    let img = document.createElement("img");
    img.src = icon;

    type.appendChild(img);
    type.innerHTML += name;
    type.id = `type_${name}`;
    type.classList.add("unselected");
    parent.appendChild(type);
  }
}

/**
 * Create the markers and put them inside an object sorted by categories.
 * @param {*} events
 * @param {*} users
 * @param {*} types
 * @returns
 */
function createMarkers(events, users, types) {
  let groupedOverlays = {
    Types: {},
    Users: {},
  };

  let users_ids = reduceObj(users);
  let types_ids = reduceObj(types);

  for (let { user_id, type_id, latitude, longitude, date, comment } of events) {
    var marker = new L.Marker(new L.LatLng(latitude, longitude));

    marker.bindPopup(`<div>
    <h1>${types_ids[type_id]}</h1>
    <div class="markerSmallText">
    <p>${formatDate(new Date(date))}</p>
    <p>${users_ids[user_id]}</p>
    </div>
    <p class="markerComment">${comment || ""}</p>
    </div>`);

    insertMarker(groupedOverlays, "Types", types_ids, type_id, marker);
    insertMarker(groupedOverlays, "Users", users_ids, user_id, marker);
  }

  return groupedOverlays;
}

/**
 * Add listeners to layers of the map so the buttons change aswell.
 * @param {*} map
 */
function addListenersToLayers(map) {
  map.on("overlayadd", (e) => {
    let el = document.getElementById(`type_${e.name}`) || document.getElementById(`user_${e.name}`);
    el?.classList.remove("unselected");
  });
  map.on("overlayremove", (e) => {
    let el = document.getElementById(`type_${e.name}`) || document.getElementById(`user_${e.name}`);
    el?.classList.add("unselected");
  });
}

/**
 * Add listeners to buttons around the Map
 */
function addListenersToButtons(map, groupedOverlays) {
  for (let key of Object.keys(groupedOverlays)) {
    for (let [type, cluster] of Object.entries(groupedOverlays[key])) {
      let el = document.getElementById(`type_${type}`) || document.getElementById(`user_${type}`);
      el.addEventListener("click", (e) =>
        e.target.classList.contains("unselected") ? cluster.addTo(map) : cluster.remove()
      );
    }
  }
}

/**
 * Add the events to the map.
 * @param {*} map
 * @param {*} events
 * @param {*} users
 * @param {*} types
 */
function addEvents(map, events, users, types) {
  let groupedOverlays = createMarkers(events, users, types);

  let controlLayer = L.control.groupedLayers(null, groupedOverlays, {
    groupCheckboxes: true,
  });

  addListenersToButtons(map, groupedOverlays);
  addListenersToLayers(map);

  controlLayer.addTo(map);
}

window.onload = async () => {
  let urls = ["/api/events", "api/users", "api/types"];
  let reqs = await Promise.all(urls.map((url) => fetch(url)));
  let [events, users, types] = await Promise.all(reqs.map((req) => req.json()));

  addUsers(users);
  addTypes(types);

  let map = initMap();
  addEvents(map, events, users, types);
};
