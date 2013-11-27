
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class DbHelper {
	
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
}
