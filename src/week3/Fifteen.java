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

public class Fifteen {
	
	  @FunctionalInterface
	    public interface Interface{
	        void h(ArrayList<Object> event);
	    }
	    
	
	class EventManager
	{
		private HashMap<String,ArrayList<Object>> _subscriptions;
		
		public EventManager()
		{
			this._subscriptions = new HashMap<String,ArrayList<Object>>();
		}
		
		public void subscribe(String event_type, Object handler)
		{
			if(this._subscriptions.containsKey(event_type))
			{
				this._subscriptions.get(event_type).add(handler);
			}
			
			else
			{
				ArrayList<Object> list =  new ArrayList<Object>();
				list.add(handler);
				this._subscriptions.put(event_type,list);
			}			
		}
		
		public void publish(ArrayList<Object> event)
		{
			String event_type = (String)event.get(0);
			
			if(this._subscriptions.containsKey(event_type))
			{	
		
				for(Object obj: this._subscriptions.get(event_type))
				{
					Interface h = (Interface) obj;
					h.h(event);
				}
			}
		}
		
	}
	
	class DataStorage
	{
		private EventManager _event_manager;
		private String _data = "";
		
		public DataStorage(EventManager event_manager)
		{
			this._event_manager = event_manager;
			Interface ref = (ArrayList<Object> event) -> load(event);
			this._event_manager.subscribe("load", ref);
			ref = (ArrayList<Object> event) -> produce_words(event);
			this._event_manager.subscribe("start", ref);
		}
		
		public void load(ArrayList<Object> event)
		{
			String path_to_file = (String) event.get(1);	

			try {
				this._data += new String(Files.readAllBytes(Paths.get(path_to_file)));
					
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			Pattern pattern = Pattern.compile("[\\W]|_");			
			this._data = pattern.matcher(this._data).replaceAll(" ").toLowerCase();	
	
		}
		
		public void produce_words(ArrayList<Object> event)
		{
			String data_str = String.join(" ", this._data);
			
			for(String word: data_str.split("\\s+"))
			{
				ArrayList<Object> event_ = new ArrayList<Object>();
				event_.add("word");
				event_.add(word);
				this._event_manager.publish(event_);
			}

			ArrayList<Object> event_ = new ArrayList<Object>();
			event_.add("eof");
			event_.add(null);
			this._event_manager.publish(event_);
		}
		
	}
	
	class StopWordFilter
	{
		private EventManager _event_manager;
		private ArrayList<String> _stop_words;
		
		public StopWordFilter(EventManager event_manager)
		{
			this._event_manager = event_manager;
			this._stop_words = new ArrayList<String>();
			Interface ref = (ArrayList<Object> event) -> load(event);
			this._event_manager.subscribe("load", ref);
			ref = (ArrayList<Object> event) -> is_stop_word(event);
			this._event_manager.subscribe("word", ref);			
		}
		
		public void load(ArrayList<Object> event)
		{
			try {
				String[] words = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
				Collections.addAll(this._stop_words, words);
										
			} catch (Exception e) {
				e.printStackTrace();
			}	
						
			this._stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));						
			
		}
		
		public void is_stop_word(ArrayList<Object> event)
		{
			String word = (String) event.get(1);
			if(!this._stop_words.contains(word))
			{
				ArrayList<Object> event_ = new ArrayList<Object>();
				event_.add("valid_word");
				event_.add(word);
				
				this._event_manager.publish(event_);
			}
		}
			
	}
	
	class WordFrequencyCounter
	{
		
		private EventManager _event_manager;
		private HashMap<String,Integer> _word_freqs;
		
		public WordFrequencyCounter(EventManager event_manager)
		{
			this._event_manager = event_manager;
			this._word_freqs = new HashMap<String,Integer>();
			Interface ref = (ArrayList<Object> event) -> increment_count(event);
			this._event_manager.subscribe("valid_word", ref);
			ref = (ArrayList<Object> event) -> print_freq(event);
			this._event_manager.subscribe("print", ref);			
		}
		
		public void increment_count(ArrayList<Object> event)
		{
			String word = (String) event.get(1);
			if(this._word_freqs.containsKey(word))
			{
				this._word_freqs.put(word, this._word_freqs.get(word)+1);
			}
			else
			{
				this._word_freqs.put(word, 1);
			}
		}
		
		public void print_freq(ArrayList<Object> event)
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
	
	class WordFrequencyApplication
	{
		EventManager _event_manager;
		
		public WordFrequencyApplication(EventManager event_manager)
		{
			this._event_manager = event_manager;
			Interface ref = (ArrayList<Object> event) -> run(event);
			this._event_manager.subscribe("run", ref);
			ref = (ArrayList<Object> event) -> stop(event);
			this._event_manager.subscribe("eof", ref);
		}
		
		public void run(ArrayList<Object> event)
		{
			String path_to_file = (String) event.get(1);
			ArrayList<Object> event_ = new ArrayList<Object>();
			event_.add("load");
			event_.add(path_to_file);
			this._event_manager.publish(event_);
			
			event_ = new ArrayList<Object>();
			event_.add("start");
			event_.add(null);
			this._event_manager.publish(event_);
		}
		
		public void stop(ArrayList<Object> event)
		{
			ArrayList<Object> event_ = new ArrayList<Object>();
			event_.add("print");
			event_.add(null);
			this._event_manager.publish(event_);			
		}
		
	}

	public static void main(String[] args) {
		
		Fifteen f = new Fifteen();
		EventManager em = f.new EventManager();
		f.new DataStorage(em);
		f.new StopWordFilter(em);
		f.new WordFrequencyCounter(em);
		f.new WordFrequencyApplication(em);
		
		ArrayList<Object> event_ = new ArrayList<Object>();
		event_.add("run");
		event_.add(args[0]);
				
		em.publish(event_);
	}
}
