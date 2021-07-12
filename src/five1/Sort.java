package five1;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Sort {
	
	private List<Entry<String, Integer>> word_freq;

	public Sort(List<Map.Entry<String, Integer>> word_freq){
		this.word_freq = word_freq;
	}
	
	public List<Map.Entry<String, Integer>> sort(){
		
		Collections.sort(word_freq, new Comparator<Map.Entry<String, Integer>>() {
			  @Override
			  public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
			    return Integer.compare(o2.getValue(),o1.getValue());
			  }
			});
		return word_freq;
	}
}
