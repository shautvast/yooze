package yooze;

import java.io.IOException;
import java.io.InputStream;

public class ClassByteCountingInputStream extends InputStream {

	private InputStream nestedStream;
	private String className;
	private long count=0;

	public ClassByteCountingInputStream(String className,
			InputStream inputStream) {
		this.className=className;
		this.nestedStream=inputStream;
	}

	@Override
	public int read() throws IOException {
		count++;
		return nestedStream.read();
	}
	
	@Override
	public void close() throws IOException {
		nestedStream.close();
		Statistics.addBytecodeSizeForClass(className, count);
	}

}
