$( document ).ready(function() {
    var map = L.map('map').setView([43.563476, 7.01651], 10);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    function addMarker(long, lat, text) {
        let marker = L.marker([long, lat]).addTo(map);
        marker.bindPopup(text)
    }

    addMarker(43.563476, 7.01651,'<p>Hello world!<br />This is a nice popup.</p>');


});

