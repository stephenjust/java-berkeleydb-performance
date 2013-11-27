
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class DbHelper {

	public static Database create(String dbPath, DatabaseType dbType) {
		// database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();

		// dbConfig.setErrorStream(System.err);
		// dbConfig.setErrorPrefix("MyDbs");

		dbConfig.setType(dbType);
		dbConfig.setAllowCreate(true);

		// database

		Database db;
		try {
			db = new Database(dbPath, null, dbConfig);

			return db;
		} catch (FileNotFoundException e) {
			System.err.println("Database file not found");
		} catch (DatabaseException e) {
			System.err.println("Failed to open database");
		}
		return null;
	}
	
	

	/*
	 *  To populate the given table with nrecs records
	 */
	static void populateTable(Database my_table, int nrecs ) {
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
				my_table.putNoOverwrite(null, kdbt, ddbt);
			}
		}
		catch (DatabaseException dbe) {
			System.err.println("Populate the table: "+dbe.toString());
			System.exit(1);
		}
	}
	
	public static void PopulateDBbyFile(Database db, String filename){
		BufferedReader br = null;
		int selector = 0;
		boolean terminator = false;
		String skey = "";
		String sdata = "";
		try {
			br = new BufferedReader(new FileReader(filename));	
			//use br.readline() to get a line.
			
			
			
			while(terminator != false) {
				switch (selector %3){
				case 0: 
					skey = br.readLine();
					break;
				case 1: 
					sdata = br.readLine();
					break;
				case 2:
					if (br.readLine() == null)
						terminator = true;
						break;
				}
				
				if (skey == null || sdata == null){
					terminator = true;
					break;
				}
				
				DatabaseEntry readKey = new DatabaseEntry(skey.getBytes());
				DatabaseEntry readData = new DatabaseEntry(sdata.getBytes());
				
				db.put(null, readData, readKey); //Entries reversed on purpose.
				
				
				selector++;
			}
		 
		 
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("Failed to close file");
				e.printStackTrace();
			}
		}
	}
	
	
	public static void getByKey(Database db, String skey) {
		// Search the DB by key.
		// In the answers file, must have following 3 line format:
		//		KeyString
		//		DataString
		//		EmptyString
		
		// Print to the screen:
		//		Number of records retrieved
		//		Execution time in Microseconds.
		
		// Assume that the class variable for db is called db.
		String KeyString = skey;
		String DataString;
		String EmptyString = "";
		
		
		FileOutputStream fos = null;
		long sTime = System.nanoTime();
		
		
		try {
			
			File fh = new File("answers");
			if (!fh.exists()) {
				fh.createNewFile();
			}
			if (!fh.canWrite()) {
				throw new IOException("File is not writeable!");
			}
			fos = new FileOutputStream(fh);

			
			OperationStatus oprStatus; 
			DatabaseEntry dbKey = new DatabaseEntry(skey.getBytes());
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
					fos.write(skey.getBytes()); //Search string
			    	fos.write((byte)'\n');
			    	fos.write(dbData.getData());
			    	fos.write((byte)'\n');
			    	fos.write((byte)'\n');
			 }	 
			 System.out.println("Found 1 record(s)");
			long eTime = System.nanoTime();
			System.out.println("Query took " + Math.round((eTime - sTime)/1000) + "us");
		} catch (DatabaseException e) {
			System.out.println("Database Exception in getByKey");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception. Something is wrong with file creation");
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
	
	public static void getByValueNoIndex(Database db, String value){
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
	
	public static void populateIndexFile(Database db){
		//Takes the database, reads all records into the "indexfile".
		long sTime = System.nanoTime();
		FileOutputStream fos = null;
		int recordcount = 0;
		try {
			File fh = new File("indexfile");
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
			//c.getFirst(new DatabaseEntry(), new DatabaseEntry(), LockMode.DEFAULT);
			if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) != //dont search by range
		            OperationStatus.SUCCESS) {
				System.err.println("No results");
				return;
			}
			
		    while (c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) { //getnextdup for speed!!
		    	// Switch around the entry/key compared to normal to create lookup file.
		    		fos.write(data.getData());
			    	fos.write((byte)'\n');
			    	fos.write(key.getData());
			    	fos.write((byte)'\n');
			    	fos.write((byte)'\n');
		            recordcount++;
		    	
		    		
		    	
	        }
			c.close();
			System.out.println("Wrote " + recordcount + " record(s)");
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


	public static List<String> retrieveRange(Database db, String startKey, String endKey) {
		List<String> list = new ArrayList<String>();
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
			c.getFirst(new DatabaseEntry(), new DatabaseEntry(), LockMode.DEFAULT);
			if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) !=
		            OperationStatus.SUCCESS) {
				System.err.println("No results");
				return null;
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
		
		
		return list;
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
}
