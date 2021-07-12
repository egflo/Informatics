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

public class Eight {
	
	@FunctionalInterface
    public interface Func{
        void call(Object data, Func func);
    }

	class Read_file implements Func
	{
		@Override
		public void call(Object data, Func func) {
			String path_to_file = (String) data;
			String data_str = "";
			try {
				data_str += new String(Files.readAllBytes(Paths.get(path_to_file)));
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			func.call(data_str, new Normalize());	
		}
		
	}
	
	class Filter_chars implements Func
	{

		@Override
		public void call(Object data, Func func) {
			String str_data = (String) data;
			Pattern pattern = Pattern.compile("[\\W]|_");			
			
			func.call(pattern.matcher(str_data).replaceAll(" "), new  Scan());		
		}		
	}
	
	class Normalize implements Func
	{
		@Override
		public void call(Object data, Func func) {
			String str_data = (String) data;
			
			func.call(str_data.toLowerCase(), new  Remove_stop_words());
		}		
	}
	
	class Scan implements Func
	{
		@Override
		public void call(Object data, Func func) {
			String str_data = (String) data;
			func.call(new ArrayList<String>(Arrays.asList(str_data.split("\\s+"))), new Frequencies());			
		}		
	}
	
	class Remove_stop_words implements Func 
	{
		@SuppressWarnings("unchecked")
		@Override
		public void call(Object data, Func func) {
			ArrayList<String> word_list = (ArrayList<String>) data;
			ArrayList<String> stop_words = new ArrayList<String>();
			try {
				String[] words = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
				Collections.addAll(stop_words, words);
										
			} catch (Exception e) {
				e.printStackTrace();
			}	
						
			stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));								
			word_list.removeIf(p -> stop_words.contains(p));		
			func.call(word_list, new Sort());
		}		
	}
	
	class Frequencies implements Func
	{
		@SuppressWarnings("unchecked")
		@Override
		public void call(Object data, Func func) {
			HashMap<String,Integer> wf = new HashMap<String,Integer>();
			ArrayList<String> word_list = (ArrayList<String>) data;
			
			for(String word: word_list)
			{
				if(wf.containsKey(word))
				{
					wf.put(word, wf.get(word)+1);
				}
				else
				{
					wf.put(word, 1);
				}
			}
			
			func.call(wf, new Print_text());
		}
	}
	
	class Sort implements Func
	{

		@SuppressWarnings("unchecked")
		@Override
		public void call(Object data, Func func) {
			// TODO Auto-generated method stub
			HashMap<String,Integer> wf = (HashMap<String,Integer>) data;
			
			Object sorted =((HashMap<String, Integer>) wf)	
					.entrySet().stream()
			        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
			        .collect(Collectors.toCollection(ArrayList::new));
			
			func.call(sorted, new No_op());
			
		}
		
	}
	
	class Print_text implements Func
	{

		@SuppressWarnings("unchecked")
		@Override
		public void call(Object data, Func func) {
			ArrayList<Map.Entry<String, Integer>> freqs = (ArrayList<Map.Entry<String, Integer>>) data;
				
			for(Map.Entry<String, Integer> current: freqs.subList(0, 25))
			{
				System.out.println(current.getKey() + "  -  " + current.getValue());
			}		
			
			func.call(null, null);
		}	
	}
	
	class No_op implements Func
	{

		@Override
		public void call(Object data, Func func) {
			return;
		}
		
	}
	
	public static void main(String[] args)  {
		
		Eight e = new Eight();
		e.new Read_file().call(args[0], e.new Filter_chars());
	}
}
