package week8;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TwentyNine {
	public ArrayBlockingQueue<String> word_space = new ArrayBlockingQueue<String>(250000);
	public ArrayBlockingQueue<Map<String, Integer>> freq_space = new ArrayBlockingQueue<Map<String, Integer>>(10);
	public List<String> stop_words;
	public Map<String,Integer> word_freqs = new ConcurrentHashMap<String, Integer>(new HashMap<String,Integer>());
	private Lock lock = new ReentrantLock();

	public TwentyNine()
	{
		try
		{
			stop_words =
					Arrays.asList(new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(","));
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public void process_words()
	{
		Map<String,Integer> word_freqs = new HashMap<String,Integer>();
		
		while(true)
		{
			String word;
			try
			{
				word = word_space.poll(1, TimeUnit.SECONDS);
										
			}
		
			catch(Exception e)
			{
				break;
			}
			
			if(word == null)
			{
				break;
			}
			
			
			if(!stop_words.contains(word))
			{
				if(word_freqs.containsKey(word))
				{
					word_freqs.put(word, word_freqs.get(word)+ 1);
				}
				else
				{
					word_freqs.put(word, 1);
				}
			
			}
			
			if(word_space.size() == 0)
			{
				break;
			}
			
			
		}
		
		try {
			freq_space.put(word_freqs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	public void process_freqs()
	{
	
		while(true)
		{
			Map<String,Integer> freqs;
			try
			{
			
				freqs = freq_space.poll(1, TimeUnit.SECONDS);		
				
			}
		
			catch(Exception e)
			{
				break;
			}
			
			if(freqs == null)
			{
				break;
			}
			

			lock.lock();
			for(Map.Entry<String, Integer> entry: freqs.entrySet())
			{
			
				if(word_freqs.containsKey(entry.getKey()))
				{
					word_freqs.put(entry.getKey(), entry.getValue() + word_freqs.get(entry.getKey()));
			
				}
				else
				{	
					word_freqs.put(entry.getKey(), entry.getValue());		
				}				
		
			}
			
			lock.unlock();
			
			
			if(freq_space.size() == 0)
			{
				break;
			}
		}
		

	}
	
	public static void main(String[] args) {

		try
		{
			TwentyNine tn = new TwentyNine();
			
			// Let’s have this thread populate the word space
			Matcher m = Pattern.compile("[a-z]{2,}")
					.matcher(new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase());
			while(m.find())
			{
				tn.word_space.add(m.group());
			}
			
			
			List<Thread> workers = new ArrayList<Thread>();
			
			for(int i = 0; i < 5; i++)
			{
				workers.add(new Thread(tn::process_words));
			}
			
			for(Thread t: workers)
			{
				t.start();
			}
			
			for(Thread t: workers)
			{
				t.join();
			}
			
			
			List<Thread> workers_freq = new ArrayList<Thread>();
			
			for(int i = 0; i < 5; i++)
			{
				workers_freq.add(new Thread(tn::process_freqs));
			}
			
			for(Thread t: workers_freq)
			{
				t.start();
			
			}
			
			for(Thread t: workers_freq)
			{
				t.join();
			}
			
			/**
			Map<String,Integer> word_freqs = new HashMap<String,Integer>();
			while(!tn.freq_space.isEmpty())
			{
				Map<String,Integer> freqs = (Map<String, Integer>) tn.freq_space.take();
				
				for(Map.Entry<String, Integer> entry: freqs.entrySet())
				{
					int count;
					if(word_freqs.containsKey(entry.getKey()))
					{
						int freqs_count = freqs.get(entry.getKey());
						int word_freqs_count = word_freqs.get(entry.getKey());
						count = freqs_count + word_freqs_count;
					}
					else
					{
						count = freqs.get(entry.getKey());
					}
					
					word_freqs.put(entry.getKey(), count);
				}
			}
			**/
			
			List<Map.Entry<String, Integer>> freqs_sorted = 
					tn.word_freqs	
					   .entrySet().stream()
				       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
				        .collect(Collectors.toCollection(ArrayList::new));	
			
			
			for(Map.Entry<String, Integer> current: freqs_sorted.subList(0, 25))
			{
				System.out.println(current.getKey() + "  -  " + current.getValue());
			}
				
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
