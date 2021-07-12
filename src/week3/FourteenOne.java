package week3;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FourteenOne {
	  
	@FunctionalInterface
	public interface StringInterface{
	        void h(String string);
	    }
	
	@FunctionalInterface
    public interface Interface{
        void h();
    }
	

	class WordFrequencyFramework {
		private ArrayList<Object> _load_event_handlers = new ArrayList<Object>();
		private ArrayList<Object> _dowork_event_handlers = new ArrayList<Object>();
		private ArrayList<Object> _end_event_handlers = new ArrayList<Object>();
		
		
		public void register_for_load_event(Object handler)
		{
			this._load_event_handlers.add(handler);
		}
		
		public void register_for_dowork_event(Object handler)
		{
			this._dowork_event_handlers.add(handler);
		}
		
		public void register_for_end_event(Object handler)
		{
			this._end_event_handlers.add(handler);
		}
		
		public void run(String path_to_file)
		{
			
			for(Object obj: this._load_event_handlers)
			{
				StringInterface h = (StringInterface)obj;
				h.h(path_to_file);
				
			}
			
			for(Object obj: this._dowork_event_handlers)
			{
				Interface h = (Interface)obj;
				h.h();
			}
			
			for(Object obj: this._end_event_handlers)
			{
				Interface h = (Interface)obj;
				h.h();
			}
		}
	}
		
	class DataStorage {
			
		private String _data = "";
		private StopWordFilter _stop_word_filter = null;
		private ArrayList<Object> _word_event_handlers = new ArrayList<Object>();
			
			
		public DataStorage(WordFrequencyFramework wfapp, StopWordFilter stop_word_filter)
		{
			this._stop_word_filter = stop_word_filter;
			StringInterface ref = (String path_to_file) -> _load(path_to_file);
			wfapp.register_for_load_event(ref);
			Interface ref2 = () -> _produce_words();
			wfapp.register_for_dowork_event(ref2);
				
		}
			
		public void _load(String path_to_file)
		{
			String data = "";		
			try {
				data += new String(Files.readAllBytes(Paths.get(path_to_file)));
					
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			Pattern pattern = Pattern.compile("[\\W]|_");			
			this._data = pattern.matcher(data).replaceAll(" ").toLowerCase();
		}
			
		public void _produce_words()
		{
			String data_str = String.join(" ", this._data);
				
			for(String word: data_str.split("\\s+"))
			{
				if(!this._stop_word_filter.is_stop_word(word))
				{
					for(Object obj: this._word_event_handlers)
					{
						StringInterface h = (StringInterface)obj;
						h.h(word);
					}
				}
			}
		}
			
		public void register_for_word_event(Object handler)
		{
			this._word_event_handlers.add(handler);
		}
	}
		
		
	class StopWordFilter
	{
		private List<String> _stop_words = new ArrayList<String>();
			
		public StopWordFilter(WordFrequencyFramework wfapp)
		{
			StringInterface ref = (String path_to_file) -> _load(path_to_file);
			wfapp.register_for_load_event(ref);	
		}
			
		public void _load(String ignore)
		{
			try {
				String[] words = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
				Collections.addAll(this._stop_words, words);
										
			} catch (Exception e) {
				e.printStackTrace();
			}	
						
			this._stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));						
				
		}
			
		public boolean is_stop_word(String word)
		{
			return this._stop_words.contains(word);
		}
	}	
	
	class WordFrequencyCounter
	{
		HashMap<String,Integer> _word_freqs = new HashMap<String,Integer>();
		
		public WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage data_storage)
		{	
				StringInterface ref = (String word) -> _increment_count(word);
				data_storage.register_for_word_event(ref);
				Interface ref2 = () -> _print_freqs();
				wfapp.register_for_end_event(ref2);	
		}
		
		public void _increment_count(String word)
		{
			if(this._word_freqs.containsKey(word))
			{
				this._word_freqs.put(word, this._word_freqs.get(word)+1);
			}
			else
			{
				this._word_freqs.put(word, 1);
			}
		}
		
		public void _print_freqs()
		{
			List<Map.Entry<String,Integer>> list = 
					new ArrayList<Map.Entry<String,Integer>>();	
			
			for(String word: this._word_freqs.keySet()){
				Integer frequency = this._word_freqs.get(word);
				list.add(new AbstractMap.SimpleEntry<String,Integer>(word, frequency));
			}
			
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				  @Override
				  public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				    return Integer.compare(o2.getValue(),o1.getValue());
				  }
				});
			
			for(Map.Entry<String, Integer> current: list.subList(0, 25))
			{
				System.out.println(current.getKey() + "  -  " + current.getValue());
			}
	
		}
		
	}
	
	public static void main(String[] args) {

		FourteenOne f = new FourteenOne();
		WordFrequencyFramework wfapp = f.new WordFrequencyFramework();
		StopWordFilter stop_word_filter = f.new StopWordFilter(wfapp);
		DataStorage data_storage = f.new DataStorage(wfapp,stop_word_filter);
		WordFrequencyCounter word_freq_counter = f.new WordFrequencyCounter(wfapp,data_storage);
		wfapp.run(args[0]);
	}

}
