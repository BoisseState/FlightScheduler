package flightscheduler;

import java.util.Date;
import java.sql.*;
import java.util.Calendar;
 // @author JoeBoisse

public class Booking 
{
    private static PreparedStatement bookCustomer;
    private static PreparedStatement getFlightSeats;
    private static PreparedStatement takenSeats;
    private static PreparedStatement selectFlightByDay;
    private static PreparedStatement selectFlightByCustomer;
    private static PreparedStatement updateBooking;
    private static PreparedStatement cancelCust;
    private static PreparedStatement remove;
    private static PreparedStatement searchBookings;
    private static PreparedStatement available;
    
    public static String book(String customer, String flight, String date)
    {
        Timestamp pos;
        pos = new Timestamp(Calendar.getInstance().getTime().getTime());
        String mes = null;
        try
        {
            Connection conn = Database.getConnection();
            bookCustomer = conn.prepareStatement("INSERT INTO BOOKING"
                + " (CUSTOMER, FLIGHT, DATE, POSITION) VALUES (?,?,?,?)");
            bookCustomer.setString(1, customer);
            bookCustomer.setString(2, flight);
            bookCustomer.setString(3, date);
            bookCustomer.setTimestamp(4, pos);
            bookCustomer.executeUpdate();
            mes = (customer+" was successfully booked on flight, \n"+flight
            + " on "+date);
        }
        catch (SQLException sqlException) 
        {
            mes = customer+" could not be booked on flight, "+flight;
            sqlException.printStackTrace();   
        } 
        return mes;
    }
    
    public static String book(String customer, String flight, String date, Timestamp pos)
    {
        String mes = null;
        try
        {
            Connection conn = Database.getConnection();
            bookCustomer = conn.prepareStatement("INSERT INTO BOOKING"
                + " (CUSTOMER, FLIGHT, DATE, POSITION) VALUES (?,?,?,?)");
            bookCustomer.setString(1, customer);
            bookCustomer.setString(2, flight);
            bookCustomer.setString(3, date);
            bookCustomer.setTimestamp(4, pos);
            bookCustomer.executeUpdate();
            mes = (customer+" was successfully booked on flight,\n"+flight
            + " on "+date+".");
        }
        catch (SQLException sqlException) 
        {
            mes = customer+" could not be booked on flight, "+flight;
            sqlException.printStackTrace();   
        } 
        return mes;
    }
    
