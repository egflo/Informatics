package five1;

import java.util.regex.Pattern;

public class Filter_chars_and_normalize {
	
	private String str_data;

	public Filter_chars_and_normalize(String str_data) {
		this.str_data = str_data;
		
	}
	
	public String filter_chars_and_normalize(){
		
		Pattern pattern = Pattern.compile("[\\W]|_");
		
		return pattern.matcher(str_data).replaceAll(" ").toLowerCase();
		
	}

}
