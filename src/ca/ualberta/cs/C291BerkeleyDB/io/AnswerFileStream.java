package ca.ualberta.cs.C291BerkeleyDB.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AnswerFileStream extends OutputStream implements Closeable {

	static final String FILE_NAME = "answers";
	
	File answerFile;
	FileOutputStream outputStream;
	
	public AnswerFileStream() throws IOException {
		answerFile = new File(FILE_NAME);
		if (!answerFile.exists()) answerFile.createNewFile();
		if (!answerFile.canWrite()) throw new IOException("Unable to write file " + FILE_NAME + ".");
		outputStream = new FileOutputStream(answerFile, false);
	}

	@Override
	public void write(int b) throws IOException {
		outputStream.write(b);
	}

	@Override
	public void close() throws IOException {
		if (outputStream != null) outputStream.close();
	}
	
	
}
