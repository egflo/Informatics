package week3;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Twelve {
	
	public HashMap<String,Object> data_storage_obj;
	public HashMap<String,Object> stop_words_obj;
	public HashMap<String,Object> word_freq_obj;
	
    @FunctionalInterface
    public interface ExtractInterface{
        void extract_words(HashMap<String,Object> obj, String path_to_file);
    }
    
    @FunctionalInterface
    public interface StopInterface{
        void load_stop_words(HashMap<String,Object> obj);
    }
    
    @FunctionalInterface
    public interface IsStopInterface{
        boolean isStopWord(String word);
    }
    
    @FunctionalInterface
    public interface IncrementInterface{
    	void increment_count(HashMap<String,Object> obj,String word);
    }
    
    
	@SuppressWarnings("unchecked")
	public Twelve ()
	{
		data_storage_obj = new HashMap<String,Object>();
		data_storage_obj.put("data", new ArrayList<String>());
		ExtractInterface reference = (HashMap<String,Object> obj, String path_to_file) -> extract_words(data_storage_obj,path_to_file);	
	    data_storage_obj.put("init", reference);
		data_storage_obj.put("words", data_storage_obj.get("data"));
		
		stop_words_obj = new HashMap<String,Object>();
		stop_words_obj.put("stop_words", new ArrayList<String>());
		StopInterface referenceStop = (HashMap<String,Object> obj)-> load_stop_words(obj);
		stop_words_obj.put("init", referenceStop);
		IsStopInterface referenceIsStop = (String word) -> isStopWord(word);
		stop_words_obj.put("is_stop_word", referenceIsStop);
		
		word_freq_obj = new HashMap<String,Object>();
		word_freq_obj.put("freqs", new HashMap<String,Integer>());
		IncrementInterface referenceIncrement = (HashMap<String,Object> obj,String word) -> increment_count(obj,word);
		word_freq_obj.put("increment_count", referenceIncrement);
		word_freq_obj.put("sorted",((HashMap<String, Integer>) word_freq_obj.get("freqs"))	
				.entrySet().stream()
		        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
		        .collect(Collectors.toCollection(ArrayList::new)));
		
	}
	
	//Can't do the Python lambda with Java Using Objects
	@SuppressWarnings("unchecked")
	public boolean isStopWord(String word)
	{
		ArrayList<String> stop_words = (ArrayList<String>)stop_words_obj.get("stop_words");
		return stop_words.contains(word);
	}
	

	public void extract_words(HashMap<String,Object> obj, String path_to_file)
	{		
		try {
	
			obj.put("data", new String(Files.readAllBytes(Paths.get(path_to_file))));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		Pattern pattern = Pattern.compile("[\\W]|_");
		String data_str = String.join("", pattern.matcher((String)obj.get("data")).replaceAll(" ").toLowerCase());
		ArrayList<String> data = new ArrayList<String>(Arrays.asList(data_str.split("\\s+")));
		obj.put("data", data);
		obj.put("words",data);
	}
	
	@SuppressWarnings("unchecked")
	public void load_stop_words(HashMap<String,Object> obj)
	{
		try {
			ArrayList<String> stop_words = new ArrayList<String>(
					Arrays.asList(new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",")));
			obj.put("stop_words", stop_words);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		ArrayList<String> stop_words = (ArrayList<String>)obj.get("stop_words");
		stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));
		obj.put("stop_words", stop_words);		
	}
	
	@SuppressWarnings("unchecked")
	public void increment_count(HashMap<String,Object> obj,String word)
	{
		HashMap<String,Integer> map = (HashMap<String,Integer>) obj.get("freqs");
		if(map.containsKey(word))
		{
			map.put(word, map.get(word)+1);
		}
		else
		{
			map.put(word, 1);
		}	
		
		obj.put("freqs", map);
		
	}
			
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		Twelve t = new Twelve();

		ExtractInterface e = (ExtractInterface) t.data_storage_obj.get("init");
		e.extract_words(t.data_storage_obj, args[0]);
		
		StopInterface s = (StopInterface)  t.stop_words_obj.get("init");
		s.load_stop_words(t.stop_words_obj);
		
		for(String word: (ArrayList<String>)t.data_storage_obj.get("words"))
		{
			IsStopInterface is = (IsStopInterface)t.stop_words_obj.get("is_stop_word");
			if(!is.isStopWord(word))
			{			
				IncrementInterface ii = (IncrementInterface) t.word_freq_obj.get("increment_count");
				ii.increment_count(t.word_freq_obj, word);
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		t.word_freq_obj.put("top25", ((HashMap<String, Integer>) t.word_freq_obj.get("freqs"))
				.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
				.limit(25)
				.collect(Collectors.toCollection(ArrayList::new)));
	
		for(Map.Entry<String, Integer> current: (ArrayList<Map.Entry<String, Integer>>) t.word_freq_obj.get("top25"))
		{
			System.out.println(current.getKey() + "  -  " + current.getValue());
		}		
	}
}
