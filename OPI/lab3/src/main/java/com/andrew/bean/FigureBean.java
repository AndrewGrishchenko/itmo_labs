package com.andrew.bean;

import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * SessionScoped bean for figures
 */
@ManagedBean(name = "figureBean", eager = true)
@SessionScoped
public class FigureBean {
    private String fig1;
    private String fig2;
    private String fig3;

    private double pointX = 0;
    private double pointY = 0.0;
    private int pointOpacity = 0;

    /**
     * Default FigureBean constructor
     */
    public FigureBean() {
        Locale.setDefault(Locale.US);
    }

    /**
     * Function to set figures
     * @param value r value
     */
    public void setFigs(Integer value) {
        Double r = (double) value;
        fig1 = String.format("0,0 %f,0 0,%f", r, -0.5 * r);
        fig2 = String.format("0,0 %f,0 %f,%f 0,%f", -r, -r, r, r);
        fig3 = String.format("M%f,0 A%f,%f 0 0 1 0,%f L0,0 Z", -r, r, r, -r);
    }

    /**
     * Function to set point
     * @param x x
     * @param y y
     */
    public void setPoint (double x, double y) {
        pointX = x;
        pointY = -y;
        pointOpacity = 1;
    }

    /**
     * Function to hide point
     */
    public void hidePoint() {
        pointOpacity = 0;
    }

    /**
     * Function to get figure 1 data
     * @return Figure 1 data
     */
    public String getFig1() {
        return fig1;
    }

    /**
     * Function to set figure 1 data
     * @param fig1 Figure 1 data
     */
    public void setFig1(String fig1) {
        this.fig1 = fig1;
    }

    /**
     * Function to get figure 2 data
     * @return Figure 2 data
     */
    public String getFig2() {
        return fig2;
    }

    /**
     * Function to set figure 2 data
     * @param fig2 Figure 2 data
     */
    public void setFig2(String fig2) {
        this.fig2 = fig2;
    }

    /**
     * Function to get figure 3 data
     * @return Figure 3 data
     */
    public String getFig3() {
        return fig3;
    }

    /**
     * Function to set figure 3 data
     * @param fig3 Figure 3 data
     */
    public void setFig3(String fig3) {
        this.fig3 = fig3;
    }

    /**
     * Function to get point's x
     * @return Point's x
     */
    public double getPointX() {
        return pointX;
    }

    /**
     * Function to set point's x
     * @param pointX Point's x
     */
    public void setPointX(double pointX) {
        this.pointX = pointX;
    }

    /**
     * Function to get point'y
     * @return Point's y
     */
    public double getPointY() {
        return pointY;
    }

    /**
     * Functino to set point's y
     * @param pointY Point's y
     */
    public void setPointY(double pointY) {
        this.pointY = pointY;
    }

    /**
     * Function to get point's opacity
     * @return Point's opacity
     */
    public int getPointOpacity() {
        return pointOpacity;
    }

    /**
     * Function to set point's opacity
     * @param pointOpacity Point's opacity
     */
    public void setPointOpacity(int pointOpacity) {
        this.pointOpacity = pointOpacity;
    }
}
