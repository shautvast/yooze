package yooze.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import yooze.InspectableClasspath;
import yooze.Scanner;

/**
 * Scans a directory ("lib") recursively for jarfiles and adds them to the classpath
 */
public class LibScanner implements Scanner {

	@Override
	public List<InspectableClasspath> scanArchive(String archiveName) throws IOException {
		return scanArchive(new File(archiveName));
	}

	@Override
	public List<InspectableClasspath> scanArchive(File file) throws IOException {
		List<InspectableClasspath> classpaths = new ArrayList<InspectableClasspath>();
		return doScanArchive(classpaths, file);
	}

	/*
	 * return argument classpaths for sake of recursion
	 */
	private List<InspectableClasspath> doScanArchive(List<InspectableClasspath> classpaths, File file)
			throws IOException {
		File[] entries = file.listFiles();
		for (File entry : entries) {
			if (entry.isDirectory()) {
				doScanArchive(classpaths, entry);
			} else if (entry.getName().endsWith(".jar")) {
				classpaths.add(new JarClassPath(new JarFile(entry)));
			}
		}
		return classpaths;
	}
}
