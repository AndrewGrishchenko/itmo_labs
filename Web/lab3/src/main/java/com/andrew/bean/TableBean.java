package com.andrew.bean;

import java.util.ArrayList;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.andrew.db.DBManager;
import com.andrew.model.Point;

@ManagedBean(name = "tableBean", eager = true)
@ApplicationScoped
public class TableBean {
    private ArrayList<Point> points = new ArrayList<>();

    public TableBean() {
        DBManager.init();
        points = getPoints();
        System.out.println(points);
    }

    // public void check() {
        // points.add(new Point(formBean.getX(), formBean.getY(), formBean.getR(), LocalDateTime.now().toString(), 12, true));
    // }

    public ArrayList<Point> getPoints() {
        return new ArrayList<>(DBManager.getAllPoints());
    }
}
