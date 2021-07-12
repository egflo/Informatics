package week7;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TwentyFiveWriter {


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
	
	public void create_db_schema(Connection connection)
	{
		try {
		String query = "CREATE TABLE documents (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(200))";
		String query2 = "CREATE TABLE words (id INTEGER, doc_id INTEGER, value VARCHAR(200))";
		String query3= "CREATE TABLE characters (id INTEGER, word_id INTEGER,  value VARCHAR(10))";
		
		Statement state = connection.createStatement();
		state.executeUpdate(query);
		state.executeUpdate(query2);
		state.executeUpdate(query3);
		
		state.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void load_file_into_database(String path_to_file, Connection connection) throws Exception
	{
		class _extract_words
		{
			public List<String> extract_words(String path_to_file)
			{
				List<String> word_list = new ArrayList<String>();
				
				try {
								
					String str_data = new String(Files.readAllBytes(Paths.get(path_to_file)));
					Pattern pattern = Pattern.compile("[\\W]|_");	
					
					word_list =
							new ArrayList<String>(Arrays.asList(pattern.matcher(str_data).replaceAll(" ").toLowerCase().split("\\s+")));
				

					String[] stopwords = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
					List<String> stop_words = 
							new ArrayList<String>(Arrays.asList(stopwords));
					
					stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));
					
					word_list.removeIf(p -> stop_words.contains(p));

						
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return word_list;
				
			}
		}
		
		List<String> words = new _extract_words().extract_words(path_to_file);
		
		String query = "INSERT INTO documents(name) VALUES(?)";
		PreparedStatement state = connection.prepareStatement(query);
		state.setString(1, path_to_file);
		state.executeUpdate();
		state.close();
		
		query = "SELECT id FROM documents WHERE name=? LIMIT 1";
		state = connection.prepareStatement(query);
		state.setString(1, path_to_file);
		ResultSet results = state.executeQuery();
		
		int doc_id = -1;
		if(results.next())
		{
			doc_id = results.getInt(1);
		}
		
		state.close();
		results.close();
		
		
		query = "SELECT MAX(id) FROM words LIMIT 1";
		Statement state2 = connection.createStatement();
		results = state2.executeQuery(query);
		
		int row = -1;
		if(results.next())
		{
			row = results.getInt(1);
		}
		
		state.close();
		results.close();
		
		int word_id = row;
		
		if(word_id == -1)
		{
			word_id = 0;
		}
		
		
		String queryWord = "INSERT INTO words VALUES (?,?,?)";
		PreparedStatement insertWord = connection.prepareStatement(queryWord);
		
		String queryChar = "INSERT INTO characters VALUES (?,?,?)";
		PreparedStatement insertChar = connection.prepareStatement(queryChar);
		
		connection.setAutoCommit(false);
		
		for(String word: words)
		{
			
			insertWord.setInt(1, word_id);
			insertWord.setInt(2, doc_id);
			insertWord.setString(3, word);
			
			insertWord.executeUpdate();
			
			int char_id = 0;
			for(char c: word.toCharArray())
			{
				
				insertChar.setInt(1,char_id);
				insertChar.setInt(2, word_id);
				insertChar.setString(3, String.valueOf(c));
				
				char_id++;
			}
			
			word_id++;
		}
		
		connection.setAutoCommit(true);
		insertWord.close();
		insertChar.close();
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			TwentyFiveWriter tfw = new TwentyFiveWriter();
			Connection connection = tfw.getConnection();
			tfw.create_db_schema(connection);
			tfw.load_file_into_database(args[0], connection);
			connection.close();
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}
