import {draw_graph, draw_point, click2point_perm, clear_points} from "./graph.js"

var currentX;
var currentR;

fetch('controller?points').then(response => response.json())
    .then(jsonResponse => {
        jsonResponse.forEach(point => {
            draw_point(point.x, point.y, point.hit ? 'green' : 'red');
        });
    });

document.getElementsByName("r").forEach(radio => {
    radio.addEventListener("change", () => {
        currentR = Number(radio.value);
        draw_graph(currentR);
    });
});

document.getElementById("calculator").addEventListener("click", async (e) => {
    let point = click2point_perm(e.clientX, e.clientY);
    
    let hit = await check_point(point.x, point.y, Number(currentR));
    if (hit !== undefined) {
        draw_point(point.x, point.y, hit ? 'green' : 'red');
    }
});

document.getElementById("reset_button").addEventListener("click", () => {
    clear_points();
});

const x_buttons = document.getElementsByName("x");
x_buttons.forEach(button => {
    button.addEventListener("click", () => {
        x_buttons.forEach(btn => {
            btn.classList.remove('active');
        });
        button.classList.add('active');
        currentX = Number(button.value);
    });
});

document.getElementById('coordinates-form').addEventListener("submit", async (e) => {
    e.preventDefault();

    let x = Number(currentX);
    let y = Number(document.getElementById('y').value);
    let r = Number(currentR);

    let hit = await check_point(x, y, r);
    if (hit !== undefined) {
        draw_point(Number(x), Number(y), hit ? 'green' : 'red');
    }
});

async function check_point (x, y, r) {
    if (isNaN(x)) {
        alert("Select X!");
        return;
    }

    if (y <= -3 || y >= 5) {
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
    var url = 'controller?' + queryParams.toString();

    let hit = await fetch(url).then(response => response.json())
        .then(jsonResponse => {
            appendRow(jsonResponse.x, jsonResponse.y, jsonResponse.curTime, jsonResponse.r, jsonResponse.execTime, jsonResponse.hit);
            return jsonResponse.hit;
        });
    return hit;
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