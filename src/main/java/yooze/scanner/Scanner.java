package yooze.scanner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javassist.ClassPath;

public interface Scanner {
	public List<ClassPath> scanArchive(String archiveName) throws IOException;

	public List<ClassPath> scanArchive(File file) throws IOException;
}
