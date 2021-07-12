package five1;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Read_file {
	
	private String path_to_file;

	public Read_file(String path_to_file){
		this.path_to_file = path_to_file;
	}
	
	public String read_file() {
		
		String data = "";		
		try {
			data += new String(Files.readAllBytes(Paths.get(path_to_file)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return data;
	}
}
