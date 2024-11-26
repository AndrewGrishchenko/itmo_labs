package com.andrew.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import com.andrew.model.Point;

public class DBManager {
    private static final String url = "jdbc:postgresql://localhost:5432/studs";
    private static Connection conn = null;

    public static void init () {
        Properties props = new Properties();
        props.setProperty("user", "andrew");
        props.setProperty("password", "fsociety");

        try {
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Point> getAllPoints() {
        String qeury = "SELECT * FROM points;";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(qeury);

            ArrayList<Point> points = new ArrayList<>();

            while (rs.next()) {
                points.add(new Point(rs.getDouble(2), rs.getDouble(3), rs.getInt(4), rs.getString(5), rs.getLong(6), rs.getBoolean(7)));
            }

            return points;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertPoint(Point point) {
        String query = "INSERT INTO points(x, y, r, curTime, execTime, hit) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setDouble(1, point.getX());
            pst.setDouble(2, point.getY());
            pst.setInt(3, point.getR());
            pst.setString(4, point.getCurTime());
            pst.setLong(5, point.getExecTime());
            pst.setBoolean(6, point.isHit());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}