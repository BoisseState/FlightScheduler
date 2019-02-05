package flightscheduler;

import java.sql.*;
import java.util.ArrayList;

 // @author JoeBoisse

public class Flights 
{
    private static PreparedStatement selectFlights;
    private static PreparedStatement newFlight;
    private static PreparedStatement drop;
    private static PreparedStatement dFlight;
   
    
    public static ArrayList < String > getFlights()
    {
        ArrayList < String > results = new ArrayList < >();
        ResultSet rs = null;
        String flight;
        try
        {
            Connection conn = Database.getConnection();
            selectFlights = conn.prepareStatement("SELECT * FROM FLIGHT");
            rs = selectFlights.executeQuery();
            
            
            if(rs.next())
            {
                flight = rs.getString("FLIGHTNAME");
                results.add(flight);
                while(rs.next())
                {
                    results.add(rs.getString("FLIGHTNAME"));
                }
                
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch(SQLException sqlException)
            {
                sqlException.printStackTrace();
            }                    
        }
        return results;
    }
    
    public static String addFlight(String fName, int seats)
    {
        String mes = "";
        try
        {
            Connection conn = Database.getConnection();
            newFlight = conn.prepareStatement("INSERT INTO FLIGHT"
                    + " (FLIGHTNAME, SEATS) VALUES (?,?)");
            newFlight.setString(1, fName);
            newFlight.setInt(2, seats);
            newFlight.executeUpdate();
            mes = "The flight was successfully added to \nthe database.";
        }
        catch (SQLException sqlException) 
        {
            mes = "Unable to add the flight to the database.";
            sqlException.printStackTrace();  
        }   
        return mes;
    }
    
    public static String dropFlight(String fName)
    {
        String mes ="";
        mes = deleteFlight(fName) +"\n\n"+Booking.reBook(fName)+"\n\n"+
        Waitlist.removeWait(fName);
        
        return mes;
    }
    
    public static String deleteFlight(String fName)
    {
        String mes = "";
        try
        {            
            Connection conn = Database.getConnection();
            
            dFlight = conn.prepareStatement("DELETE FROM FLIGHT"
                    + " WHERE FLIGHTNAME = ?");
            dFlight.setString(1, fName);
           
            dFlight.executeUpdate();
            mes = "The flight was successfully deleted.";
        }
        catch (SQLException sqlException) 
        {
            mes = "Unable to delete the flight to the database.";
            sqlException.printStackTrace();  
        }   
        return mes;
    }
    
    
    
}