    public static boolean seats(String flight, String date)
    {
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        int numSeats = 2;
        int bookedSeats = 0;
        java.sql.Date d = java.sql.Date.valueOf(date);
        try
        {
            Connection conn = Database.getConnection();
            takenSeats = conn.prepareStatement("SELECT COUNT(FLIGHT) FROM BOOKING"
                    +" WHERE FLIGHT = ? AND DATE = ?");
            getFlightSeats = conn.prepareStatement("SELECT SEATS FROM FLIGHT"
                    + " WHERE FLIGHTNAME = ?");
            
            getFlightSeats.setString(1, flight);
            rs1 = getFlightSeats.executeQuery();
            rs1.next();
            numSeats = rs1.getInt("SEATS");
            
            takenSeats.setString(1, flight);
            takenSeats.setDate(2, d);
            rs2 = takenSeats.executeQuery();
            rs2.next();
            bookedSeats = rs2.getInt(1); 
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        if(bookedSeats < numSeats)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String bookingStatus (String flight, String date)
    {
        String results = "";
        ResultSet rs = null;
        java.sql.Date d = java.sql.Date.valueOf(date);
        try
        {
            Connection conn = Database.getConnection();
            selectFlightByDay = conn.prepareStatement("SELECT CUSTOMER FROM BOOKING"
                    +" WHERE FLIGHT=? AND DATE=?");
            selectFlightByDay.setString(1, flight);
            selectFlightByDay.setDate(2, d);
            rs = selectFlightByDay.executeQuery();
            rs.next();
            String name = rs.getString("CUSTOMER");
            results = name;
            while(rs.next())
            {
                results = results +"\n"+ (rs.getString("Customer"));
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }

        return results;
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
                    + " BOOKING WHERE CUSTOMER=?");
            selectFlightByCustomer.setString(1, cust);
            rs = selectFlightByCustomer.executeQuery();
            if(rs.next())
            {
                flight = rs.getString("FLIGHT");
                date = rs.getString("DATE");
                results = cust+" is booked on flight, "+flight+",\n on "+date+".\n";
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
    
    public static String reBook (String flight)
    {
        String mes = "";
        String newFlight;
        ResultSet rs = null;
        try
        {
            Connection conn = Database.getConnection();
            searchBookings = conn.prepareStatement("SELECT * FROM BOOKING WHERE"
                    + " FLIGHT=? order by date, position asc");
            searchBookings.setString(1, flight);
            rs = searchBookings.executeQuery();
            if(rs.next())
            {
                String cust = rs.getString("CUSTOMER");
                String date = rs.getString("DATE");
                Timestamp pos =rs.getTimestamp("POSITION");
                newFlight = availableFlight(date);
                if(newFlight != null)
                {
                    updateBooking = conn.prepareStatement("UPDATE BOOKING SET"
                            + " FLIGHT = ? WHERE POSITION =?");
                    updateBooking.setString(1, newFlight);
                    updateBooking.setTimestamp(2, pos);
                    updateBooking.executeUpdate();
                    mes = cust+" was succesfully rebooked on flight \n"+newFlight+".\n";
                }
                else
                {
                    if(Flights.getFlights().size()!=0)
                    {
                       flight = Flights.getFlights().get(0);
                       mes = Waitlist.waitlist(cust, flight, date, pos);
                    }      
                    removeBooking(pos);
                    
                }
                while(rs.next())
                {
                    cust = rs.getString("CUSTOMER");
                    date = rs.getString("DATE");
                    pos =rs.getTimestamp("POSITION");
                    newFlight = availableFlight(date);
                    if(newFlight != null)
                    {
                        updateBooking = conn.prepareStatement("UPDATE BOOKING SET"
                                + " FLIGHT = ? WHERE POSITION =?");
                        updateBooking.setString(1, newFlight);
                        updateBooking.setTimestamp(2, pos);
                        updateBooking.executeUpdate();
                        mes = mes+cust+" was succesfully rebooked on flight \n"+newFlight+".\n";
                    }
                     else
                    {
                        if(Flights.getFlights().size()!=0)
                        {
                            flight = Flights.getFlights().get(0);
                            mes = mes +Waitlist.waitlist(cust, flight, date, pos);
                        }      
                        removeBooking(pos);
                    
                    }
                
                }
            
            }
        }
        
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        return mes;
        
    }
    public static String availableFlight(String date)
    {
        String flight;
        ResultSet rs = null;
        boolean b = false;
        try
        {
            Connection conn = Database.getConnection();
            available = conn.prepareStatement("SELECT * FROM FLIGHT");
            rs = available.executeQuery();
            if(rs.next())
            {
                flight = rs.getString("FLIGHTNAME");
                if(seats(flight, date))
                {
                    b = true;
                    return flight;
                }
                while(!b && rs.next())
                {
                    flight = rs.getString("FLIGHTNAME");
                    if(seats(flight, date))
                    {
                        b = true;
                        return flight;
                    }
                    
                }
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return null;
    }
    
    public static void removeBooking(Timestamp pos)
    {
        try
        {
            Connection conn = Database.getConnection();
            remove = conn.prepareStatement("DELETE FROM BOOKING WHERE "
                    + "POSITION =?");
            remove.setTimestamp(1, pos);
            remove.executeUpdate();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
    }
    
    public static String cancelBooking(String cust, String date)
    {
        String mes = "";
        ResultSet rs = null;
      
        try
        {
            Connection conn = Database.getConnection();
            cancelCust = conn.prepareStatement(" SELECT * FROM BOOKING WHERE "
                    + "CUSTOMER=? AND DATE=?");
            cancelCust.setString(1, cust);
            cancelCust.setString(2, date);
            rs = cancelCust.executeQuery();
            if(rs.next())
            {
                String flight = rs.getString("FLIGHT");
                Timestamp pos = rs.getTimestamp("POSITION");
                removeBooking(pos);
                mes = cust+" booking was cancelled.\n"
                    +Waitlist.isWaiting(date, flight);
            }
            
        }
        catch(SQLException sqlException)
        {
            mes = "Unable to delete booking.";
            sqlException.printStackTrace();
        }
        return mes;
    }
}
