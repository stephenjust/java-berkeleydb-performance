package ca.ualberta.cs.C291BerkeleyDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import ca.ualberta.cs.C291BerkeleyDB.db.BaseDb;
import ca.ualberta.cs.C291BerkeleyDB.db.BtreeDB;
import ca.ualberta.cs.C291BerkeleyDB.db.HashTableDb;
import ca.ualberta.cs.C291BerkeleyDB.db.Indexfile;
import ca.ualberta.cs.C291BerkeleyDB.io.ChoicesMenu;

public class DatabaseApp {
	private String tmpDir;
	BaseDb db;
	ChoicesMenu menu;
	
	public static void main(String[] args) {
		DatabaseApp app = new DatabaseApp();
		try {
			app.setup(args);
			app.run();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			app.cleanup();
		}
	}
	
	/**
	 * Set up application in current run-mode
	 * @param args
	 * @throws FileSystemException
	 */
	public void setup(String[] args) throws Exception {
		if (System.getProperty("os.name").startsWith("Windows")) {
			tmpDir = "C:\\tmp\\smartynk_dir";
		} else {
			tmpDir = "/tmp/sajust_dir";
		}
		
		// Create temporary directory
		File tDirFile = new File(tmpDir);
		if (tDirFile.exists()) tDirFile.delete();
		
		if (!tDirFile.exists() && !(new File(tmpDir)).mkdirs()) {
			throw new Exception("Failed to create temp folder");
		}
		
		// Select the appropriate mode based on commandline arguments
		try{
			if (args[0].equals("btree")) db = new BtreeDB(tmpDir);
			else if (args[0].equals("hash")) db = new HashTableDb(tmpDir);
			else if (args[0].equals("indexfile")) db = new Indexfile(tmpDir);
			else throw new IllegalArgumentException("Invalid mode");
		} catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("Please enter in a commandline argument.");
			System.err.println("Acceptable options are: btree, hash, indexfile");
			System.exit(1);
		} catch(IllegalArgumentException e) {
			System.err.println("Please enter in a commandline argument.");
			System.err.println("Acceptable options are: btree, hash, indexfile");
			System.exit(1);
			// In Java 7 we could have caught both of those nicely in one catch clause...
		}
		
		setupMenu();
	}
	
	private void setupMenu() {
		menu = new ChoicesMenu();
		menu.setTitle("Berkeley DB Performance Demo");
		menu.addChoice(1, "Create and populate the database");
		menu.addChoice(2, "Retrieve records with a given key");
		menu.addChoice(3, "Retrieve records with a given data");
		menu.addChoice(4, "Retrieve records with a given range of key values");
		menu.addChoice(5, "Destroy the database");
		menu.addChoice(6, "Quit");
	}
	
	/**
	 * Run application
	 */
	public void run() {		
		/** Display the main menu, prompt the user for which db type is being used
		 * 
		 */
		while (true) {
			int choice = menu.prompt();
			
			switch(choice) {
			case 1: 
				db.populateTable(100000);
				break;
			case 2: 
				System.out.println("Enter Search Key");
				String searchkey;
				searchkey = inputKey();
				db.getByKey(searchkey);
				break;
			case 3:
				System.out.println("Enter search value");
				db.getByValue(inputKey());
				break;
			case 4:
				System.out.print("Start of range?: ");
				String startKey = inputKey();
				System.out.print("End of range?: ");
				String endKey = inputKey();
				db.retrieveRange(startKey, endKey);
				break;
			case 5:
				db.cleanUp();
				System.out.println("Deleted database file");
				break;
			case 6:
				System.exit(0);
				break;
			}
			
		}
		
		
	}
	
	public void cleanup() {
		if (db != null) {
			db.cleanUp();
		}
		File tDirFile = new File(tmpDir);
		if (tDirFile.exists()){
			tDirFile.delete();
		}
	}
	
	/**
	 * Prompt for key
	 * @return Key string
	 */
	private String inputKey() {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(System.in));
		
		String key;
		while(true) {
			try {
				key = br.readLine();

				// Return value from input
//				if (key.length() < 64) {
//					System.err.println("Key must be at least 64 characters long");
//					continue;
//				}
				if (key.length() > 127) {
					System.err.println("Key must be shorter than 128 characters");
					continue;
				}
				return key;
			} catch (IOException e) {
				System.err.println("Error retrieving value");
			}
		}
	}

}
