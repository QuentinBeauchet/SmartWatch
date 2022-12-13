function padTo2Digits(num) {
  return num.toString().padStart(2, "0");
}

export function formatDate(date) {
  return (
    [date.getFullYear(), padTo2Digits(date.getMonth() + 1), padTo2Digits(date.getDate())].join("-") +
    " | " +
    [padTo2Digits(date.getHours()), padTo2Digits(date.getMinutes()), padTo2Digits(date.getSeconds())].join(":")
  );
}

export function reduceObj(obj) {
  return obj.reduce((prev, { id, name }) => {
    prev[id] = name;
    return prev;
  }, {});
}

export function insertMarker(grp, to, from, id, marker) {
  let name = from[id];
  if (grp[to][name]) {
    grp[to][name].addLayer(marker);
  } else {
    grp[to][name] = new L.MarkerClusterGroup();
  }
}
