package week7;


import java.sql.*;

public class TwentyFiveReader {
	public Connection getConnection()
	{
		Connection connection = null;
		
		try 
	    {
	    	// Incorporate mySQL driver
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
	        
	    	// Connect to the test database
	        connection = DriverManager.getConnection("jdbc:mysql:///tf_db?useSSL=false","root","35152766");
	    	
	    }
		catch(Exception e)
		{
			System.out.println(e);
		}		
		
		return connection;
	}
	
	public void read_top25(Connection connection) throws Exception
	{
		String query = "SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC LIMIT 25";
		Statement statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery(query);
		
		while(rs.next())
		{
			String word = rs.getString(1);
			int count = rs.getInt(2);
			
			System.out.println(word + "  -  "+ count);
		}
		
	}
	public static void main(String[] args) {
		try
		{
			TwentyFiveReader tfr = new TwentyFiveReader();
			Connection connection = tfr.getConnection();
			tfr.read_top25(connection);
			connection.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	

}
