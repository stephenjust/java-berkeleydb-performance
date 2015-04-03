package ca.ualberta.cs.C291BerkeleyDB.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;


public class BtreeDB extends BaseDb {

	public BtreeDB(String dbPath) throws IOException {
		super(dbPath);
		// database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();

		dbConfig.setErrorStream(System.err);
		dbConfig.setType(DatabaseType.BTREE);
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
	public void cleanUp() {
		File dbFile = new File(dbPath + File.separator + "table.db");
		if (dbFile.exists()) dbFile.delete();
	}

	
	



}
