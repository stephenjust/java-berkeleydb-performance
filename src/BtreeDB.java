import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;


public class BtreeDB extends BaseDb implements ISearch {

	public BtreeDB(String dbPath) {
		super(dbPath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getByValue(String value) {
	
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
			
			Cursor c = db.openCursor(null, null);
			c.getFirst(new DatabaseEntry(), new DatabaseEntry(), LockMode.DEFAULT);
			if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) != //demove this block breaks hashtable
		            OperationStatus.SUCCESS) {
				System.err.println("No results");
				return;
			}
			recordCount = 1;
		    while (c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
		    	// If the current entry has the value we want, write it to file.
		    	if (compareByteArrays(key.getData(), value.getBytes("UTF-8")) == 0) {
		    		fos.write(key.getData());
			    	fos.write((byte)'\n');
			    	fos.write(data.getData());
			    	fos.write((byte)'\n');
			    	fos.write((byte)'\n');
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

	
	



}
