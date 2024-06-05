package lab7_server.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import lab7_core.models.Coordinates;
import lab7_core.models.Event;
import lab7_core.models.Ticket;
import lab7_core.models.TicketType;
import lab7_core.models.User;

import java.sql.Statement;

public class DBManager {
    private static final String url = "jdbc:postgresql://localhost:5432/studs";
    private static Connection conn = null;

    private static HashMap<String, Integer> lastIds = new HashMap<>() {{
        put("users", 1);
        put("events", 1);
        put("tickets", 1);
    }};

    public static void init () throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", "andrew");
        props.setProperty("password", "fsociety");
        
        conn = DriverManager.getConnection(url, props);

        lastIds.keySet().forEach((String table) -> revertId(table));
    }

    public static ArrayList<Object> executeSelect (String table, String... condition) {
        String query = "SELECT * FROM " + table;
        
        if (condition.length != 0) {
            query += " WHERE " + condition[0];
        }
        query += ";";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            ArrayList<Object> result = new ArrayList<Object>();
            while (rs.next()) {
                switch (table) {
                    case "users":
                        User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3));
                        result.add(user);
                        break;
                    case "events":
                        Event event = new Event(rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getLong(4), rs.getString(5));
                        result.add(event);
                        break;
                    case "tickets":
                        Event ticket_event = (Event) executeSelect("events", "id=" + String.valueOf(rs.getInt(7))).get(0);
                        Ticket ticket = new Ticket(rs.getInt(1), rs.getString(2), new Coordinates((double) rs.getFloat(3), (double) rs.getFloat(4)), rs.getInt(5), TicketType.valueOf(rs.getString(6)), ticket_event);
                        result.add(ticket);
                        break;
                }
            }

            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    public static int executeInsert (String table, Object value) {
        String query = "INSERT INTO " + table + " VALUES ";
        PreparedStatement pst;
        int id = -1;

        try {
            switch (table) {
                case "users":
                    query += "(?, ?, ?)";
                    pst = conn.prepareStatement(query);

                    User user = (User) value;

                    if (exists(table, "username", user.getUsername())) return id;

                    id = getNextId(table);
                    pst.setInt(1, getNextId(table));
                    pst.setString(2, user.getUsername());
                    pst.setString(3, user.getHash());
                    pst.executeUpdate();
                    break;
                case "events":
                    query += "(?, ?, ?, ?, ?)";
                    pst = conn.prepareStatement(query);

                    Event event = (Event) value;

                    id = getNextId(table);
                    pst.setInt(1, getNextId(table));
                    pst.setString(2, event.getName());
                    pst.setTimestamp(3, event.getTimestamp());
                    pst.setInt(4, event.getTicketsCount().intValue());
                    pst.setString(5, event.getDescription());
                    pst.executeUpdate();
                    break;
                case "tickets":
                    query += "(?, ?, ?, ?, ?, CAST(? AS ticket_type), ?, ?)";
                    pst = conn.prepareStatement(query);

                    Ticket ticket = (Ticket) value;

                    id = getNextId(table);
                    pst.setInt(1, id);
                    pst.setString(2, ticket.getName());
                    pst.setFloat(3, (float) ticket.getCoordinates().getX());
                    pst.setFloat(4, ticket.getCoordinates().getY().floatValue());
                    pst.setInt(5, ticket.getPrice());
                    pst.setString(6, ticket.getType().toString());
                    pst.setInt(7, findEventId(ticket.getEvent()));
                    pst.setInt(8, ticket.getCreatorId());
                    pst.executeUpdate();
                    break;
            }
            return id;
        } catch (SQLException e) {
            revertId(table);
            return id;
        }
    }

    public static int findEventId(Event event) {
        String query = "SELECT * FROM events WHERE name=? AND date=CAST(? AS TIMESTAMP) AND tickets_count=? AND description=?;";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, event.getName());
            pst.setTimestamp(2, event.getTimestamp());
            pst.setInt(3, event.getTicketsCount().intValue());
            pst.setString(4, event.getDescription());

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                return rs.getInt(1);
            }

            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public static boolean exists (String table, String field, String value) {
        String query = "SELECT EXISTS(SELECT 1 FROM " + table + " WHERE " + field + "='" + value + "');";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                return rs.getBoolean(1);
            }

            return true;
        } catch (SQLException e) {
            return true;
        }
    }

    public static int getNextId (String table) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nextval('" + table + "_seq');");

            while (rs.next()) {
                return rs.getInt(1);
            }

            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public static void revertId (String table) {
        String query = "SELECT MAX(id) FROM " + table + ";";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                int val = rs.getInt(1);
                
                if (val == 0) {
                    query = "SELECT setval('" + table + "_seq', 1, false);";
                } else {
                    query = "SELECT setval('" + table + "_seq', " + String.valueOf(val) + ");";
                }
                
                st = conn.createStatement();
                st.executeQuery(query);
            }
        } catch (SQLException e) {
            
        }
    }
}
