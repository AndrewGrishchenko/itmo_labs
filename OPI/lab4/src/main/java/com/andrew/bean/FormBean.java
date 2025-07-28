package com.andrew.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

// import com.andrew.db.DBManager;
import com.andrew.db.PointService;
import com.andrew.model.Point;

@ManagedBean(name = "formBean", eager = true)
@SessionScoped
public class FormBean implements Serializable {
    private PointService pointService = new PointService();
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private Double x;
    private Double y;
    private Integer r;

    @ManagedProperty(value = "#{figureBean}")
    FigureBean figureBean;
    
    @ManagedProperty(value = "#{tableBean}")
    TableBean tableBean;

    // @ManagedProperty(value = "#{hitStatsBean}")
    // private HitStatsBean hitStatsBean;

    @ManagedProperty(value = "#{avgIntervalBean}")
    AvgIntervalBean avgIntervalBean;

    @ManagedProperty(value = "#{pointStatsBean}")
    PointStatsBean pointStatsBean;

    public FormBean() {
    }

    public void check() {
        Long startTime = System.nanoTime();
        LocalDateTime now = LocalDateTime.now();
        boolean hit = isHit(x, y, Double.valueOf(r));

        Point point = new Point();
        point.setX(x);
        point.setY(y);
        point.setR(r);
        point.setCurTime(dtf.format(now));
        point.setHit(hit);

        Long endTime = System.nanoTime();
        point.setExecTime((endTime - startTime) / 1000);

        pointService.insertPoint(point);
        tableBean.insertPoint(point);

        figureBean.hidePoint();

        // hitStatsBean.clicked(now, hit);
        avgIntervalBean.clicked(now);
        pointStatsBean.clicked(hit);
    }

    private boolean isHit (Double x, Double y, Double r) {
        return (-r <= x && x <= 0 && -r <= y && y <= 0)
                || (x >= 0 && y >= 0 && y <= -x/2 + r/2)
                || (x <= 0 && y >= 0 && x * x + y * y <= r * r);
    }

    public void clearPoints() {
        pointService.clearPoints();
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

    public AvgIntervalBean getAvgIntervalBean() {
        return avgIntervalBean;
    }

    public void setAvgIntervalBean(AvgIntervalBean avgIntervalBean) {
        this.avgIntervalBean = avgIntervalBean;
    }

    public PointStatsBean getPointStatsBean() {
        return pointStatsBean;
    }

    public void setPointStatsBean(PointStatsBean pointStatsBean) {
        this.pointStatsBean = pointStatsBean;
    }
}
