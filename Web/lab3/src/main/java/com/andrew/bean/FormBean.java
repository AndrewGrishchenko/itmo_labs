package com.andrew.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.andrew.model.Point;

@ManagedBean(name = "formBean", eager = true)
@SessionScoped
public class FormBean implements Serializable {
    private ArrayList<Point> points = new ArrayList<>(Arrays.asList(new Point(1.1, 1.2, 2, "wefewf", 123, true)));

    private Double x;
    private Double y;
    private Integer r;

    @ManagedProperty(value = "#{figureBean}")
    FigureBean figureBean;
    
    public FormBean() {
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

    public ArrayList<Point> getPoints() {
        return points;
    }

    public FigureBean getFigureBean() {
        return figureBean;
    }

    public void setFigureBean(FigureBean figureBean) {
        this.figureBean = figureBean;
    }
}
