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
        <jsp:include page="jsp/header.jsp"/>
        <table class="content-table">
            <tr class="content-cell">
                <td scope="col">
                    <div id="calculator" style="width: 600px; height: 400px;"></div>
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
