package com.andrew.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andrew.model.PointList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getQueryString() == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        } else if (req.getQueryString().equals("points")) {
            PointList pointList = (PointList) req.getSession().getAttribute("points");
            if (pointList == null) {
                pointList = new PointList();
                req.getSession().setAttribute("points", pointList);
            }

            PrintWriter out = resp.getWriter();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            out.print(pointList.toJSON());
            out.flush();
        } else {
            Pattern pattern = Pattern.compile("^x=.*&y=.*&r=.*");
            Matcher matcher = pattern.matcher(req.getQueryString());
            if (matcher.matches()) {
                req.getRequestDispatcher("/point").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
