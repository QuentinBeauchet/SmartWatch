var markers = {};
var types = {};
var users = {};

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

$( document ).ready(function() {
    // Creation de la map
    var map = L.map('map').setView([43.7101717,7.2619517], 2);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    //fonctions de la maps
    function addMarker(long, lat, comment, date, id, type_id ) {
        let myDate = new Date(date);
        let text = '<div style="display: flex ; flex-wrap:wrap;">' +
                        '<p class="child" style="font-weight: bold">id: '+ id +'</p>' +
                        '<p class="child" style="font-weight: bold">'+ formatDate(myDate) +'</p> <br>' +
                        '<p class="child">'+ comment +'</p>' +
                    '</div>';
        let icon = L.icon({
            iconUrl: types[type_id].icon,
            iconSize:     [38, 38], // size of the icon
            iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
            popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
        });
        let marker = L.marker([long, lat],  {icon: icon}).addTo(map);
        marker.bindPopup(text)
        return marker
    }

    function getMarkersList(){
        $.get( "/api/events", function( data ) {
            data.forEach((elem) =>{
                markers[elem.id] = {};
                markers[elem.id].obj = addMarker(elem.longitude, elem.latitude, elem.comment, elem.date, elem.id, elem.type_id);
                markers[elem.id].type_id = elem.type_id;
                markers[elem.id].user_id = elem.user_id;
            });
        });
    }
    function getTypesList (){
        $.get( "/api/types", function( data ) {
            data.forEach((elem) =>{
                types[elem.id] = {};
                types[elem.id].name = elem.name;
                types[elem.id].icon = elem.icon;
                $("#dropdown-types").append('<li><a class="dropdown-item" >'+ elem.name +'</a></li>')
            });
        });
    }

    //Main
    getTypesList();
    getMarkersList();
});

