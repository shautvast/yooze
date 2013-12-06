package yooze.scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import javassist.ClassPath;

import org.apache.commons.io.IOUtils;

import yooze.DirClassPath;
import yooze.JarClassPath;
import yooze.Util;

/**
 * @author shautvast TODO separate models for wars
 */
public class ArchiveScanner implements Scanner {
	private final static Pattern classPattern = Pattern.compile("WEB-INF/classes/(.*)\\.class");
	private final static Pattern packagePattern = Pattern.compile("(.+\\/+)(.+\\.class)");

	public List<ClassPath> scanArchive(String archiveName) throws IOException {
		return scanArchive(new File(archiveName));
	}

	public List<ClassPath> scanArchive(File archiveFile) throws IOException {
		List<ClassPath> result = new ArrayList<ClassPath>();
		if (!archiveFile.exists()) {
			return result;
		}

		if (archiveFile.isDirectory()) {
			File[] list = archiveFile.listFiles();
			for (File entry : list) {
				if (entry.getName().endsWith(".jar")) {
					result.add(new JarClassPath(new JarFile(entry)));
				} else if (entry.getName().endsWith(".class")) {
					result.add(new DirClassPath(archiveFile));
					break;
				}
			}

			return result;
		}

		JarFile archive = new JarFile(archiveFile);
		if (archive.getName().endsWith(".ear")) {
			result.addAll(scanRoot(archive));
			result.addAll(scanWars(getWars(archive)));

		} else if (archive.getName().endsWith(".war")) {
			result.addAll(scanWars(Arrays.asList(new JarFile[] { archive })));
		} else {
			// treat as jar file
			result.add(new JarClassPath(archive));
		}
		return result;
	}

	private List<ClassPath> scanWars(List<JarFile> wars) {
		List<ClassPath> classpaths = new ArrayList<ClassPath>();
		for (JarFile war : wars) {
			classpaths.addAll(scanWar(war));
		}
		return classpaths;
	}

	private List<ClassPath> scanWar(JarFile warfile) {
		List<ClassPath> classpaths = new ArrayList<ClassPath>();
		File classesDir = createTempLocation(classpaths);
		classpaths.add(new DirClassPath(classesDir));
		for (Enumeration<JarEntry> entries = warfile.entries(); entries.hasMoreElements();) {
			JarEntry entry = (JarEntry) entries.nextElement();
			if (isArchive(entry)) {
				try {
					addToClasspath(warfile, classpaths, entry);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				extractClass(warfile, entry, classesDir);
			}
		}
		return classpaths;
	}

	private File createTempLocation(List<ClassPath> classpaths) {
		File classesDir = new File(new File(System.getProperty("java.io.tmpdir")), "classes"
				+ System.currentTimeMillis());
		boolean dirsMade = classesDir.mkdirs();
		if (!dirsMade) {
			throw new RuntimeException("Directory " + classesDir + " could not be created");
		}
		return classesDir;
	}

	private void addToClasspath(JarFile warfile, List<ClassPath> classpaths, JarEntry entry) throws IOException {
		File jarFile = Util.extractFile(warfile, entry);
		classpaths.add(new JarClassPath(new JarFile(jarFile)));
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
				throw new RuntimeException("Directory " + classDir + " could not be created");
			}
		}
		return classDir;
	}

	private List<JarFile> getWars(JarFile earfile) {
		List<JarFile> wars = new ArrayList<JarFile>();
		for (Enumeration<JarEntry> entries = earfile.entries(); entries.hasMoreElements();) {
			JarEntry entry = (JarEntry) entries.nextElement();
			if (isWarfile(entry)) {
				try {
					File warFile = Util.extractFile(earfile, entry);
					wars.add(new JarFile(warFile));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return wars;
	}

	private boolean isWarfile(JarEntry entry) {
		return entry.getName().endsWith(".war");
	}

	private List<ClassPath> scanRoot(JarFile earfile) {
		List<ClassPath> classpaths = new ArrayList<ClassPath>();
		for (Enumeration<JarEntry> entries = earfile.entries(); entries.hasMoreElements();) {
			JarEntry entry = (JarEntry) entries.nextElement();
			if (isJarfile(entry)) {
				try {
					addToClasspath(earfile, classpaths, entry);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return classpaths;
	}

	private boolean isJarfile(JarEntry entry) {
		return entry.getName().endsWith(".jar");
	}

}
