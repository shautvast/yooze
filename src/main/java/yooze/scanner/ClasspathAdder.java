package yooze.scanner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import yooze.InspectableClasspath;
import yooze.Util;

public class ClasspathAdder {
	public static void addEntriesFromWarToClasspath(JarFile warfile, List<InspectableClasspath> classpaths,
			JarEntry entry) throws IOException {
		File jarFile = Util.extractFile(warfile, entry);
		classpaths.add(new JarClassPath(new JarFile(jarFile)));
	}
}
