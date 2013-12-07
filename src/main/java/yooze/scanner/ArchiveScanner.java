package yooze.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import yooze.InspectableClasspath;
import yooze.Scanner;
import yooze.Util;

/**
 * TODO separate models for wars
 */
public class ArchiveScanner implements Scanner {
	private WarScanner warScanner;

	public ArchiveScanner(WarScanner warScanner) {
		this.warScanner = warScanner;
	}

	public List<InspectableClasspath> scanArchive(String archiveName) {
		return scanArchive(new File(archiveName));
	}

	public List<InspectableClasspath> scanArchive(File archiveFile) {
		List<InspectableClasspath> result = new ArrayList<InspectableClasspath>();
		if (!archiveFile.exists()) {
			return result;
		}

		if (archiveFile.isDirectory()) {
			return directoryAsClasspath(archiveFile, result);
		} else {
			JarFile archive = createJar(archiveFile);
			if (isEarFile(archive)) {
				result.addAll(scanRoot(archive));
				result.addAll(warScanner.scanWars(getWars(archive)));

			} else if (isWarFile(archive)) {
				result.addAll(warScanner.scanWars(Arrays.asList(new JarFile[] { archive })));
			} else {
				// treat as jar file
				result.add(new JarClassPath(archive));
			}
			return result;
		}
	}

	private boolean isWarFile(JarFile archive) {
		return archive.getName().endsWith(".war");
	}

	private boolean isEarFile(JarFile archive) {
		return archive.getName().endsWith(".ear");
	}

	private List<InspectableClasspath> directoryAsClasspath(File archiveFile, List<InspectableClasspath> result) {
		File[] list = archiveFile.listFiles();
		for (File entry : list) {
			if (entry.getName().endsWith(".jar")) {
				result.add(new JarClassPath(createJar(entry)));
			} else if (entry.getName().endsWith(".class")) {
				result.add(new DirClassPath(archiveFile));
				break;
			}
		}

		return result;
	}

	private JarFile createJar(File archiveFile) {
		try {
			return new JarFile(archiveFile);
		} catch (IOException e) {
			throw new JarCouldNotBeCreated(archiveFile.getAbsolutePath());
		}
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

	private List<InspectableClasspath> scanRoot(JarFile earfile) {
		List<InspectableClasspath> classpaths = new ArrayList<InspectableClasspath>();
		for (Enumeration<JarEntry> entries = earfile.entries(); entries.hasMoreElements();) {
			JarEntry entry = (JarEntry) entries.nextElement();
			if (isJarfile(entry)) {
				try {
					ClasspathAdder.addEntriesFromWarToClasspath(earfile, classpaths, entry);
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

	@SuppressWarnings("serial")
	private static class JarCouldNotBeCreated extends RuntimeException {
		public JarCouldNotBeCreated(String name) {
			super(name);
		}
	}
}
