package com.andrew.bean;

import java.util.ArrayList;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.andrew.db.PointService;
import com.andrew.model.Point;

/**
 * ManagedBean for table
 */
@ManagedBean(name = "tableBean", eager = true)
@ApplicationScoped
public class TableBean {
    private ArrayList<Point> points = new ArrayList<>();
    private PointService pointService = new PointService();

    /**
     * Default TableBean constructor
     */
    public TableBean() {
        points = getPoints();
        System.out.println(points);
    }

    /**
     * Function for inserting point
     * @param point point
     */
    public void insertPoint(Point point) {
        points.add(point);
    }

    /**
     * Get all points
     * @return points
     */
    public ArrayList<Point> getPoints() {
        return new ArrayList<>(pointService.getAllPoints());
    }

    /**
     * Set points
     * @param points points
     */
    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
}
