package com.andrew.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "figureBean", eager = true)
@SessionScoped
public class FigureBean {
    private String fig1;
    private String fig2;
    private String fig3;

    private double pointX = 0;
    private double pointY = 0.0;
    private int pointOpacity = 0;

    public FigureBean() {
    }

    public void setFigs(Integer value) {
        Double r = (double) value;
        fig1 = String.format("0,0 %f,0 0,%f", r, -0.5 * r);
        fig2 = String.format("0,0 %f,0 %f,%f 0,%f", -r, -r, r, r);
        fig3 = String.format("M%f,0 A%f,%f 0 0 1 0,%f L0,0 Z", -r, r, r, -r);
    }

    public void setPoint (double x, double y) {
        pointX = x;
        pointY = -y;
        pointOpacity = 1;
    }

    public void hidePoint() {
        pointOpacity = 0;
    }

    public String getFig1() {
        return fig1;
    }

    public void setFig1(String fig1) {
        this.fig1 = fig1;
    }

    public String getFig2() {
        return fig2;
    }

    public void setFig2(String fig2) {
        this.fig2 = fig2;
    }

    public String getFig3() {
        return fig3;
    }

    public void setFig3(String fig3) {
        this.fig3 = fig3;
    }

    public double getPointX() {
        return pointX;
    }

    public void setPointX(double pointX) {
        this.pointX = pointX;
    }

    public double getPointY() {
        return pointY;
    }

    public void setPointY(double pointY) {
        this.pointY = pointY;
    }

    public int getPointOpacity() {
        return pointOpacity;
    }

    public void setPointOpacity(int pointOpacity) {
        this.pointOpacity = pointOpacity;
    }
}
