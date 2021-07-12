package week5;

import java.util.List;
import java.util.Map;

interface TFPrint{
	public void print(List<Map.Entry<String, Integer>> word_freqs);
}

public class print1 implements TFPrint{

	public void print(List<Map.Entry<String, Integer>> word_freqs)
	{
		for(Map.Entry<String, Integer> current: word_freqs)
		{
			System.out.println(current.getKey() + "  -  " + current.getValue());
		}		
	}
}
