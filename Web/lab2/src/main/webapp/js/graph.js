export var svg = document.getElementById('svg');
var poly = document.getElementById('poly');
let graphAbortController;

export function clear_points () {
    const circles = document.querySelectorAll('circle[name="point"]');
    circles.forEach(circle => {
        circle.remove();
    });
}

export function clear_graph () {
    poly.setAttribute("points", "");
}

export function click2point_perm (x, y) {
    const rect = svg.getBoundingClientRect();
    const graphX = (x - rect.left) / rect.width * 14 - 7;
    const graphY = (y - rect.top) / rect.height * 14 - 7;
    
    return [graphX, -graphY];
}

export function draw_point (x, y, color) {
    const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("name", "point");
    circle.setAttribute("cx", x);
    circle.setAttribute("cy", -y);
    circle.setAttribute("r", 0.1);
    circle.setAttribute("fill", color);
    svg.appendChild(circle);
}

export async function draw_graph (r) {
    if (graphAbortController) {
        graphAbortController.abort();
    }

    graphAbortController = new AbortController();
    const { signal } = graphAbortController;
    
    let points = part1(r) + part2(r) + part3(r) + part4(r);
    let pointsArray = points.split(" ");
    let tmp_points = "";
    
    for (let i = 0; i < pointsArray.length; i++) {
        if (signal.aborted) {
            return;
        }

        tmp_points += pointsArray[i] + ' '
        document.getElementById('poly').setAttribute('points', tmp_points);
        await delay(0.01);
    }
}

function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function eqs (x, y, r) {
    let firstAreaA = Math.abs(x) / (r / 7) - 3;
    let firstSecondAreaA = Math.abs(y / (r / 7) + 3 / 7 * Math.sqrt(33));
    let firstSecondAreaB = Math.pow((y / (r / 7)) / 3, 2);
    let firstSecondAreaC = Math.sqrt(Math.abs(firstSecondAreaA) / firstSecondAreaA);
    let firstArea = (y / (r / 7)) >= 0 && Math.pow(x / r, 2)
            * Math.sqrt(Math.abs(firstAreaA) / (firstAreaA))
            + firstSecondAreaB
            * firstSecondAreaC
            - 1 <= 0;

    let secondAreaA = Math.abs(x) / (r / 7) - 4;
    let secondArea = (y / (r / 7)) < 0 && Math.pow(x / r, 2)
            * Math.sqrt(Math.abs(secondAreaA) / (secondAreaA))
            + firstSecondAreaB
            * firstSecondAreaC
            - 1 <= 0;

    let thirdArea = (y / (r / 7)) < 0 && Math.abs((x / (r / 7)) / 2)
            - (3 * Math.sqrt(33) - 7) * Math.pow((x / (r / 7)), 2) / 112
            - 3 + Math.sqrt(1 - Math.pow(Math.abs(Math.abs(x) / (r / 7) - 2) - 1, 2))
            - y / (r / 7) <= 0;

    let fourthArea = Math.abs(x) / (r / 7) <= 1 && Math.abs(x) / (r / 7) >= 0.75
            && y / (r / 7) <= 3 && y / (r / 7) >= 0
            && 9 - 8 * Math.abs(x) / (r / 7) >= y / (r / 7);

    let fifthArea = y / (r / 7) >= 0
            && Math.abs(x) / (r / 7) <= 0.75 && Math.abs(x) / (r / 7) >= 0.5
            && 3 * Math.abs(x) / (r / 7) + 0.75 >= y / (r / 7);

    let sixthArea = x / (r / 7) <= 0.5 && x / (r / 7) >= -0.5
            && y / (r / 7) >= 0
            && y / (r / 7) <= 2.25;

    let seventhAreaA = Math.abs(x) / (r / 7) - 1;
    let seventhArea = y / (r / 7) >= 0 && 6 * Math.sqrt(10) / 7
            + (1.5 - 0.5 * Math.abs(x) / (r / 7))
            * Math.sqrt(Math.abs(seventhAreaA) / seventhAreaA)
            - 6 * Math.sqrt(10) / 14
            * Math.sqrt(4 - Math.pow(seventhAreaA, 2)) >= y / (r / 7);

    return firstArea || secondArea || thirdArea
            || fourthArea || fifthArea || sixthArea || seventhArea;
}

function part1 (r, step=0.01) {
    let points = "";
    for (let x = 0; x <= r; x += step) {
        let maxY = null;
        for (let y = r; y >= 0; y -= step) {
            if (eqs(x, y, r) && y >= maxY) {
                maxY = y;
            }
        }

        if (maxY != null) {
            points += x + "," + -maxY + " ";
        }
    }
    return points;
}

function part2 (r, step=0.01) {
    let points = "";
    for (let x = r; x >= 0; x -= step) {
        let minY = null;
        for (let y = 0; y >= -r; y -= step) {
            if (eqs(x, y, r) && y <= minY) {
                minY = y;
            }
        }

        if (minY != null) {
            points += x + "," + -minY + " ";
        }
    }
    return points;
}

function part3 (r, step=0.01) {
    let points = "";
    for (let x = 0; x >= -r; x -= step) {
        let minY = null;
        for (let y = -r; y <= 0; y += step) {
            if (eqs(x, y, r) && y <= minY) {
                minY = y;
            }
        }

        if (minY != null) {
            points += x + "," + -minY + " ";
        }
    }
    return points;
}

function part4 (r, step=0.01) {
    let points = "";
    for (let x = -r; x <= 0; x += step) {
        let maxY = null;
        for (let y = 0; y <= r; y += step) {
            if (eqs(x, y, r) && y >= maxY) {
                maxY = y;
            }
        }

        if (maxY != null) {
            points += x + "," + -maxY + " ";
        }
    }
    return points;
}