package week5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class frequencies2 {

	public List<Map.Entry<String, Integer>> top25(List<String> word_list)
	{
	
		HashMap<String,Integer> word_freq = new HashMap<String,Integer>();
		for(String word: word_list)
		{
			int occurence = Collections.frequency(word_list, word );
			word_freq.put(word, occurence);
		}
	
			
		return word_freq	
		   .entrySet().stream()
	       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
	        .collect(Collectors.toCollection(ArrayList::new)).subList(0, 25);	
	}
}
