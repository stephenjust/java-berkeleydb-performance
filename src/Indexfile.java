import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;


public class Indexfile extends BaseDb implements ISearch {

	public Database table;
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
			table = new Database(dbPath + File.pathSeparator + "table.db", null, dbConfig);
			index = new Database(dbPath + File.pathSeparator + "index.db", null, dbConfig);
		} catch (FileNotFoundException e) {
			System.err.println("Database file not found");
		} catch (DatabaseException e) {
			System.err.println("Failed to open database");
		}
	}
	
	@Override
	public void populateTable(int nrecs) {
	
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
