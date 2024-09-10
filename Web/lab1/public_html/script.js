let coordinatesForm = document.getElementById('coordinates-form');

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

    var url = 'http://localhost/point?x=' + x + '&y=' + y + '&r=' + r;
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function () {
        var status = xhr.status;
        if (status === 200) {
            var jsonResponse = xhr.response;
            if (jsonResponse.status == "ok") {
                let tableRef = document.getElementById('records');
                let newRow = tableRef.insertRow(-1);

                let cellX = newRow.insertCell(0);
                let cellY = newRow.insertCell(1);
                let cellR = newRow.insertCell(2);
                let cellCurTime = newRow.insertCell(3);
                let cellExecTime = newRow.insertCell(4);
                let cellRes = newRow.insertCell(5);

                cellX.appendChild(document.createTextNode(jsonResponse.x));
                cellY.appendChild(document.createTextNode(jsonResponse.y));
                cellR.appendChild(document.createTextNode(jsonResponse.r));
                cellCurTime.appendChild(document.createTextNode(jsonResponse.current_time));
                cellExecTime.appendChild(document.createTextNode(jsonResponse.execution_time + ' мкс'));
                cellRes.appendChild(document.createTextNode(jsonResponse.result));
            } else {
                alert(jsonResponse.message);
            }
        } else {
            alert("fcgi request err");
        }
    };
    xhr.send();
});