import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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


public class Indexfile extends BaseDb implements ISearch {

	public Database db;
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
			db = new Database(dbPath + File.pathSeparator + "table.db", null, dbConfig);
			index = new Database(dbPath + File.pathSeparator + "index.db", null, dbConfig);
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
				db.putNoOverwrite(null, kdbt, ddbt);
				index.putNoOverwrite(null, ddbt, kdbt);
				
			}
		}
		catch (DatabaseException dbe) {
			System.err.println("Populate the table: "+dbe.toString());
			System.exit(1);
		}
	}

	@Override
	public void getByKey(String skey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getByValue(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveRange(String startKey, String endKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	

}
