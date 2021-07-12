package week7;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TwentySeven {

	public class line implements Iterator<String>
	{
		public Scanner scanner;
		public line(String filename)
		{
			File file = new File(filename);
			try {
				scanner = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public boolean hasNext() {
			boolean next = scanner.hasNext();		
			return next;
		}

		@Override
		public String next() {
						
			return scanner.nextLine();
		}			
	}

	public class all_words implements Iterator<List<String>>
	{
		public String filename;
		public Iterator<String> it;
		
		public all_words(String filename)
		{
			this.filename = filename;
			TwentySeven ts = new TwentySeven();
			it = ts.new line(filename);
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public List<String> next() {
			Pattern pattern = Pattern.compile("[\\W]|_");							
			List<String> words = 
					new ArrayList<String>(Arrays.asList(pattern.matcher(it.next()).replaceAll(" ").toLowerCase().split("\\s+")));	
			return words;		
		}
	}
	
	public class non_stop_words implements Iterator<List<String>>
	{
		public String filename;
		public List<String> stop_words;
		public Iterator<List<String>> it;
		
		public non_stop_words(String filename)
		{
			this.filename = filename;
			TwentySeven ts = new TwentySeven();
			it = ts.new all_words(filename);
		}
	
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public List<String> next() {
			try {
				
				String[] stopwords = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
				stop_words = 
						new ArrayList<String>(Arrays.asList(stopwords));
				
				stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		    List<String> words = new ArrayList<String>();
			for(String w: it.next())
			{
				if(!stop_words.contains(w) && !w.isEmpty())
				{
					words.add(w);
				}
				
			}
			return words;	
		}		
	}
	
	
	public class count_and_sort implements Iterator<Object>
	{
		public String filename;
		public Map<String,Integer> freq = new HashMap<String,Integer>();
		public Iterator<List<String>> it;
			
		public count_and_sort(String filename)
		{
			this.filename = filename;
			TwentySeven ts = new TwentySeven();
			it = ts.new non_stop_words(filename);
		}
		
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public Object next() {

			int i = 1;
			while(it.hasNext())
			{
				//List<String> w = it.next();
				
				for(String w: it.next())
				{
				if(freq.containsKey(w) && !w.equals(filename))
				{
					freq.put(w, freq.get(w)+1);
				}
				else
				{
					freq.put(w, 1);
				}
				
				if(i%5000 == 0)
				{	
					return  freq	
							   .entrySet().stream()
						       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
						        .collect(Collectors.toCollection(ArrayList::new));
				}
				i = i + 1;
			}
			}
			return  freq	
					   .entrySet().stream()
				       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
				        .collect(Collectors.toCollection(ArrayList::new));			
		}
		
	}
	
	public static void main(String[] args) {

		TwentySeven ts = new TwentySeven();
		Iterator<Object> it = ts.new count_and_sort(args[0]);
		
		while(it.hasNext())
		{
			System.out.println("-----------------------------");
			@SuppressWarnings("unchecked")
			List<Map.Entry<String, Integer>> lst = (List<Map.Entry<String, Integer>>) it.next();
			for(Map.Entry<String, Integer> curr: lst.subList(0, 25))
			{
				System.out.println(curr.getKey() + "  -  " + curr.getValue());
			}
		}
		
	}

}
