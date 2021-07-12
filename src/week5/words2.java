package week5;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class words2 {
	public List<String> extract_words(String path_to_file)
	{
		List<String> word_list = new ArrayList<String>();
		
		try {
						
			Matcher m = Pattern.compile("[a-z]{2,}")
					.matcher(new String(Files.readAllBytes(Paths.get(path_to_file))).toLowerCase());
			
			while(m.find())
			{
				word_list.add(m.group());
			}
			
			String[] stopwords = new String(Files.readAllBytes(Paths.get(path_to_file))).split(",");
			List<String> stop_words = 
					new ArrayList<String>(Arrays.asList(stopwords));
			
			word_list.removeIf(p -> stop_words.contains(p));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return word_list;
	}
	
}
