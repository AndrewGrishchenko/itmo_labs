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

function unselectCheckboxes (selected) {
    var checked = document.querySelectorAll('[id*="x_"]:checked');
    
    checked.forEach((box) => {
        if (box.id.split('_')[1] != selected) box.checked = false;
    });
}

document.addEventListener("DOMContentLoaded", function() {
    // document.querySelector('[id$="radioGroup"]').addEventListener("change", updateFigures);

    document.getElementById("svg").addEventListener("click", (e) => {
        let [x, y] = click2point_perm(e.clientX, e.clientY);
        console.log(x, y);
    });
});