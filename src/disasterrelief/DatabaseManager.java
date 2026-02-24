package disasterrelief;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Singleton that manages the MySQL connection and provides CRUD helpers
 * for every table in the Disaster Relief database.
 */
public class DatabaseManager {

    private static final String URL      = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME  = "disasterrelief";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static DatabaseManager instance;
    private Connection connection;

    // ── singleton ────────────────────────────────────────────
    private DatabaseManager() { connect(); }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    // ── connection helpers ───────────────────────────────────
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // First connect without DB name to ensure DB exists
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.close();
            connection.close();

            // Now connect to the specific database
            connection = DriverManager.getConnection(URL + DB_NAME, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.err.println("CRITICAL: Could not connect to MySQL. Ensure MySQL is running on port 3306 with 'root'/'root'.");
            e.printStackTrace();
        }
    }

    /**
     * Reads schema.sql and executes it to create tables and sample data.
     * Only runs if the 'incidents' table is empty to avoid duplicates.
     */
    public void initializeDatabase() {
        try {
            // Check if database is already initialized
            Connection conn = getConnection();
            try (Statement checkStmt = conn.createStatement();
                 ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM incidents")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Database already contains data. Skipping initialization.");
                    return;
                }
            } catch (SQLException e) {
                // Table might not exist yet, proceed with initialization
            }

            String schema = new String(Files.readAllBytes(Paths.get("schema.sql")));
            String[] statements = schema.split(";");
            Statement stmt = conn.createStatement();
            
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.executeUpdate(trimmed);
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("exists")) {
                            System.err.println("Warning executing SQL: " + trimmed.substring(0, Math.min(50, trimmed.length())) + "... -> " + e.getMessage());
                        }
                    }
                }
            }
            stmt.close();
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) connect();
        } catch (SQLException e) { connect(); }
        return connection;
    }

    public void close() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException ignored) {}
    }

    // ── generic helpers ──────────────────────────────────────
    public ResultSet executeQuery(String sql) throws SQLException {
        return getConnection().createStatement().executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        return getConnection().createStatement().executeUpdate(sql);
    }

    // ── INCIDENTS ────────────────────────────────────────────
    public ResultSet getIncidents() throws SQLException {
        return executeQuery("SELECT * FROM incidents ORDER BY date DESC");
    }

    public void addIncident(String title, String type, String date,
                            String location, String description, String severity) throws SQLException {
        String sql = "INSERT INTO incidents (title,type,date,location,description,severity_level) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, type);
        ps.setString(3, date);
        ps.setString(4, location);
        ps.setString(5, description);
        ps.setString(6, severity);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteIncident(int id) throws SQLException {
        executeUpdate("DELETE FROM incidents WHERE incident_id = " + id);
    }

    // ── VICTIMS ──────────────────────────────────────────────
    public ResultSet getVictims() throws SQLException {
        return executeQuery(
            "SELECT v.*, i.title AS incident_title FROM victims v " +
            "LEFT JOIN incidents i ON v.incident_id = i.incident_id ORDER BY v.victim_id DESC");
    }

    public void addVictim(int incidentId, String name, String status,
                          String contact, String address) throws SQLException {
        String sql = "INSERT INTO victims (incident_id,name,status,contact_info,address) VALUES (?,?,?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, incidentId);
        ps.setString(2, name);
        ps.setString(3, status);
        ps.setString(4, contact);
        ps.setString(5, address);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteVictim(int id) throws SQLException {
        executeUpdate("DELETE FROM victims WHERE victim_id = " + id);
    }

    // ── RELIEF REQUESTS ──────────────────────────────────────
    public ResultSet getReliefRequests() throws SQLException {
        return executeQuery(
            "SELECT r.*, v.name AS victim_name FROM relief_requests r " +
            "JOIN victims v ON r.victim_id = v.victim_id ORDER BY r.req_id DESC");
    }

    public void addReliefRequest(int victimId, String needs, String priority) throws SQLException {
        String sql = "INSERT INTO relief_requests (victim_id,needs_description,priority) VALUES (?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, victimId);
        ps.setString(2, needs);
        ps.setString(3, priority);
        ps.executeUpdate();
        ps.close();
    }

    public void updateRequestStatus(int reqId, String status) throws SQLException {
        String sql = "UPDATE relief_requests SET status = ? WHERE req_id = ?";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, status);
        ps.setInt(2, reqId);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteRequest(int id) throws SQLException {
        executeUpdate("DELETE FROM relief_requests WHERE req_id = " + id);
    }

    // ── CENTERS ──────────────────────────────────────────────
    public ResultSet getCenters() throws SQLException {
        return executeQuery("SELECT * FROM centers ORDER BY center_id");
    }

    public void addCenter(String name, String location, int capacity, String contact) throws SQLException {
        String sql = "INSERT INTO centers (name,location,capacity,contact_info) VALUES (?,?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, location);
        ps.setInt(3, capacity);
        ps.setString(4, contact);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteCenter(int id) throws SQLException {
        executeUpdate("DELETE FROM centers WHERE center_id = " + id);
    }

    // ── TEAMS ────────────────────────────────────────────────
    public ResultSet getTeams() throws SQLException {
        return executeQuery(
            "SELECT t.*, c.name AS center_name FROM teams t " +
            "LEFT JOIN centers c ON t.center_id = c.center_id ORDER BY t.team_id");
    }

    public void addTeam(int centerId, String teamName, String specialization) throws SQLException {
        String sql = "INSERT INTO teams (center_id,team_name,specialization) VALUES (?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, centerId);
        ps.setString(2, teamName);
        ps.setString(3, specialization);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteTeam(int id) throws SQLException {
        executeUpdate("DELETE FROM teams WHERE team_id = " + id);
    }

    // ── RESOURCES ────────────────────────────────────────────
    public ResultSet getResources() throws SQLException {
        return executeQuery(
            "SELECT r.*, c.name AS center_name FROM resources r " +
            "LEFT JOIN centers c ON r.center_id = c.center_id ORDER BY r.resource_id");
    }

    public void addResource(int centerId, String name, String type,
                            int quantity, String unit) throws SQLException {
        String sql = "INSERT INTO resources (center_id,name,type,quantity,unit) VALUES (?,?,?,?,?)";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, centerId);
        ps.setString(2, name);
        ps.setString(3, type);
        ps.setInt(4, quantity);
        ps.setString(5, unit);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteResource(int id) throws SQLException {
        executeUpdate("DELETE FROM resources WHERE resource_id = " + id);
    }

    // ── DASHBOARD COUNTS ─────────────────────────────────────
    public int getCount(String table) {
        try {
            ResultSet rs = executeQuery("SELECT COUNT(*) FROM " + table);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ignored) {}
        return 0;
    }

    public int getPendingRequestCount() {
        try {
            ResultSet rs = executeQuery("SELECT COUNT(*) FROM relief_requests WHERE status = 'Pending'");
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ignored) {}
        return 0;
    }

    // ── combo-box helpers (return id→name lists) ─────────────
    public List<String[]> getIncidentList() {
        List<String[]> list = new ArrayList<>();
        try {
            ResultSet rs = executeQuery("SELECT incident_id, title FROM incidents ORDER BY incident_id");
            while (rs.next()) list.add(new String[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException ignored) {}
        return list;
    }

    public List<String[]> getVictimList() {
        List<String[]> list = new ArrayList<>();
        try {
            ResultSet rs = executeQuery("SELECT victim_id, name FROM victims ORDER BY victim_id");
            while (rs.next()) list.add(new String[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException ignored) {}
        return list;
    }

    public List<String[]> getCenterList() {
        List<String[]> list = new ArrayList<>();
        try {
            ResultSet rs = executeQuery("SELECT center_id, name FROM centers ORDER BY center_id");
            while (rs.next()) list.add(new String[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException ignored) {}
        return list;
    }
}
