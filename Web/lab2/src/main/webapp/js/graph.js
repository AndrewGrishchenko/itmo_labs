var elt = document.getElementById('calculator');

var calculator = Desmos.GraphingCalculator(elt, {
    keypad: false,
    expressions: false,
    settingsMenu: false,
    zoomButtons: false,
    expressionsTopbar: false,
    pointsOfInterest: false,
    lockViewport: true,
    zoomFit: true,
    trace: false,
    xAxisStep: 1,
    yAxisStep: 1,
});

var points = [];

export function clear_points () {
    points.forEach((expression) => {
        calculator.removeExpression(expression);
    });
}

export function clear_graph () {
    calculator.setBlank();
}

export function click2point_perm (x, y) {
    let calcRect = elt.getBoundingClientRect();
    return calculator.pixelsToMath({
        x: x - calcRect.left,
        y: y - calcRect.top
    });
}

export function draw_point (x, y, color) {
    points.push({
        id: 'p' + (points.length + 1),
        latex: `(${x}, ${y})`,
        color: color
    });
    calculator.setExpression(points[points.length - 1]);
}

export function draw_graph (r) {
    let k = 49 / r
    calculator.setExpression({
        id: 'k',
        latex: 'k=' + k.toString()
    });
    calculator.setExpression({
        id: 's',
        latex: '\\s(x)=\\sqrt{(\\abs(x*k/7))/(x*k/7)}',
        hidden: true
    });
    calculator.setExpression({
        id: 'graph2',
        latex: '(x*k/49)^2*s(\\abs(x*k/7)-3)+(y*k/21)^2*s(y*k/7+3*\\sqrt{33}/7)-1=0',
        color: '#F2476A'
    });
    calculator.setExpression({
        id: 'graph3',
        latex: '\\abs(x*k/14)-((3*\\sqrt{33}-7)/112)*(x*k/7)^2-3+\\sqrt{1-(\\abs(\\abs(x*k/7)-2)-1)^2}-y*k/7=0',
        color: '#F2476A'
    });
    calculator.setExpression({
        id: 'graph4',
        latex: '9*s((1-\\abs(x*k/7))*(\\abs(x*k/7)-.75))-8*\\abs(x*k/7)-y*k/7=0',
        color: '#F2476A'
    });
    calculator.setExpression({
        id: 'graph5',
        latex: '3*\\abs(x*k/7)+.75*s((.75-\\abs(x*k/7))*(\\abs(x*k/7)-.5))-y*k/7=0',
        color: '#F2476A'
    });
    calculator.setExpression({
        id: 'graph6',
        latex: '2.25*s((.5-x*k/7)*(x*k/7+.5))-y*k/7=0',
        color: '#F2476A'
    });
    calculator.setExpression({
        id: 'graph7',
        latex: '6*\\sqrt{10}/7+(1.5-.5*\\abs(x*k/7))*s(\\abs(x*k/7)-1)-6*\\sqrt{10}/14*\\sqrt{4-(\\abs(x*k/7)-1)^2}-y*k/7=0',
        color: '#F2476A'
    });

    calculator.setMathBounds({
        left: -r - 1,
        right: r + 1,
        bottom: -r - 1,
        top: r + 1
    });
}