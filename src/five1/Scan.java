package five1;

public class Scan {
	
	private String str_data;

	public Scan(String str_data){
		this.str_data = str_data;
	}
	
	public String[] scan() {
		
		return str_data.split("\\s+");	
	}
		
}

