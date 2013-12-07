package yooze.scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;

import yooze.InspectableClasspath;

public class WarScanner {
	private final static Pattern classPattern = Pattern.compile("WEB-INF/classes/(.*)\\.class");
	private final static Pattern packagePattern = Pattern.compile("(.+\\/+)(.+\\.class)");

	public List<InspectableClasspath> scanWars(List<JarFile> wars) {
		List<InspectableClasspath> classpaths = new ArrayList<InspectableClasspath>();
		for (JarFile war : wars) {
			classpaths.addAll(scanWar(war));
		}
		return classpaths;
	}

	private List<InspectableClasspath> scanWar(JarFile warfile) {
		List<InspectableClasspath> classpaths = new ArrayList<InspectableClasspath>();
		File classesDir = createTempLocation(classpaths);
		classpaths.add(new DirClassPath(classesDir));
		for (Enumeration<JarEntry> entries = warfile.entries(); entries.hasMoreElements();) {
			JarEntry entry = (JarEntry) entries.nextElement();
			if (isArchive(entry)) {
				try {
					ClasspathAdder.addEntriesFromWarToClasspath(warfile, classpaths, entry);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				extractClass(warfile, entry, classesDir);
			}
		}
		return classpaths;
	}

	private File createTempLocation(List<InspectableClasspath> classpaths) {
		File classesDir = new File(new File(System.getProperty("java.io.tmpdir")), "classes"
				+ System.currentTimeMillis());
		boolean dirsMade = classesDir.mkdirs();
		if (!dirsMade) {
			throw new RuntimeException("Directory " + classesDir + " could not be created");
		}
		return classesDir;
	}

	private boolean isArchive(JarEntry entry) {
		String name = entry.getName();
		return name.startsWith("WEB-INF/lib") && name.endsWith(".jar");
	}

	private File extractClass(JarFile warfile, ZipEntry entry, File classesDir) {
		Matcher matcher = classPattern.matcher(entry.getName());
		if (matcher.matches()) {
			String className = matcher.group(1) + ".class";
			Matcher matcher2 = packagePattern.matcher(className);
			if (matcher2.matches()) {
				String packageName = matcher2.group(1);
				File classDir = createUnarchivedPackageDicectory(classesDir, packageName);
				String simpleClassName = matcher2.group(2);
				File classFile = new File(classDir, simpleClassName);
				return createUnarchivedClassfile(warfile, entry, classFile);
			}
		}
		return null;
	}

	private File createUnarchivedClassfile(JarFile warfile, ZipEntry entry, File classFile) {
		try {
			FileOutputStream out = new FileOutputStream(classFile);
			IOUtils.copy(warfile.getInputStream(entry), out);
			return classFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File createUnarchivedPackageDicectory(File classesDir, String packageName) {
		File classDir = new File(classesDir, packageName);
		if (!classDir.exists()) {
			boolean packageDirsMade = classDir.mkdirs();
			if (!packageDirsMade) {
				throw new DirectoryCouldNotBeCreated(classDir);
			}
		}
		return classDir;
	}

	private static class DirectoryCouldNotBeCreated extends RuntimeException {
		public DirectoryCouldNotBeCreated(File classDir) {
			super(classDir.toString());
		}
	}

}
