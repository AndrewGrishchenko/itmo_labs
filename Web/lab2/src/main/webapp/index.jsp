<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Lab2</title>
        <meta charset="UTF-8">
        <script src="https://www.desmos.com/api/v1.9/calculator.js?apiKey=dcb31709b452b1cf9dc26972add0fda6"></script>
        <script type="module" src="js/graph.js" defer></script>
        <script type="module" src="js/script.js" defer></script>
        <link rel="stylesheet" href="css/styles.css">
    </head>
    <body>
        <header class="header">
            <div style="display: flex">
                <span>Грищенко Андрей P3208</span>
                <span>Вариант 223317</span>
            </div>
        </header>
        <table class="content-table">
            <tr class="content-cell">
                <td scope="col">
                    <div id="calculator" style="width: 600px; height: 400px;"></div>
                </td>
                <td scope="col">
                    <form action="" id="coordinates-form">
                        <div class="x-button">
                            X:
                            <input type="button" name="x" id="x" value="-5">
                            <input type="button" name="x" id="x" value="-4">
                            <input type="button" name="x" id="x" value="-3">
                            <input type="button" name="x" id="x" value="-2">
                            <input type="button" name="x" id="x" value="-1">
                            <input type="button" name="x" id="x" value="0">
                            <input type="button" name="x" id="x" value="1">
                            <input type="button" name="x" id="x" value="2">
                            <input type="button" name="x" id="x" value="3">
                        </div>
                        <div class="y-text">
                            Y: 
                            <input type="text" id="y" placeholder="(-3; 5)" required>
                        </div>
                        <div class="r-radio">
                            R: 
                            <label>
                                <input type="radio" name="r" value="1" checked>
                                1
                            </label>
                            <label>
                                <input type="radio" name="r" value="2">
                                2
                            </label>
                            <label>
                                <input type="radio" name="r" value="3">
                                3
                            </label>
                            <label>
                                <input type="radio" name="r" value="4">
                                4
                            </label>
                            <label>
                                <input type="radio" name="r" value="5">
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
