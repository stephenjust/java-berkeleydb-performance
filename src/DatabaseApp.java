import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class DatabaseApp {

	public static void main(String[] args) {
		DatabaseApp app = new DatabaseApp();
		try {
			app.setup();
			app.run();
		} finally {
			app.cleanup();
		}
	}
	
	public void setup() {
		
	}
	public void run() {
		/** Display the main menu, prompt the user for which db type is being used
		 * 
		 */
		while (true) {
			System.out.println("CMPUT 291 Project 2");
			System.out.println("-------------------");
			System.out.println("Select Test Option");
			System.out.println("1) btree");
			System.out.println("2) hash");
			System.out.println("3) indexfile");
			System.out.println("0) Exit");
			
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(System.in));
			Integer inputnumber = 0;
			try {
				String input = br.readLine();
				inputnumber = Integer.parseInt(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("Invalid Entry, please try again");
				continue;
			}
			
			switch(inputnumber) {
			case 1: break;
			case 2: break;
			case 3: break;
			}
			
		}
		
	}
	public void cleanup() {
		
	}

}
