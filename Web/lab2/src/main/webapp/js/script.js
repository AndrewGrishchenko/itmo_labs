import {draw_graph, draw_point, click2point_perm, clear_points, clear_graph, svg} from "./graph.js"

var currentX = localStorage.getItem("x");
var currentY = localStorage.getItem("y");
var currentR = localStorage.getItem("r");

if (currentX != null) {
    var btnX = document.getElementById("x_" + currentX);
    if (btnX != null) {
        btnX.classList.add('active');
    }
}

if (currentY != null) {
    document.getElementById("y").value = currentY;
}

if (currentR != null) {
    var radioR = document.getElementById("r_" + currentR);
    if (radioR != null) {
        radioR.checked = true;
    }
    draw_graph(Number(currentR));
}

function updateHistory () {
    clearHistoryTable();
    clear_points();
    fetch('controller?points').then(response => response.json())
        .then(jsonResponse => {
            jsonResponse.forEach(point => {
                draw_point(point.x, point.y, point.hit ? 'green' : 'red');
                appendRow(point.x, point.y, point.curTime, point.r, point.execTime, point.hit);
            });
        });
}

updateHistory();

svg.addEventListener("click", async (e) => {
    let [x, y] = click2point_perm(e.clientX, e.clientY);

    let hit = await check_point(x, y, currentR, "checkPoint");
    if (hit !== undefined) {
        draw_point(x, y, hit ? 'green' : 'red');
    }
});

const x_buttons = document.getElementsByName("x");
x_buttons.forEach(button => {
    button.addEventListener("click", () => {
        x_buttons.forEach(btn => {
            btn.classList.remove('active');
        });
        button.classList.add('active');
        currentX = Number(button.value);
        localStorage.setItem("x", currentX);
    });
});

document.getElementById("y").addEventListener("change", () => {
    currentY = Number(document.getElementById("y").value);
    localStorage.setItem("y", currentY);
});

document.getElementsByName("r").forEach(radio => {
    radio.addEventListener("change", () => {
        currentR = Number(radio.value);
        localStorage.setItem("r", currentR);
        draw_graph(currentR);
    });
});

document.getElementById('coordinates-form').addEventListener("submit", (e) => {
    e.preventDefault();

    check_point(currentX, currentY, currentR, "submit");
});

document.getElementById("reset_button").addEventListener("click", () => {
    clear_graph();
    document.getElementsByName("x").forEach(btn => {
        btn.classList.remove('active');
    });
    currentX = currentY = currentR = null;
    localStorage.clear();
});

document.getElementById("clearHistoryButton").addEventListener("click", () => {
    fetch("controller?clear");
    updateHistory();
});

async function check_point (x, y, r, action) {
    x = Number(x);
    y = Number(y);
    r = Number(r);
    
    if (isNaN(x)) {
        alert("Select X!");
        return;
    }

    if (y <= -3 || y >= 5 || isNaN(y)) {
        alert("Y must be in (-3; 5)!");
        return;
    }

    if (isNaN(r)) {
        alert("Select R!");
        return;
    }

    x = x.toFixed(2);
    y = y.toFixed(2);

    const queryParams = new URLSearchParams();
    queryParams.append('x', x);
    queryParams.append('y', y);
    queryParams.append('r', r);
    queryParams.append('action', action);
    var url = 'controller?' + queryParams.toString();

    if (action == "submit") {
        window.location.href = url;
    } else if (action == "checkPoint") {
        let hit = await fetch(url).then(response => response.json())
            .then(jsonResponse => {
                appendRow(jsonResponse.x, jsonResponse.y, jsonResponse.curTime, jsonResponse.r, jsonResponse.execTime, jsonResponse.hit);
                return jsonResponse.hit;
            });
        return hit;
    }
}

function clearHistoryTable () {
    let tableRef = document.getElementById('records');
    while (tableRef.rows.length > 1) {
        tableRef.deleteRow(1);
    }
}

function appendRow (x, y, curTime, r, execTime, hit) {
    let tableRef = document.getElementById('records');
    let tableBodyRef = tableRef.getElementsByTagName('tbody')[0];
    let newRow = tableBodyRef.insertRow(-1);
    newRow.setAttribute('class', 'table-row');

    let cellX = newRow.insertCell(0);
    let cellY = newRow.insertCell(1);
    let cellCurTime = newRow.insertCell(2);
    let cellR = newRow.insertCell(3);
    let cellExecTime = newRow.insertCell(4);
    let cellRes = newRow.insertCell(5);

    cellX.appendChild(document.createTextNode(x));
    cellY.appendChild(document.createTextNode(y));
    cellR.appendChild(document.createTextNode(r));
    cellCurTime.appendChild(document.createTextNode(curTime));
    cellExecTime.appendChild(document.createTextNode(execTime + ' мкс'));

    if (hit) {
        cellRes.setAttribute('style', 'color: green');
    } else {
        cellRes.setAttribute('style', 'color: red');
    }
    cellRes.appendChild(document.createTextNode(hit ? "Попал" : "Промазал"));
}