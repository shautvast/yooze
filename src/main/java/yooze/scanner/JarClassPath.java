package yooze.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javassist.NotFoundException;
import yooze.ClassByteCountingInputStream;
import yooze.ClassNotFound;
import yooze.InspectableClasspath;
import yooze.Util;

public class JarClassPath implements InspectableClasspath {

	private final JarFile jar;

	public JarClassPath(JarFile jar) {
		super();
		this.jar = jar;
	}

	public void close() {
		try {
			jar.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public URL find(String className) {
		try {
			return new URL("file:///" + jar.getName() + "!" + Util.toClassResource(className));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "JarClasspath[" + jar.getName() + "]";
	}

	public List<String> getClasses() {
		List<String> classes = new ArrayList<String>();
		for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
			String name = entries.nextElement().getName().replaceAll("/", ".");
			if (name.endsWith(".class")) {
				classes.add(name.substring(0, name.length() - 6));
			}
		}
		return classes;
	}

	public String getResourceName() {
		return jar.getName();
	}

	@Override
	public InputStream openClassfile(String className) throws NotFoundException {
		ZipEntry entry = jar.getEntry(Util.toClassResource(className));
		if (entry == null) {
			throw new ClassNotFound(className);
		}
		try {
			return new ClassByteCountingInputStream(className, jar.getInputStream(entry));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
