<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Lab2</title>
        <meta charset="UTF-8">
        <script type="module" src="js/graph.js" defer></script>
        <script type="module" src="js/script.js" defer></script>
        <link rel="stylesheet" href="css/styles.css">
    </head>
    <body>
        <jsp:include page="jsp/header.jsp"/>
        <table class="content-table">
            <tr class="content-cell">
                <td scope="col">
                    <div class="svg-container">
                        <svg id="svg" width="600" height="600" viewBox="-7 -7 14 14">
                            <polyline id="poly" style="fill:none;stroke:#F2476A;stroke-width:0.1"/>

                            <line stroke="black" stroke-width="0.05" x1="-7" y1="0" x2="7" y2="0"/>
                            <line stroke="black" stroke-width="0.05" x1="0" y1="-7" x2="0" y2="7"/>
                            
                            <line stroke="black" stroke-width="0.05" x1="-6" y1="-0.2" x2="-6" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="-5" y1="-0.2" x2="-5" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="-4" y1="-0.2" x2="-4" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="-3" y1="-0.2" x2="-3" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="-2" y1="-0.2" x2="-2" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="-1" y1="-0.2" x2="-1" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="1" y1="-0.2" x2="1" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="2" y1="-0.2" x2="2" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="3" y1="-0.2" x2="3" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="4" y1="-0.2" x2="4" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="5" y1="-0.2" x2="5" y2="0.2"/>
                            <line stroke="black" stroke-width="0.05" x1="6" y1="-0.2" x2="6" y2="0.2"/>

                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="-6" x2="0.2" y2="-6"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="-5" x2="0.2" y2="-5"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="-4" x2="0.2" y2="-4"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="-3" x2="0.2" y2="-3"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="-2" x2="0.2" y2="-2"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="-1" x2="0.2" y2="-1"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="1" x2="0.2" y2="1"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="2" x2="0.2" y2="2"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="3" x2="0.2" y2="3"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="4" x2="0.2" y2="4"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="5" x2="0.2" y2="5"/>
                            <line stroke="black" stroke-width="0.05" x1="-0.2" y1="6" x2="0.2" y2="6"/>

                            <text fill="black" x="-6.25" y="0.625" style="font-size: 0.5px;">-6</text>
                            <text fill="black" x="-5.25" y="0.625" style="font-size: 0.5px;">-5</text>
                            <text fill="black" x="-4.25" y="0.625" style="font-size: 0.5px;">-4</text>
                            <text fill="black" x="-3.25" y="0.625" style="font-size: 0.5px;">-3</text>
                            <text fill="black" x="-2.25" y="0.625" style="font-size: 0.5px;">-2</text>
                            <text fill="black" x="-1.25" y="0.625" style="font-size: 0.5px;">-1</text>
                            <text fill="black" x="0.85" y="0.675" style="font-size: 0.5px;">1</text>
                            <text fill="black" x="1.85" y="0.675" style="font-size: 0.5px;">2</text>
                            <text fill="black" x="2.85" y="0.675" style="font-size: 0.5px;">3</text>
                            <text fill="black" x="3.85" y="0.675" style="font-size: 0.5px;">4</text>
                            <text fill="black" x="4.85" y="0.675" style="font-size: 0.5px;">5</text>
                            <text fill="black" x="5.85" y="0.675" style="font-size: 0.5px;">6</text>

                            <text fill="black" x="0.3" y="6.125" style="font-size: 0.5px;">-6</text>
                            <text fill="black" x="0.3" y="5.125" style="font-size: 0.5px;">-5</text>
                            <text fill="black" x="0.3" y="4.125" style="font-size: 0.5px;">-4</text>
                            <text fill="black" x="0.3" y="3.125" style="font-size: 0.5px;">-3</text>
                            <text fill="black" x="0.3" y="2.125" style="font-size: 0.5px;">-2</text>
                            <text fill="black" x="0.3" y="1.125" style="font-size: 0.5px;">-1</text>
                            <text fill="black" x="0.3" y="-0.875" style="font-size: 0.5px;">1</text>
                            <text fill="black" x="0.3" y="-1.875" style="font-size: 0.5px;">2</text>
                            <text fill="black" x="0.3" y="-2.875" style="font-size: 0.5px;">3</text>
                            <text fill="black" x="0.3" y="-3.875" style="font-size: 0.5px;">4</text>
                            <text fill="black" x="0.3" y="-4.875" style="font-size: 0.5px;">5</text>
                            <text fill="black" x="0.3" y="-5.875" style="font-size: 0.5px;">6</text>
                        </svg>
                    </div>
                </td>
                <td scope="col">
                    <form action="" id="coordinates-form">
                        <div class="x-button">
                            X:
                            <input type="button" name="x" id="x_-5" value="-5">
                            <input type="button" name="x" id="x_-4" value="-4">
                            <input type="button" name="x" id="x_-3" value="-3">
                            <input type="button" name="x" id="x_-2" value="-2">
                            <input type="button" name="x" id="x_-1" value="-1">
                            <input type="button" name="x" id="x_0" value="0">
                            <input type="button" name="x" id="x_1" value="1">
                            <input type="button" name="x" id="x_2" value="2">
                            <input type="button" name="x" id="x_3" value="3">
                        </div>
                        <div class="y-text">
                            Y: 
                            <input type="text" id="y" placeholder="(-3; 5)" required>
                        </div>
                        <div class="r-radio">
                            R: 
                            <label>
                                <input type="radio" name="r" id="r_1" value="1">
                                1
                            </label>
                            <label>
                                <input type="radio" name="r" id="r_2" value="2">
                                2
                            </label>
                            <label>
                                <input type="radio" name="r" id="r_3" value="3">
                                3
                            </label>
                            <label>
                                <input type="radio" name="r" id="r_4" value="4">
                                4
                            </label>
                            <label>
                                <input type="radio" name="r" id="r_5" value="5">
                                5
                            </label>
                        </div>
                        <button type="submit">Отправить</button>
                        <button id="reset_button" type="reset">Очистить</button>
                    </form>
                </td>
            </tr>
        </table>
        <jsp:include page="jsp/pointlist.jsp"/>
    </body>
</html>
