package week5;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class words1 {

	public List<String> extract_words(String path_to_file)
	{
		List<String> word_list = new ArrayList<String>();
		
		try {
						
			String str_data = new String(Files.readAllBytes(Paths.get(path_to_file)));
			Pattern pattern = Pattern.compile("[\\W]|_");	
			
			word_list =
					new ArrayList<String>(Arrays.asList(pattern.matcher(str_data).replaceAll(" ").toLowerCase().split("\\s+")));
		

			String[] stopwords = new String(Files.readAllBytes(Paths.get(path_to_file))).split(",");
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
