package week5;

import java.util.List;
import java.util.Map;

public class print2 {
	public void print(List<Map.Entry<String, Integer>> word_freqs)
	{
		word_freqs.forEach(System.out::println);
	}
}
