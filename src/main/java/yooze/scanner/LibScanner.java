package yooze.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import yooze.JarClassPath;

import javassist.ClassPath;

/**
 * Scans a directory ("lib") recursively for jarfiles and adds them to the
 * classpath
 * 
 * @author sander
 * 
 */
public class LibScanner implements Scanner {

	@Override
	public List<ClassPath> scanArchive(String archiveName) throws IOException {
		return scanArchive(new File(archiveName));
	}

	@Override
	public List<ClassPath> scanArchive(File file) throws IOException {
		List<ClassPath> classpaths = new ArrayList<ClassPath>();
		return doScanArchive(classpaths,file);
	}

	private List<ClassPath> doScanArchive(List<ClassPath> classpaths, File file)
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
