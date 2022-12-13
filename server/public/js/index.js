var markers = {};
var types = {};
var users = {};
var typesNames = [];
var overlays = {};
var markersCluster = new L.MarkerClusterGroup();

//tools
function padTo2Digits(num) {
  return num.toString().padStart(2, '0');
}
function formatDate(date) {
  return (
      [
        date.getFullYear(),
        padTo2Digits(date.getMonth() + 1),
        padTo2Digits(date.getDate()),
      ].join('-') +
      ' | ' +
      [
        padTo2Digits(date.getHours()),
        padTo2Digits(date.getMinutes()),
        padTo2Digits(date.getSeconds()),
      ].join(':')
  );
}

//gen le marker et l'ajoute au layer
function addMarker(long, lat, comment, date, id, type_id, map ) {
  let myDate = new Date(date);
  let text = '<div style="display: flex ; flex-wrap:wrap;">' +
      '<p class="child" style="font-weight: bold">id: '+ id +'</p>' +
      '<p class="child" style="font-weight: bold">'+ formatDate(myDate) +'</p> <br>' +
      '<p class="child">'+ comment +'</p>' +
      '</div>';
  let marker ;
  if (types[type_id]!=null){
    let icon = L.icon({
      iconUrl: types[type_id].icon,
      iconSize:     [38, 38], // size of the icon
      iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
      popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
    });
    marker = L.marker([long, lat], {icon: icon}).addTo(overlays[types[type_id].name]);
  }else{
    marker = L.marker([long, lat]).addTo(map);
  }
  marker.bindPopup(text)
  return marker
}

//recup les markers et ajoute les layers sur la map
function getMarkersList(map){
  $.get( "/api/events", function( data ) {
    data.forEach((elem) =>{
      markers[elem.id] = {};
      markers[elem.id].obj = addMarker(elem.longitude, elem.latitude, elem.comment, elem.date, elem.id, elem.type_id,map);
      markers[elem.id].type_id = elem.type_id;
      markers[elem.id].user_id = elem.user_id;
      markers[elem.id].hideType = false;
      markers[elem.id].hideUser = false;
      markersCluster.addLayer(markers[elem.id].obj);
    });
    markersCluster.addTo(map)
  });
}

//recup les users et les ajoute à la liste
function getUsersList(){
  $.get( "/api/users", function( data ) {
    data.forEach((elem) =>{
      users[elem.id] = {};
      users[elem.id].name = elem.name;
      users[elem.id].device_id = elem.device_id;
      users[elem.id].name = elem.name;

      let newItem = $(document.createElement("button"));
      newItem.attr("class","list-group-item list-group-item-action usersItem active");
      newItem.attr("type","button");
      newItem.append(elem.name );
      newItem.on("click", (e) => {
        if (newItem.attr("class").split(" ").includes("active")) {
          newItem.removeClass("active");
          removeUserLayers(map,elem.id);
        }else{
          newItem.addClass("active");
          putBackUserLayers(map,elem.id);
        }
      });
      $(".container-users").append(newItem);
    });
    return typesNames;
  });
}

//recup les types d'event
function getTypesList (map){
  $.get( "/api/types", function( data ) {
    data.forEach((elem) =>{
      types[elem.id] = {};
      types[elem.id].name = elem.name;
      types[elem.id].icon = elem.icon;
      overlays[elem.name] = L.layerGroup();
      typesNames.push(elem.name);
      let newButton = $(document.createElement("button"));
      newButton.attr("class","btn btn-success activated");
      newButton.attr("type","button");
      newButton.text(elem.name);
      newButton.on("click", (e) => {
            if (newButton.attr("class").split(" ").includes("activated")) {
              removeLayers(map,elem.id)
              newButton.removeClass("btn-success");
              newButton.addClass("btn-secondary")
            } else {
              newButton.removeClass("btn-secondarys");
              newButton.addClass("btn-success");
              putBackLayers(map, elem.id);
            }
          newButton.toggleClass("activated");
          }
      );
      $(".container-types").append(newButton);
    });
    return typesNames;
  });
}

function removeUserLayers(map,user_id){
  Object.keys(markers).forEach((elem)=>{
    if( markers[elem].user_id == user_id){
      markers[elem].hideUser = true;
      hideMarker(markers[elem]);
    }
  });
}

//remove layers with type
function removeLayers(map,type_id){
  Object.keys(markers).forEach((elem)=>{
    if( markers[elem].type_id == type_id){
      markers[elem].hideType = true;
      hideMarker(markers[elem]);
    }
  });
}

function hideMarker(marker){
  if (marker.hideType == true || marker.hideUser == true){
    if (marker.hideType == false || marker.hideUser == false){
      marker.oldLat = marker.obj.getLatLng();
    }
    marker.obj.setLatLng(
        L.latLng(
            parseFloat(100000),
            parseFloat(100000)
        ));
  }
}
function putBackUserLayers(map,user_id){
  Object.keys(markers).forEach((elem)=>{
    if( markers[elem].user_id == user_id  ){
      markers[elem].hideUser = false;
      showMarker(markers[elem])
    }
  });
}

//put back the layers on the map
function putBackLayers(map, type_id){
  Object.keys(markers).forEach((elem)=>{
    if( markers[elem].type_id == type_id  ){
      markers[elem].hideType = false;
      showMarker(markers[elem])
    }
  });
}

function showMarker(marker){
  if (marker.hideType == false && marker.hideUser == false){
    marker.obj.setLatLng(
        marker.oldLat
    );
  }
}

