package com.andrew.model;

import java.io.Serializable;
import java.util.Objects;

public class Point implements Serializable {
    private static final long serialVersionUID = 1;

    private double x;
    private double y;
    private int r;
    private String curTime;
    private long execTime;
    private boolean hit;

    public Point () {
    }

    public Point (double x, double y, int r, String curTime, long execTime, boolean hit) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.curTime = curTime;
        this.execTime = execTime;
        this.hit = hit;
    }

    public String toJSON () {
        return "{" +
                "\"x\": " + x +
                ", \"y\": " + y +
                ", \"r\": " + r +
                ", \"curTime\": \"" + curTime + "\"" +
                ", \"execTime\": " + execTime +
                ", \"hit\": " + hit +
                "}";
    }

    public double getX() {
        return x;
    }

    public void setX (double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY (double y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR (int r) {
        this.r = r;
    }

    public String getCurTime() {
        return curTime;
    }

    public void setCurTime (String curTime) {
        this.curTime = curTime;
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime (long execTime) {
        this.execTime = execTime;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit (boolean hit) {
        this.hit = hit;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;

        Point other = (Point) obj;
        return Objects.equals(getX(), other.getX())
            && Objects.equals(getY(), other.getY())
            && Objects.equals(getR(), other.getR())
            && Objects.equals(getCurTime(), other.getCurTime())
            && Objects.equals(getExecTime(), other.getExecTime())
            && Objects.equals(isHit(), other.isHit());
    }

    @Override
    public int hashCode () {
        return Objects.hash(getX(), getY(), getR(), getCurTime(), getExecTime(), isHit());
    }

    @Override
    public String toString () {
        return "Point{" +
            "x=" + x +
            ", y=" + y +
            ", r=" + r +
            ", curTime=" + curTime +
            ", execTime=" + execTime +
            ", hit=" + hit +
            '}';
    }
}