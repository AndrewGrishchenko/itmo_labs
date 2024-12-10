package com.andrew.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.andrew.db.DBManager;
import com.andrew.model.Point;

@ManagedBean(name = "formBean", eager = true)
@SessionScoped
public class FormBean implements Serializable {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private Double x;
    private Double y;
    private Integer r;

    @ManagedProperty(value = "#{figureBean}")
    FigureBean figureBean;
    
    @ManagedProperty(value = "#{tableBean}")
    TableBean tableBean;

    public FormBean() {
    }

    public void check(Double x, Double y, Integer r) {
        System.out.println(x);
        System.out.println(y);
        System.out.println(r);
        System.out.println();

        Long startTime = System.nanoTime();
        
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        point.setR(r);
        point.setCurTime(dtf.format(LocalDateTime.now()));
        point.setHit(isHit(x, y, Double.valueOf(r)));

        Long endTime = System.nanoTime();
        point.setExecTime((endTime - startTime) / 1000);

        DBManager.insertPoint(point);
        tableBean.insertPoint(point);

        figureBean.hidePoint();
    }

    private boolean isHit (Double x, Double y, Double r) {
        return (0 <= x && x <= r && 0 <= y && y <= r/2 && x + 2 * y <= r)
            || (0 >= x && x >= -r && 0 >= y && y >= -r)
            || (x >= 0 && y >= 0 && x*x + y*y <= r*r);
    }

    public void clearPoints() {
        DBManager.clearPoints();
    }

    public void renderPoint() {
        if (x != null && y != null) {
            figureBean.setPoint(x, y);
        } else {
            figureBean.hidePoint();
        }
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
        renderPoint();
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
        renderPoint();
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
        figureBean.setFigs(r);
    }

    public FigureBean getFigureBean() {
        return figureBean;
    }

    public void setFigureBean(FigureBean figureBean) {
        this.figureBean = figureBean;
    }

    public TableBean getTableBean() {
        return tableBean;
    }

    public void setTableBean(TableBean tableBean) {
        this.tableBean = tableBean;
    }
}
