package week5;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Nineteen {
	public TFWords tfwords;
	public TFFreqs tffreqs;
	
	interface TFWords {
		 public List<String> extractWords(String path);
		}
	
	interface TFFreqs {
		 public List<Map.Entry<String, Integer>> top25(List<String> words);
	}
	
	public void load_plugins() throws Exception
	{
		
		Properties config = new Properties();
		FileInputStream in = new FileInputStream("config.properties");
		config.load(in);
		in.close();
		
		String words_plugin = config.getProperty("words");
		String frequencies_plugin = config.getProperty("frequencies");
		
		tfwords = (TFWords) Class.forName(words_plugin).newInstance();
		tffreqs = (TFFreqs) Class.forName(frequencies_plugin).newInstance();

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			Nineteen n = new Nineteen();
			n.load_plugins();
			
			List<Map.Entry<String, Integer>> word_freqs =
					n.tffreqs.top25(n.tfwords.extractWords(args[0]));
			

			for(Map.Entry<String, Integer> current: word_freqs)
			{
				System.out.println(current.getKey() + "  -  " + current.getValue());
			}		
					
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		
	}

}
