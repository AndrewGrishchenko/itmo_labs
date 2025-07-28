package com.andrew.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Point model class
 */
@Entity
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Column(nullable = false)
    private int r;

    @Column(name = "cur_time", nullable = false)
    private String curTime;

    @Column(name = "exec_time", nullable = false)
    private long execTime;

    @Column(nullable = false)
    private boolean hit;

    /**
     * Default Point constructor
     */
    public Point () {
    }

    /**
     * Point constructor with all values
     * @param x x value
     * @param y y value
     * @param r r value
     * @param curTime current time
     * @param execTime execution time
     * @param hit is hit
     */
    public Point (double x, double y, int r, String curTime, long execTime, boolean hit) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.curTime = curTime;
        this.execTime = execTime;
        this.hit = hit;
    }

    /**
     * Get x value
     * @return x value
     */
    public double getX() {
        return x;
    }

    /**
     * Set x value
     * @param x x value
     */
    public void setX (double x) {
        this.x = x;
    }

    /**
     * Get y value
     * @return y value
     */
    public double getY() {
        return y;
    }

    /**
     * Set y value
     * @param y y value
     */
    public void setY (double y) {
        this.y = y;
    }

    /**
     * Get r value
     * @return r value
     */
    public int getR() {
        return r;
    }

    /**
     * Set r value
     * @param r r value
     */
    public void setR (int r) {
        this.r = r;
    }

    /**
     * Get current time value
     * @return current time value
     */
    public String getCurTime() {
        return curTime;
    }

    /**
     * Set current time value
     * @param curTime current time value
     */
    public void setCurTime (String curTime) {
        this.curTime = curTime;
    }

    /**
     * Get exection time value
     * @return execution time value
     */
    public long getExecTime() {
        return execTime;
    }

    /**
     * Set execution time value
     * @param execTime execution time value
     */
    public void setExecTime (long execTime) {
        this.execTime = execTime;
    }

    /**
     * Get is hit value
     * @return is hit value
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * Set is hit value
     * @param hit is hit value
     */
    public void setHit (boolean hit) {
        this.hit = hit;
    }

    /**
     * Equals function override
     */
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

    /**
     * hashCode function override
     */
    @Override
    public int hashCode () {
        return Objects.hash(getX(), getY(), getR(), getCurTime(), getExecTime(), isHit());
    }

    /**
     * toString function override
     */
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