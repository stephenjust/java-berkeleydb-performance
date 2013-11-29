import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;


public class Indexfile extends BaseDb {

	public Database index;
	
	
	public Indexfile(String dbPath) {
		super(dbPath);
		// database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();

		dbConfig.setErrorStream(System.err);
		dbConfig.setType(DatabaseType.BTREE);
		dbConfig.setAllowCreate(true);

		// database
		try {
			db = new Database(dbPath + File.separator + "table.db", null, dbConfig);
			index = new Database(dbPath + File.separator + "index.db", null, dbConfig);
		} catch (FileNotFoundException e) {
			System.err.println("Database file not found");
		} catch (DatabaseException e) {
			System.err.println("Failed to open database");
		}
	}
	
	@Override
	public void populateTable(int nrecs) {
		int range;
		DatabaseEntry kdbt, ddbt;
		String s;
		/*  
		 *  generate a random string with the length between 64 and 127,
		 *  inclusive.
		 *
		 *  Seed the random number once and once only.
		 */
		Random random = new Random(1000000);

		try {
			for (int i = 0; i < nrecs; i++) {

				/* to generate a key string */
				range = 64 + random.nextInt( 64 );
				s = "";
				for ( int j = 0; j < range; j++ ) 
					s+=(new Character((char)(97+random.nextInt(26)))).toString();

				/* to create a DBT for key */
				kdbt = new DatabaseEntry(s.getBytes());
				kdbt.setSize(s.length()); 

				// to print out the key/data pair
				// System.out.println(s);	

				/* to generate a data string */
				range = 64 + random.nextInt( 64 );
				s = "";
				for ( int j = 0; j < range; j++ ) 
					s+=(new Character((char)(97+random.nextInt(26)))).toString();
				// to print out the key/data pair
				// System.out.println(s);	
				// System.out.println("");

				/* to create a DBT for data */
				ddbt = new DatabaseEntry(s.getBytes());
				ddbt.setSize(s.length()); 

				/* to insert the key/data pair into the database */
				if (db.putNoOverwrite(null, kdbt, ddbt) != OperationStatus.KEYEXIST) {
					index.put(null, ddbt, kdbt);
				}
				
			}
		}
		catch (DatabaseException dbe) {
			System.err.println("Populate the table: "+dbe.toString());
			System.exit(1);
		}
	}



	@Override
	public void getByValue(String value) {
		// Search the DB by key.
		// Search the DB by key.
				// In the answers file, must have following 3 line format:
				//		KeyString
				//		DataString
				//		EmptyString
				
				// Print to the screen:
				//		Number of records retrieved
				//		Execution time in Microseconds.
				
				//This is like a reverse-hashtable lookup. 
				//Make a cursor, go through the db.
				
				long sTime = System.nanoTime();
				int recordCount = 0;
				
				try {
					
					DatabaseEntry key = new DatabaseEntry();
					key.setData(value.getBytes());
					key.setSize(value.length());
					DatabaseEntry data = new DatabaseEntry();
					
					Cursor c = index.openCursor(null, null);
					if (c.getSearchKey(key, data, LockMode.DEFAULT) != //demove this block breaks hashtable
				            OperationStatus.SUCCESS) {
						System.err.println("No results");
						return;
					}
					recordCount = 1;
		    		addRecordToAnswers(key, data);
				    while (c.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				    	    addRecordToAnswers(data, key);
				            recordCount++;
				    		
				    	
			        }
					c.close();
					System.out.println("Found " + recordCount + " record(s)");
					long eTime = System.nanoTime();
					System.out.println("Query took " + Math.round((eTime - sTime)/1000) + "us");
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	@Override
	public void cleanUp() {
		File dbFile = new File(dbPath + File.separator + "table.db");
		File indexFile = new File(dbPath + File.separator + "index.db");
		if (dbFile.exists()) dbFile.delete();
		if (indexFile.exists()) dbFile.delete();
	}
	
	
	
	
	
	

}
