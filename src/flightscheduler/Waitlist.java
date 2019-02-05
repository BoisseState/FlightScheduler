package flightscheduler;

import java.util.Date;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
// @author JoeBoisse

public class Waitlist 
{
    private Flights flight;
    private Date date;
    private Customer cust;
 
    
    private static PreparedStatement waitlistCustomer;
    private static PreparedStatement selectWaitByDay;
    private static PreparedStatement remove;
    private static PreparedStatement waiting;
    private static PreparedStatement selectFlightByCustomer;
    
    public static String waitlist(String customer, String flight, String date)
    {
        Timestamp pos;
        String mes;
        pos = new Timestamp(Calendar.getInstance().getTime().getTime());
        try
        {
            Connection conn = Database.getConnection();
            waitlistCustomer = conn.prepareStatement("INSERT INTO WAITLIST"
                    + " (CUSTOMER, FLIGHT, DATE, POSITION) VALUES (?,?,?,?)");
            waitlistCustomer.setString(1, customer);
            waitlistCustomer.setString(2, flight);
            waitlistCustomer.setString(3, date);
            waitlistCustomer.setTimestamp(4, pos);
            waitlistCustomer.executeUpdate();
            mes = (customer+" was successfully waitlisted.");
            
        }
        catch (SQLException sqlException) 
        {
            mes = ("Unable to waitlist "+customer+".");
            sqlException.printStackTrace();   
        }   
        return mes;
    }
    public static String waitlist(String customer, String flight, String date, Timestamp pos)
    {
        String mes;
        try
        {
            Connection conn = Database.getConnection();
            waitlistCustomer = conn.prepareStatement("INSERT INTO WAITLIST"
                    + " (CUSTOMER, FLIGHT, DATE, POSITION) VALUES (?,?,?,?)");
            waitlistCustomer.setString(1, customer);
            waitlistCustomer.setString(2, flight);
            waitlistCustomer.setString(3, date);
            waitlistCustomer.setTimestamp(4, pos);
            waitlistCustomer.executeUpdate();
            mes = (customer+" was successfully waitlisted.\n");
            
        }
        catch (SQLException sqlException) 
        {
            mes = ("Unable to waitlist "+customer+".\n");
            sqlException.printStackTrace();   
        }   
        return mes;
    }
   
    public static String getWaitByDay(String date)
    {
        java.sql.Date d = java.sql.Date.valueOf(date);
        String name, flight, results = "";
        ResultSet rs = null;
        try
        {
            Connection conn = Database.getConnection();
            selectWaitByDay = conn.prepareStatement("SELECT * FROM WAITLIST"
                    +" WHERE DATE = ?");
            selectWaitByDay.setDate(1, d);
            rs = selectWaitByDay.executeQuery();
            if(rs.next())
            {
                name = rs.getString("Customer");
                flight = rs.getString("Flight");
                results = results + (name+" is waiting for flight "+flight+"\n");
                while(rs.next())
                {
                    results = results + (rs.getString("Customer")+" is waiting for flight "
                            +rs.getString("Flight")+"\n");
                }
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        return results;
    }
    public static String removeWait(String flight)
    {
        String mes = "";
        
        try
        {
            Connection conn = Database.getConnection();
            remove = conn.prepareStatement("DELETE FROM WAITLIST"
                    + " WHERE FLIGHT = ?");
            remove.setString(1, flight);
           
            remove.executeUpdate();
            
            mes = "If any customers were on the waitlist \nfor flight "+flight+", they have been \nremoved.";
        }
        catch (SQLException sqlException) 
        {
            mes = "Unable to remove customers from the waitlist for flight "+flight;
            sqlException.printStackTrace();  
        }   
        return mes;
        
    }
    
     public static String removeWait(Timestamp pos)
    {
        String mes = "";
        
        try
        {
            Connection conn = Database.getConnection();
            remove = conn.prepareStatement("DELETE FROM WAITLIST"
                    + " WHERE POSITION = ?");
            remove.setTimestamp(1, pos);
           
            remove.executeUpdate();
            
            mes = "The customer has been removed from the \nwaitlist.\n";
        }
        catch (SQLException sqlException) 
        {
            mes = "Unable to remove the customer from the \nwaitlist and book them to their desired \nflight.";
            sqlException.printStackTrace();  
        }   
        return mes;
        
    }
    public static String isWaiting(String date, String flight)
    {
        ResultSet rs = null;
        String mes = "";
        try
        {
            Connection conn = Database.getConnection();
            waiting = conn.prepareStatement("SELECT * FROM WAITLIST WHERE"
                    + " DATE=? and FLIGHT =? ORDER BY POSITION ASC");
            waiting.setString(1, date);
            waiting.setString(2, flight);
            rs = waiting.executeQuery();
            if(rs.next())
            {
                String cust = rs.getString("CUSTOMER");
                Timestamp pos = rs.getTimestamp("Position");
                mes = Booking.book(cust, flight, date, pos)+removeWait(pos);
                
            }
            else
            {
                mes = "No customers were waiting for that flight.";
            }
        }
        catch (SQLException sqlException) 
        {
            sqlException.printStackTrace();  
        }   
        return mes;
    }
     public static String customerStatus(String cust)
    {
        String results = "";
        String flight = "";
        String date = "";
        ResultSet rs = null;
        try
        {
            Connection conn = Database.getConnection();
            selectFlightByCustomer = conn.prepareStatement("SELECT * FROM"
                    + " WAITLIST WHERE CUSTOMER=?");
            selectFlightByCustomer.setString(1, cust);
            rs = selectFlightByCustomer.executeQuery();
            if(rs.next())
            {
                flight = rs.getString("FLIGHT");
                date = rs.getString("DATE");
                results = cust+" is wailisted for flight, "+flight+",\n on "+date+".\n";
                while(rs.next())
                {
                    flight = rs.getString("FLIGHT");
                    date = rs.getString("DATE");
                    results = results+ cust+" is booked on flight, "+flight+",\n on "+date+".\n"; 
                }
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return results;
    }
}
