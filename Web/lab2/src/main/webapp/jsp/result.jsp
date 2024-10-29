<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.andrew.model.Point" %>
<%@ page import="com.andrew.model.PointList" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <link rel="stylesheet" href="css/styles.css">
    </head>
    <body>
        <jsp:include page="header.jsp"/>
        <table class="records-table">
            <thead>
                <tr class="table-header">
                    <th colspan="3" style="font-size: 25px">Введенные данные</th>
                </tr>
                <tr class="table-header">
                    <th scope="col">X</td>
                    <th scope="col">Y</td>
                    <th scope="col">R</td>
                </tr>
            </thead>
            <tbody>
                <% Point point = ((PointList) request.getSession().getAttribute("points")).getLastPoint(); %>
                <tr class="table-row">
                    <td> <%= point.getX() %> </td>
                    <td> <%= point.getY() %> </td>
                    <td> <%= point.getR() %> </td>
                </tr>
            </tbody>
        </table>

        <table class="records-table" id="records">
            <thead>
                <tr class="table-header">
                    <th colspan="3" style="font-size:25px">Результат</th>
                </tr>
                <tr class="table-header">
                    <th scope="col">Текущее время</td>
                    <th scope="col">Время исполнения</td>
                    <th scope="col">Результат попадания</td>
                </tr>
            </thead>
            <tbody>
                <tr class="table-row">
                    <td> <%= point.getCurTime() %> </td>
                    <td> <%= point.getExecTime() %> мкс </td>
                    <% if (point.isHit()) { %>
                    <td style="color: green"> Попал </td>
                    <% } else { %> 
                    <td style="color: red"> Промазал </td>
                    <% } %>
                </tr>
            </tbody>
        </table>

        <div style="text-align: center">
            <button id="backButton">Назад</button>
        </div>
        <script type="text/javascript">
            document.getElementById("backButton").onclick = function () {
                location.href = "/";
            };
        </script>
    </body>
</html>