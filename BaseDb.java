import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;


public abstract class BaseDb {
	protected Database db;
	protected String dbPath;

	public BaseDb(String dbPath) {
		this.dbPath = dbPath;
		cleanUp();
	}

	public abstract void cleanUp();

	/*
	 *  To populate the given table with nrecs records
	 */
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
			}
		}
		catch (DatabaseException dbe) {
			System.err.println("Populate the table: "+dbe.toString());
			System.exit(1);
		}
	}

	public void getByKey(String skey) {
		// Search the DB by key.
		// In the answers file, must have following 3 line format:
		//		KeyString
		//		DataString
		//		EmptyString

		// Print to the screen:
		//		Number of records retrieved
		//		Execution time in Microseconds.

		long sTime = System.nanoTime();

		try {
			OperationStatus oprStatus; 
			DatabaseEntry dbKey = new DatabaseEntry(skey.getBytes());
			dbKey.setSize(skey.length());
			DatabaseEntry dbData = new DatabaseEntry(); //db data is the one result in place modified returned.
			oprStatus = db.get(null, dbKey , dbData, LockMode.DEFAULT );


			if (oprStatus == OperationStatus.KEYEMPTY) {
				System.out.println("Key was empty");
				return;
			} else if (oprStatus == OperationStatus.NOTFOUND) {
				System.out.println("No data found");					
				return;
			} else if (oprStatus != OperationStatus.SUCCESS) {
				System.out.println("General failure to succeed");				
				return;
			} else {
				//Success
				addRecordToAnswers(dbKey, dbData);
			}	 
			System.out.println("Found 1 record(s)");
			long eTime = System.nanoTime();
			System.out.println("Query took " + Math.round((eTime - sTime)/1000) + "us");
		} catch (DatabaseException e) {
			System.out.println("Database Exception in getByKey");
			e.printStackTrace();
		}
	}

	/**
	 * Compare byte arrays in lexicographical order, from Apache Hbase
	 * @param left
	 * @param right
	 * @return
	 */
	public static int compareByteArrays(byte[] left, byte[] right) {
		for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
			int a = (left[i] & 0xff);
			int b = (right[j] & 0xff);
			if (a != b) {
				return a - b;
			}
		}
		return left.length - right.length;
	}

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

		long sTime = System.nanoTime();
		int recordCount = 0;

		try {

			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();

			Cursor c = db.openCursor(null, null);
			c.getFirst(key, data, LockMode.DEFAULT);
			if (compareByteArrays(Arrays.copyOf(data.getData(), data.getSize()), value.getBytes()) == 0) {
			    addRecordToAnswers(key,data);
			    recordCount++;
			}
			while (c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				// If the current entry has the value we want, write it to file.
			    if (compareByteArrays(Arrays.copyOf(data.getData(),data.getSize()), value.getBytes("UTF-8")) == 0) {
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
			fos = new FileOutputStream(fh, true);

			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();
			key.setData(startKey.getBytes("UTF-8"));
			key.setSize(startKey.length());
			CursorConfig cc = new CursorConfig();

			Cursor c = db.openCursor(null, cc);
			c.getFirst(new DatabaseEntry(), new DatabaseEntry(), LockMode.DEFAULT);
			if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) !=
					OperationStatus.SUCCESS) {
				System.err.println("No results");
				return;
			}
			addRecordToAnswers(key, data);
			recordCount = 1;
			while (c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				// Stop at end of range
				if (compareByteArrays(key.getData(), endKey.getBytes("UTF-8")) > 0)
					break;
				addRecordToAnswers(key, data);
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
	
	protected void addRecordToAnswers(DatabaseEntry key, DatabaseEntry value) {
		FileOutputStream fos = null;
		File fh = new File("answers");
		try {
			if (!fh.exists()) {
				fh.createNewFile();
			}
			if (!fh.canWrite()) {
				throw new IOException("File is not writeable!");
			}
			fos = new FileOutputStream(fh, true);
			
			fos.write(new String(key.getData()).substring(0, key.getSize()).getBytes());
			fos.write((byte)'\n');
			fos.write(new String(value.getData()).substring(0, value.getSize()).getBytes());
			fos.write((byte)'\n');
			fos.write((byte)'\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
