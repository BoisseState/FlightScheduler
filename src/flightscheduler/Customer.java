package flightscheduler;

import java.sql.*;
import java.util.ArrayList;


 // @author JoeBoisse
 
public class Customer 
{
    private static PreparedStatement newCustomer;
    private static PreparedStatement selectCustomers;
    
    
    
    public static ArrayList < String > getCustomers()
    {
        ArrayList < String > results = new ArrayList < >();
        ResultSet rs = null;
        String name;
        try
        {
            Connection conn = Database.getConnection();
            selectCustomers = conn.prepareStatement("SELECT * FROM CUSTOMER");
            rs = selectCustomers.executeQuery();
            if(rs.next())
                {
                    name = rs.getString("NAME");
                    results.add(name);
                    while(rs.next())
                    {
                        results.add(rs.getString("NAME"));
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
    
    public static String addCustomer(String name)
    {
        String mes = "";
        try
        {
            Connection conn = Database.getConnection();
            newCustomer = conn.prepareStatement("INSERT INTO CUSTOMER"
                    + " (NAME) VALUES (?)");
            newCustomer.setString(1, name);
            newCustomer.executeUpdate();
            mes = name+" was successfully added to the database.";
        }
        catch (SQLException sqlException) 
        {
            mes = "Unable to add the customer to the database.";
            sqlException.printStackTrace();  
        }   
        return mes;
    }
    
    public static String Status(String cust)
    {
        return Booking.customerStatus(cust) + Waitlist.customerStatus(cust);
    }
    
    
}
