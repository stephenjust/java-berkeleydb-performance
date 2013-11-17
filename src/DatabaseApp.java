
import java.io.File;
import java.nio.file.FileSystemException;


public class DatabaseApp {
	private String tmpDir;
	Integer mode = 0;
	public static void main(String[] args) {
		DatabaseApp app = new DatabaseApp();
		try {
			app.setup(args);
			app.run();
		} catch (FileSystemException e) {
			System.err.println(e.getMessage());
		} finally {
			app.cleanup();
		}
	}
	
	public void setup(String[] args) throws FileSystemException {
		if (System.getProperty("os.name").startsWith("Windows")) {
			tmpDir = "C:\\tmp\\sajust_db";
		} else {
			tmpDir = "/tmp/sajust_dir";
		}
		
		/** Select the appropriate mode based on commandline arguments
		 */
		try{
		if (args[0].equals("btree")) this.mode = 1;
		if (args[0].equals("hash")) this.mode = 2;
		if (args[0].equals("indexfile")) this.mode = 3;
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Please enter in a commandline argument.");
			System.out.println("Acceptable options are: btree, hash, indexfile");
		}
		File tDirFile = new File(tmpDir);
		if (tDirFile.exists()) tDirFile.delete();
		
		if (!(new File(tmpDir)).mkdirs()) {
			throw new FileSystemException("Failed to create temp folder");
		}
		
		

	}
	
	public void run() {
		
		
	}
	
	public void cleanup() {
		File tDirFile = new File(tmpDir);
		if (tDirFile.exists()) tDirFile.delete();
	}

}
