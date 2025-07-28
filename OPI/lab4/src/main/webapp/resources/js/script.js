function updateFigures() {
    var selectedR = Number(document.querySelector('[id^="coordinates-form:radioGroup:"]:checked').value);
    var fig1 = document.getElementById('fig1');
    var fig2 = document.getElementById('fig2');
    var fig3 = document.getElementById('fig3');

    fig1.setAttribute('points', `0,0 ${selectedR},0 0,${-0.5 * selectedR}`);
    fig2.setAttribute('points', `0,0 ${-selectedR},0 ${-selectedR},${selectedR} 0,${selectedR}`);
    fig3.setAttribute('d', `M${-selectedR},0 A${selectedR},${selectedR} 0 0 1 0,${-selectedR} L0,0 Z`);
}

function click2point_perm (x, y) {
    const rect = svg.getBoundingClientRect();
    const graphX = (x - rect.left) / rect.width * 14 - 7;
    const graphY = (y - rect.top) / rect.height * 14 - 7;
    
    return [graphX, -graphY];
}

function validateForm () {
    var x = document.querySelector('[id$=x_hidden]').value;
    var y = document.querySelector('[id$=y_hidden]').value;
    var r = document.querySelector('[id$=r_hidden]').value;
    
    if (x == "" || y == "" || r == "") {
        alert("Fields must not be empty");
        return false;
    }

    x = Number(x);
    y = Number(y);
    r = Number(r);

    if (x < -3 || x > 3) {
        alert("X must be in [-3, 3]");
        return false;
    }

    if (y <= -5 || y >= 3) {
        alert("Y must be in (-5; 3)");
        return false;
    }
    if (![1, 2, 3, 4, 5].includes(r)) {
        alert("R must be in {1, 2, 3, 4, 5}");
        return false;
    }

    return true;
}

document.addEventListener("DOMContentLoaded", function() {
    document.addEventListener("click", (e) => {
        if (e.target.closest('svg')) {
            let [x, y] = click2point_perm(e.clientX, e.clientY);

            hiddenR = document.querySelector('[id$="r_hidden"]').value;
            if (hiddenR == '') {
                alert("R value must be set");
                return;
            }
            
            x_hidden = document.getElementById('form:x_hidden');
            y_hidden = document.getElementById('form:y_hidden');
            
            x_hidden.value = x;
            y_hidden.value = y;

            updateCoordinates();
            checkHit();
        }
    });
});