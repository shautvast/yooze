package yooze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPath;
import javassist.NotFoundException;

public class DirClassPath implements ClassPath, Inspectable {

	private final File dir;

	public DirClassPath(File dir) {
		super();
		this.dir = dir;
	}

	public void close() {
	}

	public URL find(String className) {
		try {
			return new URL("file:///" + dir.getCanonicalPath() + "/" + Util.toClassResource(className));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "DirectoryClasspath[" + dir + "]";
	}

	public List<String> getClasses() {
		List<String> classes = new ArrayList<String>();
		getClasses(dir, dir, classes);
		return classes;
	}

	private void getClasses(File root, File dir, List<String> classes) {
		File[] fileList = dir.listFiles();
		for (File entry : fileList) {
			if (entry.isDirectory()) {
				// recurse deeper
				getClasses(root, entry, classes);
			} else if (isClassFile(entry)) {
				classes.add(createQualifiedClassNameFromFileLocation(root, entry));
			}
		}
	}

	private boolean isClassFile(File entry) {
		return entry.isFile() && entry.getName().endsWith(".class");
	}

	private String createQualifiedClassNameFromFileLocation(File root, File classFile) {
		String absolutePath = classFile.getAbsolutePath();
		String relativePath = absolutePath.substring(root.getAbsolutePath().length() + 1);
		String packageFormat = relativePath.replaceAll("\\\\", ".").replaceAll("/", ".");
		String substring = packageFormat.substring(0, packageFormat.length() - 6);
		return substring;
	}

	@Override
	public String getResourceName() {
		return dir.getName();
	}

	@Override
	public InputStream openClassfile(String className) throws NotFoundException {
		File classFile = new File(dir, Util.toClassResource(className) + ".class");
		if (!classFile.exists()) {
			return null;
		}
		try {
			return new ClassByteCountingInputStream(className, new FileInputStream(classFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
