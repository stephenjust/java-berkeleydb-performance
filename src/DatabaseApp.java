
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystemException;


public class DatabaseApp {
	private String tmpDir;
	Integer mode = 0;
	public static void main(String[] args) {
		DatabaseApp app = new DatabaseApp();
		try {
			app.setup(args);
			app.run();
		} catch (FileSystemException e) {
			System.err.println(e.getMessage());
		} finally {
			app.cleanup();
		}
	}
	
	public void setup(String[] args) throws FileSystemException {
		if (System.getProperty("os.name").startsWith("Windows")) {
			tmpDir = "C:\\tmp\\sajust_db";
		} else {
			tmpDir = "/tmp/sajust_dir";
		}
		
		/** Select the appropriate mode based on commandline arguments
		 */
		try{
		if (args[0].equals("btree")) this.mode = 1;
		if (args[0].equals("hash")) this.mode = 2;
		if (args[0].equals("indexfile")) this.mode = 3;
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Please enter in a commandline argument.");
			System.out.println("Acceptable options are: btree, hash, indexfile");
		}
		File tDirFile = new File(tmpDir);
		if (tDirFile.exists()) tDirFile.delete();
		
		if (!(new File(tmpDir)).mkdirs()) {
			throw new FileSystemException("Failed to create temp folder");
		}
		
		

	}
	
	public void run() {
		
		/** Display the main menu, prompt the user for which db type is being used
		 * 
		 */
		while (true) {
			System.out.println("CMPUT 291 Project 2");
			System.out.println("-------------------");
			System.out.println("Select Option");
			System.out.println("1) Create and populate the database");
			System.out.println("2) Retrieve records with a given key");
			System.out.println("3) Retrieve records with a given data");
			System.out.println("4) Retrieve records with a given range of key values");
			System.out.println("5) Destroy the database");
			System.out.println("6) Quit");
			
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
			case 4: break;
			case 5: break;
			case 6: break;
			}
			
		}
		
		
	}
	
	public void cleanup() {
		File tDirFile = new File(tmpDir);
		if (tDirFile.exists()) tDirFile.delete();
	}

}
