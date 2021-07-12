package week6;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TwentyFour {
	
	interface f {
		public Object _f();
	}
	
	interface Func {
		Object run(Object v);
	}
	
	
	class TFQuarantine {
			public List<Object> _Funcs;
			
			public TFQuarantine(Object Func)
			{
				this._Funcs =  new ArrayList<Object>();
				this._Funcs.add(Func);
			}
		
			public TFQuarantine bind(Object Func)
			{
				this._Funcs.add(Func);
				return this;
			}
		
			public void execute()
			{
				class Guard_callable
				{
					Object guard_callable(Object v)
					{
						if(v instanceof f)
						{
							f func = (f)v;
							return func._f();
						}
						
						else
						{
							return v;
						}
					}
				}
									
				Object value = null;
				Guard_callable g = new Guard_callable();
				
				for(Object obj: this._Funcs)
				{				
					Func func = (Func) obj;
					value = func.run(g.guard_callable(value));
				}
				
				System.out.print((String)g.guard_callable(value));
			}
		
		}
	
		public class Get_input implements Func {
			private String[] args;
			
			public Get_input(String[] args)
			{
				this.args = args;
			}
			
			public f run(Object arg)
			{
				f _f = new f() {
					public Object _f()
					{
						return args[0];
					}
				};
				
				return _f;
			}		
		}

		
		public class Extract_words implements Func {
			
			public f run(Object path_file)
			{
				String path_to_file = (String) path_file;
				f _f = new f() {
					public Object _f()
					{
						String data = "";
						List<String> word_list = null;
						
						try {
							data += new String(Files.readAllBytes(Paths.get(path_to_file)));
							Pattern pattern = Pattern.compile("[\\W]|_");							
							word_list = 
									new ArrayList<String>(Arrays.asList(pattern.matcher((String)data).replaceAll(" ").toLowerCase().split("\\s+")));	
						} 
						
						catch (Exception e) {
								e.printStackTrace();
						}
							
						return word_list;
					}
				};				
				return _f;			
			}		
		}
		
		
		public class Remove_stop_words implements Func{
			
			@SuppressWarnings("unchecked")
			public f run(Object wordlist)
			{		
				List<String> word_list = (List<String>) wordlist;
				f _f = new f() {
					public Object _f()
					{
						try {						
							String[] stopwords = new String(Files.readAllBytes(Paths.get("stopwords.txt"))).split(",");
							List<String> stop_words = 
									new ArrayList<String>(Arrays.asList(stopwords));
							
							stop_words.addAll(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));
							
							word_list.removeIf(p -> stop_words.contains(p));
							
						} catch (Exception e) {
							e.printStackTrace();
						}	
						
						return word_list;
					}
				};
				
				return _f;			
			}
					
		}
		
		public class Frequencies implements Func {
			
			@SuppressWarnings("unchecked")
			public Object run(Object wordlist)
			{
				List<String> word_list = (ArrayList<String>) wordlist;
				Map<String,Integer> word_freqs = new HashMap<String,Integer>();
				
				for(String word: word_list)
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
		
		
		public class Sort implements Func {
			
			@SuppressWarnings("unchecked")
			public Object run(Object word_freqs)
			{		
				return ((Map<String, Integer>) word_freqs)	
						   .entrySet().stream()
					       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
					        .collect(Collectors.toCollection(ArrayList::new));							
			}
		
		}
		
		//Keep One commented
		//This is for 24.3

		public class Top25_freqs implements Func {
			
			public f run(Object wordfreqs)
			{		
				@SuppressWarnings("unchecked")
				List<Map.Entry<String, Integer>> word_freqs =
						(List<Map.Entry<String, Integer>>) wordfreqs;
				
				f _f = new f() {
					public Object _f()
					{
						for(Entry<String, Integer> word: word_freqs.subList(0, 25))
						{
							System.out.println(word.getKey() + "  -  " + word.getValue());
						}	
						
						return "";
					}
				};
				
				return _f;			
			}
			
			
		}

		
		/**
		public class Top25_freqs implements Func {
			@SuppressWarnings("unchecked")
			public Object run(Object wordfreqs)
			{
				List<Map.Entry<String, Integer>> word_freqs =
						(List<Map.Entry<String, Integer>>) wordfreqs;
				
				String top25 = "";
				for(Entry<String, Integer> word: word_freqs.subList(0, 25))
				{
					top25 += word.getKey() + "  -  " + word.getValue() + "\n";
				}
				
				return top25;
			}
					
		}

	**/
		
	public static void main(String[] args) {
		
		TwentyFour tw = new TwentyFour();
	
		Get_input get_input = tw. new Get_input(args);
		Extract_words extract_words = tw. new Extract_words();
		Remove_stop_words remove_stop_words = tw. new Remove_stop_words();
		Frequencies frequencies = tw. new Frequencies();
		Sort sort = tw. new Sort();
		Top25_freqs top25_freqs = tw. new Top25_freqs();
		
		TFQuarantine quarantine
			= tw.new TFQuarantine(get_input).bind(extract_words).bind(remove_stop_words)
			.bind(frequencies).bind(sort).bind(top25_freqs);
		
		System.out.println("Question: 24.2 " + quarantine._Funcs);
		System.out.println("Since this is just a list of class objects in a list");
		System.out.println("nothing is executed until the program calls run()");
		System.out.println("which is the method within each class.");
		System.out.println("");
		
		quarantine.execute();
	}

}
