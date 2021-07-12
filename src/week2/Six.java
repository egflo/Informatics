package week2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Six {

	public static void main(String[] args) {
	
		try {
		
			List<String> stops = Arrays.asList(new String(Files.readAllBytes(Paths.get("stopwords.txt"))).toLowerCase().split(","));
	
			List<String> words = Arrays.asList(
					new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase().trim().split("[^A-Za-z0-9]"));
			
			List<Map.Entry<String, Integer>> unique_words = new ArrayList<Map.Entry<String, Integer>>();
			for(String word: words){ if(word.length() > 1 && !stops.contains(word)) unique_words.add(
					new AbstractMap.SimpleEntry<String, Integer>(word, Collections.frequency(words, word)));}			
			
			unique_words = new ArrayList<Map.Entry<String, Integer>>(new HashSet<Map.Entry<String, Integer>>(unique_words));
	
			//Collections.sort(words, Comparator.comparing(Map.Entry<String, Integer>>>p -> -<Pair<String, Integer>>p.getRight());
			Collections.sort(unique_words, new Comparator<Map.Entry<String, Integer>>() {
				  @Override
				  public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				    return Integer.compare(o2.getValue(),o1.getValue());
				  }
				});
			
			for(Map.Entry<String, Integer> x :  unique_words.subList(0, 25)) {System.out.println(x.getKey() + "  -  " + x.getValue());}
		}
		
		
		catch(Exception e) {
			System.out.println(e);
		}
	}

}
