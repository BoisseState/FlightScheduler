package flightscheduler;

import java.sql.*;
//@author Joe Boisse
public class Database 
{
    
    private static final String dbURL = "jdbc:derby://localhost:1527/FlightSchedulerJosephBoissejjb5883";
    private static final String user = "java";
    private static final String pass = "java";
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            connection = DriverManager.getConnection(dbURL, user, pass);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return connection;
    }
}
