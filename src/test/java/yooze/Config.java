package yooze;

import java.io.File;

public class Config {
	private File earFile;
	private File tgzFile;
	
	public void setEarFile(File earFile) {
		this.earFile = earFile;
	}
	
	public File getEarFile() {
		return earFile;
	}

	public File getTgzFile() {
		return tgzFile;
	}

	public void setTgzFile(File tgzFile) {
		this.tgzFile = tgzFile;
	}
	
	
}
