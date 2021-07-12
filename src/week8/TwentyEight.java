package week8;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TwentyEight {

	public interface dispatch
	{
		public void _dispatch(List<Object> message);
	}
	
	public abstract class ActiveWFObject extends Thread implements dispatch
	{
		public String _name;
		public ArrayBlockingQueue<Object> queue;
		public boolean _stop;
		
		public ActiveWFObject()
		{
			super("ActiveWFObject");
			this._name = this.toString();
			this.queue = new ArrayBlockingQueue<Object>(100000);
			this._stop = false;
			this.start();
		}
		
		@Override
		public void run() {
			try{
				 while(!this._stop)
				 {
					 @SuppressWarnings("unchecked")
					 List<Object> message = (List<Object>)this.queue.take();
					 _dispatch(message);
					 if(message.get(0) == "die")
					 {
						 this._stop = true;
					 }							 
				 }										
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		
		}
		
	}
	
	public void send(Object receiver, Object message)
	{
		ActiveWFObject obj = (ActiveWFObject) receiver;
		try {
			obj.queue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public class DataStorageManager extends ActiveWFObject
	{
		public String _data = "";
		
		public List<String> _stop_words;
		public WordFrequencyManager _word_freqs_manager;
		
		public void _dispatch(List<Object> message)
		{
			String msg = (String) message.get(0);
			if(msg.compareToIgnoreCase("init") == 0)
			{
				_init(message.subList(1, message.size()));
			}
			else if(msg.compareToIgnoreCase("send_word_freqs") == 0)
			{
				_process_words(message.subList(1, message.size()));
			}
			else
			{
				//forward
				send(this._word_freqs_manager, message);
			}
		}
		
		public void _init(List<Object> message)
		{
			try {
				
				String path_to_file = (String)message.get(0);
				
				String str_data;
				str_data = new String(Files.readAllBytes(Paths.get(path_to_file)));
				Pattern pattern = Pattern.compile("[\\W]|_");	
				this._data = pattern.matcher(str_data).replaceAll(" ").toLowerCase();
				
				String[] stopwords = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
				List<String> stop_words = 
						new ArrayList<String>(Arrays.asList(stopwords));
				
				stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));
				this._stop_words = stop_words;
				
				this._word_freqs_manager = (WordFrequencyManager) message.get(1);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
	
			
		}
		
		public void _process_words(List<Object> message)
		{
			Object recipient = message.get(0);
			String data_str = "";
			data_str += this._data;
			String[] words = data_str.split("\\s+");
			for(String word: words)
			{
				if(!this._stop_words.contains(word))
				{
					List<Object> new_msg = new ArrayList<Object>();
					new_msg.add("word");
					new_msg.add(word);
					send(this._word_freqs_manager,new_msg);
				}
			}
			
			List<Object> new_msg = new ArrayList<Object>();
			new_msg.add("top25");
			new_msg.add(recipient);
			send(this._word_freqs_manager, new_msg);
		}
		
	}
	
	public class WordFrequencyManager extends ActiveWFObject
	{
		public Map<String,Integer> _word_freqs = new HashMap<String,Integer>();
		
		public void _dispatch(List<Object> message)
		{
			String msg = (String) message.get(0);
			
			if(msg.compareToIgnoreCase("word") == 0)
			{
				_increment_count(message.subList(1, message.size()));
			}
			else if(msg.compareToIgnoreCase("top25") == 0)
			{
				_top25(message.subList(1, message.size()));
			}
		}
		
		public void _increment_count(List<Object> message)
		{
			String word = (String) message.get(0);
			if(this._word_freqs.containsKey(word))
			{
				this._word_freqs.put(word, this._word_freqs.get(word)+1);
			}
			
			else
			{
				this._word_freqs.put(word, 1);
			}
			
		}
		
		public void _top25(List<Object> message)
		{
			Object recipient = message.get(0);
			List<Map.Entry<String, Integer>> freqs_sorted = 
					this._word_freqs	
					   .entrySet().stream()
				       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
				        .collect(Collectors.toCollection(ArrayList::new));	
			
			
			List<Object> new_msg = new ArrayList<Object>();
			new_msg.add("top25");
			new_msg.add(freqs_sorted);
			send(recipient, new_msg);
		}
		
	}
	
	public class WordFrequencyController extends ActiveWFObject
	{
		public DataStorageManager _storage_manager;

		public void _dispatch(List<Object> message)
		{
			String msg = (String) message.get(0);
			if(msg.compareToIgnoreCase("run") == 0)
			{
				_run(message.subList(1, message.size()));
			}
			else if(msg.compareToIgnoreCase("top25") == 0)
			{
				_display(message.subList(1, message.size()));
			}
			else
			{
				//forward
				System.out.println("Message Not Understood");
			}			
		}
		
		public void _run(List<Object> message)
		{
			this._storage_manager = (DataStorageManager) message.get(0);
			List<Object> new_msg = new ArrayList<Object>();
			new_msg.add("send_word_freqs");
			new_msg.add(this);
			send(this._storage_manager, new_msg);
		}
		
		
		public void _display(List<Object> message)
		{
			@SuppressWarnings("unchecked")
			List<Map.Entry<String, Integer>> word_freqs = (List<Entry<String, Integer>>) message.get(0);
			
			
			for(Map.Entry<String, Integer> word: word_freqs.subList(0, 25))
			{
				System.out.println(word.getKey() + "  -  " + word.getValue());
			}
			
			List<Object> new_msg = new ArrayList<Object>();
			new_msg.add("die");
			send(this._storage_manager,new_msg);
			this._stop = true;
			
		}
	}
	
	public static void main(String[] args) {
		TwentyEight te = new TwentyEight();
		
		WordFrequencyManager word_freq_manager = te.new WordFrequencyManager();	
				
		DataStorageManager storage_manager = te.new DataStorageManager();
		
		List<Object> msg = new ArrayList<Object>();
		msg.add("init");
		msg.add(args[0]);
		msg.add(word_freq_manager);

		te.send(storage_manager, msg);
		
		WordFrequencyController wfcontroller = te.new WordFrequencyController();
		
		List<Object> msg2 = new ArrayList<Object>();
		msg2.add("run");
		msg2.add(storage_manager);

		te.send(wfcontroller, msg2);
		
		List<ActiveWFObject> objects = new ArrayList<ActiveWFObject>();
		objects.add(word_freq_manager);
		objects.add(storage_manager);
		objects.add(wfcontroller);
		
		try {
			for(ActiveWFObject t: objects)
			{
				t.join();
			}

		} catch (InterruptedException e) {
			System.out.println(e);
		}
		
		
	}

}
