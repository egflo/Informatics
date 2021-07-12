package week5;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class frequencies1 {

	public List<Map.Entry<String, Integer>> top25(List<String> word_list)
	{
		Map<String,Integer> word_freqs = new HashMap<String,Integer>();
		
		for(String word: word_list)
		{
			if(word_freqs.containsKey(word))
			{
				word_freqs.put(word, word_freqs.get(word)+1);
			}
			else
			{
				word_freqs.put(word, 1);
			}
		}
			
		return word_freqs	
		   .entrySet().stream()
	       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
	        .collect(Collectors.toCollection(ArrayList::new)).subList(0, 25);	
	}
}
