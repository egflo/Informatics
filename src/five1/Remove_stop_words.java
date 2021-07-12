package five1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Remove_stop_words {

	private String[] word_list;

	public Remove_stop_words(String[] word_list){
		this.word_list = word_list;
	}
	
	public List<String> remove_stop_words(String stop_word_path){
		
		List<String> words = new ArrayList<String>();	
		Set<String> stop_words;	
		try {
			Scanner input = new Scanner(new File(stop_word_path));
			stop_words = new HashSet<String>(Arrays.asList(input.nextLine().toLowerCase().split(",")));
			input.close();
			
			Set<String> ascii = new HashSet<String>(Arrays.asList("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")));  
			stop_words.addAll(ascii);
		  
			for(String word: word_list){
			  if(!stop_words.contains(word)) {
				 
				  words.add(word);
			  }
		  }			
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return words;
	}
}
