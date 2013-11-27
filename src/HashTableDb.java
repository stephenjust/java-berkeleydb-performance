import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;


public class HashTableDb extends BaseDb implements ISearch {
	
	
	public HashTableDb(String dbPath) {
		super(dbPath);
		
		// database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();

		dbConfig.setErrorStream(System.err);
		dbConfig.setType(DatabaseType.HASH);
		dbConfig.setAllowCreate(true);

		// database
		try {
			db = new Database(dbPath, null, dbConfig);
		} catch (FileNotFoundException e) {
			System.err.println("Database file not found");
		} catch (DatabaseException e) {
			System.err.println("Failed to open database");
		}
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
		File dbFile = new File(dbPath + File.separator + "table.db");
		if (dbFile.exists()) dbFile.delete();
	}

}
