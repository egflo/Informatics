package week4;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Seven {
	
	
	public void count(List<String> word_list, 
			List<String> stop_words, Map<String,Integer> word_freqs)
	{		
		if(word_list.isEmpty())
		{
			return;
		}
		
		if(word_list.size() == 1)
		{
			String word = word_list.get(0);
			//System.out.println(word);
			if(!stop_words.contains(word))
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
		}
		
		else
		{
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(word_list.get(0));
			
			count(temp, stop_words, word_freqs);
			count(word_list.subList(1, word_list.size()),stop_words,word_freqs);
		}
	
	}
	
	public void load_stop_words(String data, List<String> stop_words)
	{		
			
		if(data.length() < 2)
		{
			return;
		}
		
		else if(data.charAt(0) == ',' && data.charAt(data.length()-1) == ',')
		{
			int index = data.indexOf(',');
			int index2 = data.indexOf(',', 2);
						
			stop_words.add(data.substring(index+1, index2));
			load_stop_words(data.substring(1),stop_words);
        } 
			
		else 
		{	
			
			load_stop_words(data.substring(1),stop_words);
		
		}
	}
	
	public void wf_print(List<Map.Entry<String, Integer>> wordfreq)
	{
		if(wordfreq.isEmpty())
		{
			return;
		}
		
		if(wordfreq.size() == 1)
		{
			Map.Entry<String, Integer> map = wordfreq.get(0);
			System.out.println(map.getKey() + " - " + map.getValue());
		}
		else
		{
			List<Map.Entry<String, Integer>> temp = new ArrayList<Map.Entry<String, Integer>>();
			temp.add(wordfreq.get(0));
			
			wf_print(temp);				
			wf_print(wordfreq.subList(1, wordfreq.size()));
		}
	}
	

	public static void main(String[] args)  {
		Seven s = new Seven();	
		List<String> stop_words = new ArrayList<String>();
		List<String> word_list = new ArrayList<String>();
		try {
			
			//Can only read only once
			String data = new String(Files.readAllBytes(Paths.get("stopwords.txt")));
			s.load_stop_words(data, stop_words);
			
			Matcher m = Pattern.compile("[a-z]{2,}")
					.matcher(new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase());
			while(m.find())
			{
				word_list.add(m.group());
			}
					
			Map<String,Integer> word_freq = new HashMap<String,Integer>();					
			int RECURSION_LIMIT = 4000;
	
			//Adjusted due to differences between Java and Python Sublists
			for(int i = 0; i <= word_list.size(); i = i + RECURSION_LIMIT)
			{			
				if(i + RECURSION_LIMIT > word_list.size())
				{
					s.count(word_list.subList(i, word_list.size()), stop_words, word_freq);
				}
				
				else
				{
					s.count(word_list.subList(i, i + RECURSION_LIMIT), stop_words, word_freq);
				}			
			}
						
			s.wf_print(((HashMap<String, Integer>) word_freq)	
				   .entrySet().stream()
			       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
			        .collect(Collectors.toCollection(ArrayList::new)).subList(0, 25));
	
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
}
