package ca.ualberta.cs.C291BerkeleyDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;


public class HashTableDb extends BaseDb {
	
	
	public HashTableDb(String dbPath) {
		super(dbPath);
		
		// database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();

		dbConfig.setErrorStream(System.err);
		dbConfig.setType(DatabaseType.HASH);
		dbConfig.setAllowCreate(true);

		// database
		try {
			db = new Database(dbPath + File.separator + "table.db", null, dbConfig);
		} catch (FileNotFoundException e) {
			System.err.println("Database file not found");
		} catch (DatabaseException e) {
			System.err.println("Failed to open database");
		}
	}

	@Override
	public void retrieveRange(String startKey, String endKey) {
		OperationStatus os = null;
		
		long sTime = System.nanoTime();
		int recordCount = 0;
		
		try {			
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();
			key.setData(startKey.getBytes("UTF-8"));
			key.setSize(startKey.length());
			CursorConfig cc = new CursorConfig();
			
			Cursor c = db.openCursor(null, cc);
			os = c.getFirst(key, data, LockMode.DEFAULT);
			if (os == OperationStatus.NOTFOUND) {
				System.err.println("Database is empty");
				return;
			} else if (os != OperationStatus.SUCCESS) {
				System.err.println("Lookup failed, aborting!");
				return;
			}
			if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) !=
		            OperationStatus.SUCCESS) {
				System.err.println("No results");
				return;
			}
			if (inRange(key, startKey, endKey)) {
				addRecordToAnswers(key, data);
				recordCount = 1;
			}
			
		    while (c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
		    	if (inRange(key, startKey, endKey)) {
		    		addRecordToAnswers(key, data);
		    		recordCount++;
		    	}
	        }
			c.close();
			System.out.println("Found " + recordCount + " record(s)");
			long eTime = System.nanoTime();
			System.out.println("Query took " + Math.round((eTime - sTime)/1000) + "us");
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Check if a database entry is between start and end values
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 */
	private boolean inRange(DatabaseEntry value, String start, String end) {
		try {
			return (compareByteArrays(value.getData(), start.getBytes("UTF-8")) >= 0 && compareByteArrays(value.getData(), end.getBytes("UTF-8")) <= 0);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Encoding error");
		}
		return false;
	}
	
	@Override
	public void cleanUp() {
		File dbFile = new File(dbPath + File.separator + "table.db");
		if (dbFile.exists()) dbFile.delete();
	}

}
