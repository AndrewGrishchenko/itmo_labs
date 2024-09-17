let coordinatesForm = document.getElementById('coordinates-form');

if (localStorage.getItem("data") == "null") {
    localStorage.setItem("data", JSON.stringify([]));
}

let data = JSON.parse(localStorage.getItem("data"));
for (row in data) {
    appendRow(data[row][0], data[row][1], data[row][2], data[row][3], data[row][4], data[row][5]);
}

function appendRow (x, y, r, current_time, execution_time, result) {
    let tableRef = document.getElementById('records');
    let tableBodyRef = tableRef.getElementsByTagName('tbody')[0];
    let newRow = tableBodyRef.insertRow(-1);
    newRow.setAttribute('class', 'table-row');

    let cellX = newRow.insertCell(0);
    let cellY = newRow.insertCell(1);
    let cellR = newRow.insertCell(2);
    let cellCurTime = newRow.insertCell(3);
    let cellExecTime = newRow.insertCell(4);
    let cellRes = newRow.insertCell(5);

    cellX.appendChild(document.createTextNode(x));
    cellY.appendChild(document.createTextNode(y));
    cellR.appendChild(document.createTextNode(r));
    cellCurTime.appendChild(document.createTextNode(current_time));
    cellExecTime.appendChild(document.createTextNode(execution_time + ' мкс'));
    cellRes.appendChild(document.createTextNode(result == "hit" ? "Попал" : "Промазал"));
}

let reset_button = document.getElementById('reset_button');
reset_button.addEventListener("click", (e) => {
    let tableRef = document.getElementById('records');
    tableRef.getElementsByTagName("tbody")[0].innerHTML = "";
    localStorage.setItem("data", JSON.stringify([]));
});

coordinatesForm.addEventListener("submit", (e) => {
    e.preventDefault();

    let x = document.getElementById('x').value;
    let y = document.getElementById('y').value;
    let r = document.querySelector('input[name="r"]:checked').value;

    let point = document.getElementById('point');
    
    let cx = x / r * 100 + 150;
    let cy = -(y / r) * 100 + 150;
    if (cx >= 0 && cx <= 300 && cy >= 0 && cy <= 300) {
        point.setAttribute("cx", cx);
        point.setAttribute("cy", cy);
        point.setAttribute("visibility", "shown");
    } else {
        point.setAttribute("visibility", "hidden");
    }

    var url = 'fcgi-bin/server-1.0.jar?x=' + x + '&y=' + y + '&r=' + r;
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function () {
        var status = xhr.status;
        if (status === 200) {
            var jsonResponse = xhr.response;
            if (jsonResponse.status == "ok") {
                appendRow(jsonResponse.x, jsonResponse.y, jsonResponse.r, jsonResponse.current_time, jsonResponse.execution_time, jsonResponse.result);

                let data = JSON.parse(localStorage.getItem("data"));
                data.push([jsonResponse.x, jsonResponse.y, jsonResponse.r, jsonResponse.current_time, jsonResponse.execution_time, jsonResponse.result]);
                localStorage.setItem("data", JSON.stringify(data));
            } else {
                alert(jsonResponse.message);
            }
        } else {
            alert("fcgi request err");
        }
    };
    xhr.send();
});
