package week9;


import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ThirtyThree {

	public static Map<String,Object> data = new HashMap<String,Object>();
	public static Map<String,Method> handlers = new HashMap<String,Method>();
	public static List<String> stops;
	public static Scanner sys = new Scanner(System.in);
	
	
	public static Object error_state()
	{
		
		List<String> items = new ArrayList<String>();
		items.add("get");
		items.add("default");
		items.add(null);
		
		List<Object> ret = new ArrayList<Object>();
		ret.add("Something wrong");
		ret.add(items);
		
		return ret;
	}
	
	public static Object default_get_handler(Object args)
	{
		String rep = "What would you like to do?";
		rep += "\n1 - Quit" + "\n2 - Upload file";
		Map<String,List<String>> links = new HashMap<String,List<String>>();
		
		List<String> items = new ArrayList<String>();
		items.add("post");
		items.add("execution");
		items.add(null);
		
		links.put("1", items);
		
		List<String> items2 = new ArrayList<String>();
		items2.add("get");
		items2.add("file_form");
		items2.add(null);
		
		links.put("2", items2);
		
		List<Object> ret = new ArrayList<Object>();
		ret.add(rep);
		ret.add(links);
		
		return ret;
	}
	
	public static void quit_handler(Object args)
	{
		System.out.println("Goodbye cruel world....");
		System.exit(0);
	}
	
	public static Object upload_get_handler(Object args)
	{
		
		List<Object> ret = new ArrayList<Object>();
		ret.add("Name of file to upload?");
		List<String> items = new ArrayList<String>();
		items.add("post");
		items.add("file");
		ret.add(items);
		
		return ret;
	}
	
	public static Object upload_post_handler(Object args) 
	{
		class Create_data
		{
			public void create_data(String filename) throws Exception
			{
				if(data.containsKey(filename))
				{
					return;
				}
					
				Map<String,Integer> word_freqs = new HashMap<String,Integer>();
				Scanner sc = new Scanner(new File(filename));
				while(sc.hasNextLine())
				{
					String line = sc.nextLine();
					for(String w: line.toLowerCase().split("[^a-zA-Z]+"))
					{
						if(w.length() > 0 && !stops.contains(w))
						{
							if(word_freqs.containsKey(w))
							{
								word_freqs.put(w, word_freqs.get(w) + 1);
							}
								
							else
							{
								word_freqs.put(w, 1);
							}						
						}
					}
				}
					
				List<Map.Entry<String, Integer>> word_freqs1 = 
						word_freqs	
							 .entrySet().stream()
						      .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
						       .collect(Collectors.toCollection(ArrayList::new));	
					
				data.put(filename,word_freqs1);
				sc.close();
			}
				
		}	
		
		if(args == null)
		{
			return error_state();
		}
		
		@SuppressWarnings("unchecked")
		List<String> args1 = (List<String>) args;
		String filename = args1.get(0);
		
		try
		{
			new Create_data().create_data(filename);
		
		}
		
		catch(Exception e)
		{
		
			e.printStackTrace();
			return error_state();
		}
		
		List<Object> ret = new ArrayList<Object>();
		ret.add(filename);
		ret.add(0);

		return word_get_handler(ret);
	}
	
	public static Object word_get_handler(Object args) {
		
		
		class Get_word
		{
			@SuppressWarnings("unchecked")
			public Entry<String, Integer> get_word(String filename, int word_index)
			{
				List<Map.Entry<String, Integer>> data_ = 
						(List<Map.Entry<String, Integer>>) data.get(filename);
				
				if(word_index < data_.size() && word_index > -1)
				{
					return data_.get(word_index);
				
				}
				else
				{
					Map.Entry<String, Integer> entry = 
							new AbstractMap.SimpleEntry<String, Integer>("No more Words", 0);
				
					return entry;
				}
			}
		}
		
		
		@SuppressWarnings("unchecked")
		List<Object> args_ = (List<Object>)args;
		
		String filename = (String) args_.get(0);
		int word_index = (int) args_.get(1);

		Get_word gw = new Get_word();
		
		Entry<String, Integer> word_info = gw.get_word(filename, word_index);
		
		String rep = 
				String.format("\n#%d: %s  -  %d", word_index+1, word_info.getKey(),word_info.getValue());
		rep += "\n\n What would you like to do next?";
		rep += "\n1 - Quit" + "\n2 - Upload file";
		rep += "\n3 - See next most-frequently occuring word";
		rep += "\n4 - See previous most-frequently occuring word";
		
		Map<String,Object> links = new HashMap<String,Object>();
		
		List<String> item1 = new ArrayList<String>();
		item1.add("post");
		item1.add("execution");
		item1.add(null);
		
		List<String> item2 = new ArrayList<String>();
		item2.add("get");
		item2.add("file_form");
		item2.add(null);
		
		List<Object> item3 =  new ArrayList<Object>();
		item3.add("get");
		item3.add("word");
		
		List<Object> word_list = new ArrayList<Object>();
		word_list.add(filename);
		word_list.add(word_index + 1);
		
		item3.add(word_list);
		
		List<Object> item4 =  new ArrayList<Object>();
		item4.add("get");
		item4.add("word");
		
		List<Object> word_list2 = new ArrayList<Object>();
		word_list2.add(filename);
		word_list2.add(word_index - 1);
		
		item4.add(word_list2);
		
		links.put("1", item1);
		links.put("2", item2);
		links.put("3", item3);
		links.put("4", item4);
		
		List<Object> ret = new ArrayList<Object>();
		ret.add(rep);
		ret.add(links);
		
		return ret;
	}
	
	
	
	// Server Core
	public Object handle_request(String verb, String uri, Object args) throws Exception
	{
		class Handler_key
		{
			public String handler_key(String verb, String uri)
			{
				return verb + "_" + uri;
			}
		}
		
		Handler_key h_k = new Handler_key();
		
		if(handlers.containsKey(h_k.handler_key(verb, uri)))
		{
			return handlers.get(h_k.handler_key(verb, uri)).invoke(null, args);
		}
		
		else
		{
			return handlers.get(h_k.handler_key("get","default")).invoke(null, args);
		}
	}
	
	

	@SuppressWarnings("unchecked")
	public Object render_and_get_input(Object state_representation, Object links)
	{
		System.out.println(state_representation);
		
		if(links instanceof Map)
		{
			Map<String,Object> _links = (Map<String,Object>) links;
			String input = sys.nextLine().trim();
			if(_links.containsKey(input))
			{
				return _links.get(input);
			}
			
			else
			{
				List<String> items = new ArrayList<String>();
				items.add("get");
				items.add("default");
				items.add(null);
				
				return items;
			}
		}
		
		else if(links instanceof List)
		{
			List<Object> _links = (List<Object>) links;
			String first_item = (String)_links.get(0);
			
			if(first_item.compareToIgnoreCase("post") == 0)
			{
				String input = sys.nextLine().trim();
				List<Object> ret = new ArrayList<Object>();
				ret.add(input);
				_links.add(ret);
				
				return _links;
			}
			else //# get action, don’t get user input
			{
				return links;
			}
		}
		
		else
		{
			List<String> items = new ArrayList<String>();
			items.add("get");
			items.add("default");
			items.add(null);
			
			return items;
		}		
	}

	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		try
		{
			String[] stopwords = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
			List<String> stop_words = 
					new ArrayList<String>(Arrays.asList(stopwords));
			
			stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));
		
			ThirtyThree tt = new ThirtyThree();
			ThirtyThree.stops = stop_words;
			
			//Handlers
	        Class[] cArg = new Class[1];
	        cArg[0] = Object.class;
			
	        ThirtyThree.handlers.put("post_execution", ThirtyThree.class.getMethod("quit_handler",cArg));
			ThirtyThree.handlers.put("get_default", ThirtyThree.class.getMethod("default_get_handler",cArg));
			ThirtyThree.handlers.put("get_file_form", ThirtyThree.class.getMethod("upload_get_handler",cArg));
			ThirtyThree.handlers.put("post_file", ThirtyThree.class.getMethod("upload_post_handler",cArg));
			ThirtyThree.handlers.put("get_word", ThirtyThree.class.getMethod("word_get_handler",cArg));
			
			
			List<Object> request = new ArrayList<Object>();
			
			request.add("get");
			request.add("default");
			request.add(null);
			
			while(true)
			{
				// Server-side computation
				List<Object> rep 
					= (List<Object>) tt.handle_request((String)request.get(0),(String)request.get(1), request.get(2));
				
				//client-side computatation
				request = (List<Object>) tt.render_and_get_input(rep.get(0), rep.get(1));
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		
		}

	}

}