//gen tous les layers
function generateLayers(){
  let myControlLayer = L.control.layers();
  Object.keys(overlays).forEach((elem) =>{
    myControlLayer.addOverlay(overlays[elem],elem);
  });
  console.log(overlays);
  return myControlLayer;
}

function createHistogram(events, types) {
  var data = numberEvents(events,types);
  var margin = {top: 10, right: 30, bottom: 30, left: 70},
      width = 460 - margin.left - margin.right,
      height = 400 - margin.top - margin.bottom;

// append the svg object to the body of the page
  var svg = d3.select("#diagrams")
      .append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom+30)
      .append("g")
      .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");

  var x = d3.scaleBand()
      .domain(data.map(d => {
        return d.name
      }))
      .range([0, width])
  svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));
  var y = d3.scaleLinear()
      .range([height, 0]);
  y.domain([0, Math.max(...data.map(d => d.nbr))]);   // d3.hist has to be called before the Y axis obviously
  svg.append('g')
      .attr('transform', `translate(0,0)`)
      .call(d3.axisLeft(y)
          .tickValues(y.ticks().filter(tick => Number.isInteger(tick)))
          .tickFormat(d3.format('d')))

  // append the bar rectangles to the svg element
  svg.append("g")
      .attr("fill", "steelblue")
      .selectAll("rect")
      .data(data)
      .join("rect")
      .attr("x", d => x(d.name))
      .attr("y", d => y(d.nbr))
      .attr("height", d => y(0) - y(d.nbr))
      .attr("width", x.bandwidth())


  svg.append("text")
      .attr("transform", "translate(" + (width/2) + " ," + (height+40) + ")")
      .style("text-anchor", "middle")
      .text("Events");

  svg.append("text")
      .attr("transform", "rotate(-90)")
      .attr("x", -(height/2))
      .attr("y", -40)
      .style("text-anchor", "middle")
      .text("Number per event");


}

function createPie(events, users) {
  var numberEventsPerUsers = eventPerUser(events, users)
  var width = 550
  var height = 550
  var margin = 100
  var labelHeight = 18
  var radius = Math.min(width, height) / 2 - margin

  var svg = d3.select("#diagrams")
      .append("svg")
      .style("border", "2px solid black")
      .style("margin-top", "1em")
      .attr("width", width)
      .attr("height", height)
      .append("g")
      .attr("transform", "translate(" + width / 3 + "," + height / 2 + ")")


  var color = d3.scaleOrdinal()
      .domain(Object.keys(numberEventsPerUsers))
      .range(d3.schemeSet2);

  var pie = d3.pie()
      .value(function (d) {
        return d.value;
      })
  var data_ready = pie(d3.entries(numberEventsPerUsers))

  var arcGenerator = d3.arc()
      .innerRadius(0)
      .outerRadius(radius)

  svg
      .selectAll('mySlices')
      .data(data_ready)
      .enter()
      .append('path')
      .attr('d', arcGenerator)
      .attr('fill', function (d) {
        return (color(d.data.key))
      })
      .attr("stroke", "black")
      .style("stroke-width", "1px")
      .style("opacity", 0.7)


  svg.append("g")
      .attr("transform", "translate(" + -80 + "," + 220 + ")")
      .append("text")
      .text("Number of events per user")
      .attr("class", "title")

  const legend = svg
      .append('g')
      .attr('transform', `translate(${width / 3},${-(height / 2.5)})`);

  legend
      .selectAll(null)
      .data(data_ready)
      .enter()
      .append('rect')
      .attr('y', d => labelHeight * d.index * 1.8)
      .attr('width', labelHeight)
      .attr('height', labelHeight)
      .attr('fill', d => color(d.data.key))
      .attr('stroke', 'grey')
      .style('stroke-width', '1px');

  legend
      .selectAll(null)
      .data(data_ready)
      .enter()
      .append('text')
      .text(d => d.data.key)
      .attr('x', labelHeight * 1.2)
      .attr('y', d => labelHeight * d.index * 1.8 + labelHeight)
      .style('font-family', 'sans-serif')
      .style('font-size', `${labelHeight}px`);
}

function numberEvents(events, types) {
  var allTypes = [];
  types.map(type => {
    var newType = {};
    allTypes.push(newType[type.type_id] = type.name)
  })

  var tab = [];
  events.forEach(even => {
    var t = tab.find(t => t.name===allTypes[even.type_id])
    if(t) t.nbr+=1;
    else {
      var numberEvent = {};
      numberEvent["name"] = allTypes[even.type_id]
      numberEvent["nbr"] = 1;
      tab.push(numberEvent)
    }
  })
  return tab;
}

function eventPerUser(events, users) {

  var eventPerUser = {};
  events.forEach(even => {
    eventPerUser[even.user_id] = even.user_id in eventPerUser ? eventPerUser[even.user_id] + 1 : 1;
  })


  Object.keys(eventPerUser).forEach(id => {
    var user = users.find(x => x.id === parseInt(id))
    eventPerUser[user.name] = eventPerUser[id];
    delete eventPerUser[id];
  })

  return eventPerUser
}


$( document ).ready(async () =>  {
  let urls = ["/api/events", "api/users", "api/types"];
  let reqs = await Promise.all(urls.map((url) => fetch(url)));
  let [events, users, types] = await Promise.all(reqs.map((req) => req.json()));

  // Creation de la map
  var map = L.map('map').setView([43.7101717,7.2619517], 4);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(map);
  getTypesList(map);
  getMarkersList(map);
  getUsersList();

  createPie(events, users)
  createHistogram(events, types)
});