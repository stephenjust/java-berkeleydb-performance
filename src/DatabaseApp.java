import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class DatabaseApp {
	Integer mode = 0;
	public static void main(String[] args) {
		DatabaseApp app = new DatabaseApp();
		try {
			app.setup(args);
			app.run();
		} finally {
			app.cleanup();
		}
	}
	
	public void setup(String[] args) {
		/** Select the appropriate mode based on commandline arguments
		 */
		
		if (args[0].equals("btree")) this.mode = 1;
		if (args[0].equals("hash")) this.mode = 2;
		if (args[0].equals("indexfile")) this.mode = 3;
		
	}
	public void run() {
		
		
	}
	public void cleanup() {
		
	}

}
