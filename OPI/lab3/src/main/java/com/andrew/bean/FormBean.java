package com.andrew.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.andrew.db.PointService;
import com.andrew.model.Point;

/**
 * ManagedBean for form
 */
@ManagedBean(name = "formBean", eager = true)
@SessionScoped
public class FormBean implements Serializable {
    /** pointService @see PointService */
    private PointService pointService = new PointService();
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss"); 
    
    /** x value */
    private Double x;
    /** y value */
    private Double y;
    /** r value */
    private Integer r;

    /** figureBean @see FigureBean */
    @ManagedProperty(value = "#{figureBean}")
    FigureBean figureBean;
    
    /** tableBean @see TableBean */
    @ManagedProperty(value = "#{tableBean}")
    TableBean tableBean;

    /**
     * Default constructor for FormBean
     */
    public FormBean() {
    }

    /**
     * Check point's hit
     */
    public void check() {
        Long startTime = System.nanoTime();
        
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        point.setR(r);
        point.setCurTime(dtf.format(LocalDateTime.now()));
        point.setHit(isHit(x, y, Double.valueOf(r)));

        Long endTime = System.nanoTime();
        point.setExecTime((endTime - startTime) / 1000);

        pointService.insertPoint(point);
        tableBean.insertPoint(point);

        figureBean.hidePoint();
    }

    /**
     * Check if hit
     * @param x x value
     * @param y y value
     * @param r r value
     * @return is hit
     */
    private boolean isHit (Double x, Double y, Double r) {
        return (-r <= x && x <= 0 && -r <= y && y <= 0)
                || (x >= 0 && y >= 0 && y <= -x/2 + r/2)
                || (x <= 0 && y >= 0 && x * x + y * y <= r * r);
    }

    /**
     * Clear points
     */
    public void clearPoints() {
        pointService.clearPoints();
    }

    /**
     * Render point
     */
    public void renderPoint() {
        if (x != null && y != null) {
            figureBean.setPoint(x, y);
        } else {
            figureBean.hidePoint();
        }
    }

    /**
     * Get x
     * @return x
     */
    public Double getX() {
        return x;
    }

    /**
     * Set x
     * @param x x
     */
    public void setX(Double x) {
        this.x = x;
        renderPoint();
    }

    /**
     * Get y
     * @return y
     */
    public Double getY() {
        return y;
    }

    /**
     * Set y
     * @param y y
     */
    public void setY(Double y) {
        this.y = y;
        renderPoint();
    }

    /**
     * Get r
     * @return r
     */
    public Integer getR() {
        return r;
    }

    /**
     * Set r
     * @param r r
     */
    public void setR(Integer r) {
        this.r = r;
        figureBean.setFigs(r);
    }

    /**
     * Get FigureBean
     * @return FigureBean
     */
    public FigureBean getFigureBean() {
        return figureBean;
    }

    /**
     * Set FigureBean
     * @param figureBean FigureBean
     */
    public void setFigureBean(FigureBean figureBean) {
        this.figureBean = figureBean;
    }

    /**
     * Get TableBean
     * @return TableBean
     */
    public TableBean getTableBean() {
        return tableBean;
    }

    /**
     * Set TableBean
     * @param tableBean TableBean
     */
    public void setTableBean(TableBean tableBean) {
        this.tableBean = tableBean;
    }
}
