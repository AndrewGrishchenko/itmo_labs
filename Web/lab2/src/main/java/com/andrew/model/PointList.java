package com.andrew.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.ejb.Stateful;
import jakarta.enterprise.context.SessionScoped;

@Stateful
@SessionScoped
public class PointList implements Serializable {
    private static final long serialVersionUID = 1;

    private List<Point> points = new ArrayList<>();

    public void addPoint (Point point) {
        points.add(point);
    }

    public List<Point> getPoints () {
        return points;
    }

    public String toJSON () {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Point p : points) {
            sb.append(p.toJSON()).append(", ");
        }
        if (!points.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PointList other = (PointList) obj;
        return Objects.equals(getPoints(), other.getPoints());
    }

    @Override
    public int hashCode () {
        return Objects.hash(getPoints());
    }

    @Override
    public String toString () {
        return "PointList{" +
                "points=" + points +
                '}';
    }
}
