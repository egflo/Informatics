package week5;

import java.util.List;
import java.util.Map;

public class print3 {
	public void print(List<Map.Entry<String, Integer>> word_freqs)
	{
		for(int i = 0; i < word_freqs.size(); i++)
		{
			System.out.println(word_freqs.get(i).getKey() + "  -  " + word_freqs.get(i).getValue());
		}
	}
}
