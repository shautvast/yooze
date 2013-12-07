package yooze.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yooze.InspectableClasspath;
import yooze.Scanner;

/**
 * reads classes as .class files from a directory
 */
public class ClassesDirScanner implements Scanner {

	@Override
	public List<InspectableClasspath> scanArchive(String archiveName) throws IOException {
		return scanArchive(new File(archiveName));
	}

	@Override
	public List<InspectableClasspath> scanArchive(File file) throws IOException {
		List<InspectableClasspath> result = new ArrayList<InspectableClasspath>();
		result.add(new DirClassPath(file));
		return result;
	}

}
