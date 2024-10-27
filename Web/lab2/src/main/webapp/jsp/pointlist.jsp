<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.andrew.model.Point" %>
<%@ page import="com.andrew.model.PointList" %>

<table class="records-table" id="records">
    <thead>
        <tr class="table-header">
            <th scope="col">X</td>
            <th scope="col">Y</td>
            <th scope="col">Текущее время</td>
            <th scope="col">R</td>
            <th scope="col">Время исполнения</td>
            <th scope="col">Результат попадания</td>
        </tr>
    </thead>
    <tbody>
        <% PointList points = (PointList) request.getSession().getAttribute("points");
        if (points != null) {
        %>
        <% for (Point point : points.getPoints()) { %>
        <tr class="table-row">
            <td> <%= point.getX() %> </td>
            <td> <%= point.getY() %> </td>
            <td> <%= point.getCurTime() %> </td>
            <td> <%= point.getR() %> </td>
            <td> <%= point.getExecTime() %> мкс </td>
            <% if (point.isHit()) { %>
            <td style="color: green"> Попал </td>
            <% } else { %> 
            <td style="color: red"> Промазал </td>
            <% } %>
        </tr>
        <% }} %>
    </tbody>
</table>