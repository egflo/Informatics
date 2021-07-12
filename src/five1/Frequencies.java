package five1;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frequencies {
	
	private List<String> word_list;

	public Frequencies(List<String> word_list){
		this.word_list = word_list;
	}
	
	public List<Map.Entry<String, Integer>> frequencies() {
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();	
		for(String word: word_list) {	
			if(!map.containsKey(word)) {		
				map.put(word, 1);
			}		
			else{
				Integer frequency = map.get(word);
				map.put(word, frequency+1);
			}
		}			
		List<Map.Entry<String,Integer>> list = 
				new ArrayList<Map.Entry<String,Integer>>();	
		
		for(String word: map.keySet()){
			Integer frequency = map.get(word);
			list.add(new AbstractMap.SimpleEntry<String,Integer>(word, frequency));
		}
		
		return list;		
	}

}
