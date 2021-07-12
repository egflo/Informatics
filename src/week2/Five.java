package week2;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Five {

	public static String read_file(String path_to_file) {
		
		String data = "";		
		try {
			data += new String(Files.readAllBytes(Paths.get(path_to_file)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return data;
	}
	
	public static String filter_chars_and_normalize(String str_data){
		
		Pattern pattern = Pattern.compile("[\\W]|_");
		
		return pattern.matcher(str_data).replaceAll(" ").toLowerCase();
		
	}
	
	public static String[] scan(String str_data) {
		
		return str_data.split("\\s+");	
	}
	
	
	public static class Remove_stop_words {

		private String[] word_list;

		public Remove_stop_words(String[] word_list){
			this.word_list = word_list;
		}
		
		public List<String> remove_stop_words(String stop_word_path){
			
			List<String> words = new ArrayList<String>();	
			Set<String> stop_words;	
			try {
				Scanner input = new Scanner(new File(stop_word_path));
				stop_words = new HashSet<String>(Arrays.asList(input.nextLine().toLowerCase().split(",")));
				input.close();
				
				Set<String> ascii = new HashSet<String>(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));  
				stop_words.addAll(ascii);
			  
				for(String word: word_list){
				  if(!stop_words.contains(word)) {
					 
					  words.add(word);
				  }
			  }			
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return words;
		}
	}

	public static List<Map.Entry<String, Integer>> frequencies(List<String> word_list) {
		/**
		HashMap<String, Integer> map = new HashMap<String, Integer>();	
		for(String word: word_list) {	
			if(!map.containsKey(word)) {		
				map.put(word, 1);
			}		
			else{
				Integer frequency = map.get(word);
				map.put(word, frequency+1);
			}
		}
		*/			
		List<Map.Entry<String,Integer>> list = 
				new ArrayList<Map.Entry<String,Integer>>();	
		
		
		for(String word: word_list)
		{
			Map.Entry<String, Integer> obj = new AbstractMap.SimpleEntry<String,Integer>(word, 1);
			if(!list.contains(obj.getKey()))
			{
				list.add(new AbstractMap.SimpleEntry<String,Integer>(word, 1));
			}
			
			else
			{
				System.out.println("Yes");
				int index = list.indexOf(word);
				Map.Entry<String,Integer> value = list.get(index);
				int freq = value.getValue() + 1;
				list.get(index).setValue(freq);
			}
			
		}
		
		//for(String word: map.keySet()){
		//	Integer frequency = map.get(word);
		//	list.add(new AbstractMap.SimpleEntry<String,Integer>(word, frequency));
		//}
		
		return list;		
	}
	
	public static List<Map.Entry<String, Integer>> sort(List<Map.Entry<String, Integer>> word_freq){
				
		Collections.sort(word_freq, new Comparator<Map.Entry<String, Integer>>() {
			  @Override
			  public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
			    return Integer.compare(o2.getValue(),o1.getValue());
			  }
			});
		return word_freq;
	}
	
	public static void print_all(List<Map.Entry<String, Integer>> word_freq){
		
		
		if(word_freq.size() > 0) {
			System.out.println(word_freq.get(0).getKey() + "  -  " + word_freq.get(0).getValue());
			print_all(word_freq.subList(1, word_freq.size()));
		}
		
	}
	
	public static void main(String[] args) {
		print_all(sort(frequencies(new Remove_stop_words(scan(
				filter_chars_and_normalize(read_file(args[0])))).remove_stop_words(args[1]))).subList(0, 25));
		
	}
}
