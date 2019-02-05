package flightscheduler;

import java.util.Date;
import java.sql.*;
import java.util.ArrayList;


//@author JoeBoisse
public class Day 
{
    private static PreparedStatement selectDays;
    private static PreparedStatement newDay;
    
    public static String addDay(String date)
    {
        String mes = null;
        try
        {
            Connection conn = Database.getConnection();
            java.sql.Date day = java.sql.Date.valueOf(date);
            newDay = conn.prepareStatement("INSERT INTO DAYS"
                    + " (DAY) VALUES (?)");
            newDay.setDate(1, day);
            newDay.executeUpdate();
            mes = "The day was successfully added to the database.";
        }
        catch (SQLException sqlException) 
        {
            mes = "Unable to add the day to the database.";
            sqlException.printStackTrace();  
        }   
        return mes;
    }
    
    public static ArrayList < String > getDays()
    {
        ArrayList < String > results;
        results = new ArrayList<>();
        ResultSet rs = null;
        Date d;
        String strDay;
        
        try
        {
            Connection conn = Database.getConnection();
            selectDays = conn.prepareStatement("SELECT * FROM DAYS");
            rs = selectDays.executeQuery();
            if(rs.next())
            {
            d = rs.getDate("DAY");
            strDay = d.toString();
            results.add(strDay);
            }
            while(rs.next())
            {
                results.add(rs.getDate("DAY").toString());
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
}
