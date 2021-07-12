package five1;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Print_all {
	
	private List<Entry<String, Integer>> word_freq;

	public Print_all(List<Map.Entry<String, Integer>> word_freq){
		this.word_freq = word_freq;
	}

	public void print_all(){
			
		if(word_freq.size() > 0) {
			for(Entry<String,Integer> current: word_freq)
			{
				System.out.println(current.getKey() + "  -  " + current.getValue());
			}
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//new Print_all(new Sort(new Frequencies(new Remove_stop_words(new Scan(
		//		new Filter_chars_and_normalize(new Read_file(args[0]).read_file()).filter_chars_and_normalize()).scan()
		//		).remove_stop_words()).frequencies().subList(0, 25)).sort()).print_all();;
		

	}
}
