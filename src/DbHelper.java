import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;

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
			File dbFile = new File(dbPath);
			if (!dbFile.exists()) dbFile.createNewFile();
			db = new Database(dbPath, null, dbConfig);

			return db;
		} catch (FileNotFoundException e) {
			System.err.println("Database file not found");
		} catch (DatabaseException e) {
			System.err.println("Failed to open database");
		} catch (IOException e) {
			System.err.println("Failed to create database file");
		}
		return null;
	}

}
