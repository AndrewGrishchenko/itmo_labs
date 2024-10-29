package com.andrew.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.andrew.model.Point;
import com.andrew.model.PointList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/point")
public class AreaCheckServlet extends HttpServlet {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Point point = new Point();
        point.setCurTime(dtf.format(LocalDateTime.now()));
        Long startTime = System.nanoTime();
        
        double x = Double.parseDouble(req.getParameter("x"));
        double y = Double.parseDouble(req.getParameter("y"));
        int r = Integer.parseInt(req.getParameter("r"));
        String action = req.getParameter("action");

        point.setX(x);
        point.setY(y);
        point.setR(r);
        point.setHit(isHit(x, y, r));

        Long endTime = System.nanoTime();
        point.setExecTime((endTime - startTime) / 1000);

        PointList pointList = (PointList) req.getSession().getAttribute("points");
        if (pointList == null) {
            pointList = new PointList();
            req.getSession().setAttribute("points", pointList);
        }
        pointList.addPoint(point);

        if (action.equals("checkPoint")) {
            PrintWriter out = resp.getWriter();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            out.print(point.toJSON());
            out.flush();
        } else if (action.equals("submit")) {
            req.getRequestDispatcher("jsp/result.jsp").forward(req, resp);
        }
    }

    private boolean isHit (double x, double y, double r) {
        double firstAreaA = Math.abs(x) / (r / 7) - 3;
        double firstSecondAreaA = Math.abs(y / (r / 7) + (double) 3 / 7 * Math.sqrt(33));
        double firstSecondAreaB = Math.pow((y / (r / 7)) / 3, 2);
        double firstSecondAreaC = Math.sqrt(Math.abs(firstSecondAreaA) / firstSecondAreaA);
        boolean firstArea = (y / (r / 7)) >= 0 && Math.pow(x / r, 2)
                * Math.sqrt(Math.abs(firstAreaA) / (firstAreaA))
                + firstSecondAreaB
                * firstSecondAreaC
                - 1 <= 0;

        double secondAreaA = Math.abs(x) / (r / 7) - 4;
        boolean secondArea = (y / (r / 7)) < 0 && Math.pow(x / r, 2)
                * Math.sqrt(Math.abs(secondAreaA) / (secondAreaA))
                + firstSecondAreaB
                * firstSecondAreaC
                - 1 <= 0;

        boolean thirdArea = (y / (r / 7)) < 0 && Math.abs((x / (r / 7)) / 2)
                - (3 * Math.sqrt(33) - 7) * Math.pow((x / (r / 7)), 2) / 112
                - 3 + Math.sqrt(1 - Math.pow(Math.abs(Math.abs(x) / (r / 7) - 2) - 1, 2))
                - y / (r / 7) <= 0;

        boolean fourthArea = Math.abs(x) / (r / 7) <= 1 && Math.abs(x) / (r / 7) >= 0.75
                && y / (r / 7) <= 3 && y / (r / 7) >= 0
                && 9 - 8 * Math.abs(x) / (r / 7) >= y / (r / 7);

        boolean fifthArea = y / (r / 7) >= 0
                && Math.abs(x) / (r / 7) <= 0.75 && Math.abs(x) / (r / 7) >= 0.5
                && 3 * Math.abs(x) / (r / 7) + 0.75 >= y / (r / 7);

        boolean sixthArea = x / (r / 7) <= 0.5 && x / (r / 7) >= -0.5
                && y / (r / 7) >= 0
                && y / (r / 7) <= 2.25;

        double seventhAreaA = Math.abs(x) / (r / 7) - 1;
        boolean seventhArea = y / (r / 7) >= 0 && 6 * Math.sqrt(10) / 7
                + (1.5 - 0.5 * Math.abs(x) / (r / 7))
                * Math.sqrt(Math.abs(seventhAreaA) / seventhAreaA)
                - 6 * Math.sqrt(10) / 14
                * Math.sqrt(4 - Math.pow(seventhAreaA, 2)) >= y / (r / 7);

        return firstArea || secondArea || thirdArea
                || fourthArea || fifthArea || sixthArea || seventhArea;
    }
}
