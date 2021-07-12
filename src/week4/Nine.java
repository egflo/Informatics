package week4;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Nine {
	
	public interface Func
	{
		Object run(Object value);
	}
	
	class TFTheOne {
		private Object _value;
		
		public TFTheOne(Object v){
			this._value = v;
		}
		
		public TFTheOne bind(Func func)
		{
			this._value = func.run(this._value);
			return this;
		}
		
		public void printme()
		{
			System.out.println((String)this._value);
		}
		
	}
	
	
	class read_file implements Func
	{

		@Override
		public Object run(Object path_to_file) {
			String data = "";
			try {
				data += new String(Files.readAllBytes(Paths.get((String) path_to_file)));
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return data;	
		}
		
	}
	
	class filter_chars implements Func 
	{

		@Override
		public Object run(Object str_data) {
			Pattern pattern = Pattern.compile("[\\W]|_");
			return pattern.matcher((String)str_data).replaceAll(" ");
		}
		
	}
	
	class normalize implements Func
	{
		public Object run(Object str_data)
		{
			return ((String) str_data).toLowerCase();
		}
		
	}

	class scan implements Func
	{
		public Object run(Object str_data)
		{
			return new ArrayList<String>(Arrays.asList(((String) str_data).split("\\s+")));
		}
			
	}

	class remove_stop_words implements Func
	{
		@SuppressWarnings("unchecked")
		public Object run(Object word_list)
		{
			ArrayList<String> stop_words = new ArrayList<String>();
			try {
				String[] words = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
				Collections.addAll(stop_words, words);
										
			} catch (Exception e) {
				e.printStackTrace();
			}	
						
			stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));	
			ArrayList<String> new_word_list = (ArrayList<String>) word_list;
			new_word_list.removeIf(p -> stop_words.contains(p));
			
			return new_word_list;
		}	
	}
	
	class frequencies implements Func
	{
		@SuppressWarnings("unchecked")
		public Object run(Object word_list)
		{
			Map<String,Integer> word_freqs = new HashMap<String,Integer>();
				
			for(String word: (ArrayList<String>)word_list)
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
			
			return word_freqs;
		}
				
	}
	
	class sort implements Func
	{
		@SuppressWarnings("unchecked")
		public Object run(Object word_freq)
		{
			ArrayList<Map.Entry<String, Integer>> sorted =
					((Map<String, Integer>) word_freq)	
					.entrySet().stream()
			        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
			        .collect(Collectors.toCollection(ArrayList::new));
			return sorted;
		}
		
	}

	class top25_freqs implements Func
	{
		@SuppressWarnings("unchecked")
		public Object run(Object word_freqs)
		{
			String top25 = "";
			ArrayList<Map.Entry<String, Integer>> freqs = (ArrayList<Map.Entry<String, Integer>>) word_freqs;
			
			for(Map.Entry<String, Integer> current: freqs.subList(0, 25))
			{
				top25 += current.getKey() + "  -  " + current.getValue() + "\r\n";
			}		
			
			return top25;
		}
		
	}

	public static void main(String[] args) {
		Nine n = new Nine();
		n.new TFTheOne(args[0]).bind(n.new read_file()).bind(n.new filter_chars())
			.bind(n.new normalize()).bind(n.new scan()).bind(n.new remove_stop_words())
			.bind(n.new frequencies()).bind(n.new sort()).bind(n.new top25_freqs())
			.printme();
	}

}
