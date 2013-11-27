import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
		FileOutputStream fos = null;
		
		long sTime = System.nanoTime();
		int recordCount = 0;
		
		try {
			File fh = new File("answers");
			if (!fh.exists()) {
				fh.createNewFile();
			}
			if (!fh.canWrite()) {
				throw new IOException("File is not writeable!");
			}
			fos = new FileOutputStream(fh);
			
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();
			key.setData(startKey.getBytes("UTF-8"));
			key.setSize(startKey.length());
			CursorConfig cc = new CursorConfig();
			
			Cursor c = db.openCursor(null, cc);
			if (c.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.NOTFOUND) {
				System.err.println("Table empty!");
			}
			if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) !=
		            OperationStatus.SUCCESS) {
				System.err.println("No results");
				return;
			}
			recordCount = 1;
		    while (c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
		    	// Stop at end of range
		    	if (compareByteArrays(key.getData(), endKey.getBytes("UTF-8")) > 0)
		    		break;
		    	fos.write(key.getData());
		    	fos.write((byte)'\n');
		    	fos.write(data.getData());
		    	fos.write((byte)'\n');
		    	fos.write((byte)'\n');
	            recordCount++;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void cleanUp() {
		File dbFile = new File(dbPath + File.separator + "table.db");
		if (dbFile.exists()) dbFile.delete();
	}

}
