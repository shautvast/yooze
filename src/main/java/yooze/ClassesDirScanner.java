package yooze;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPath;
import yooze.scanner.Scanner;

/**
 * reads classes as .class files from a directory
 */
public class ClassesDirScanner implements Scanner {

	@Override
	public List<ClassPath> scanArchive(String archiveName) throws IOException {
		return scanArchive(new File(archiveName));
	}

	@Override
	public List<ClassPath> scanArchive(File file) throws IOException {
		List<ClassPath> result = new ArrayList<ClassPath>();
		result.add(new DirClassPath(file));
		return result;
	}

}
