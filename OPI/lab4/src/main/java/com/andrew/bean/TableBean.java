package com.andrew.bean;

import java.util.ArrayList;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.andrew.db.PointService;
// import com.andrew.db.DBManager;
import com.andrew.model.Point;

@ManagedBean(name = "tableBean", eager = true)
@ApplicationScoped
public class TableBean {
    private ArrayList<Point> points = new ArrayList<>();
    private PointService pointService = new PointService();


    public TableBean() {
        // DBManager.init();


        points = getPoints();
        System.out.println(points);
    }

    // public void check(Double x, Double y, Integer r) {
    //     System.out.println(x);
    //     System.out.println(y);
    //     System.out.println(r);
    //     System.out.println();

    //     Long startTime = System.nanoTime();
        
    //     Point point = new Point();
    //     point.setX(x);
    //     point.setY(y);
    //     point.setR(r);
    //     point.setCurTime(dtf.format(LocalDateTime.now()));
    //     point.setHit(isHit(x, y, r));

    //     Long endTime = System.nanoTime();
    //     point.setExecTime((endTime - startTime) / 1000);

    //     DBManager.insertPoint(point);
    // }

    // private boolean isHit (double x, double y, double r) {
    //     return (0 <= x && x <= r && 0 <= y && y <= r/2 && x + 2 * y <= r)
    //         || (0 >= x && x >= -r && 0 >= y && y >= -r)
    //         || (x >= 0 && y >= 0 && x*x + y*y <= r*r);
    // }

    public void insertPoint(Point point) {
        points.add(point);
    }

    public ArrayList<Point> getPoints() {
        return new ArrayList<>(pointService.getAllPoints());
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
}
