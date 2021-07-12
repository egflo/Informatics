package five1;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Print_all(new Sort(new Frequencies(new Remove_stop_words(new Scan(
				new Filter_chars_and_normalize(new Read_file(args[0]).read_file()).filter_chars_and_normalize()).scan()
					).remove_stop_words(args[1])).frequencies()).sort().subList(0, 25)).print_all();
	}

}
