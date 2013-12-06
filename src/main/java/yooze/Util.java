package yooze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

public class Util {
	private final static Pattern fileNamePattern = Pattern.compile(".+/(.+)");

	public static File extractFile(ZipFile file, ZipEntry entry)
			throws IOException {		
		String name=entry.getName();
		Matcher m = fileNamePattern.matcher(name);
		if (m.matches()){//chop off path
			name=m.group(1);
		}
		
		File tempFile = File.createTempFile(name, ".file");
		InputStream in = file.getInputStream(entry);
		FileOutputStream out = new FileOutputStream(tempFile);
		IOUtils.copy(in, out);
		return tempFile;
	}

	public static String toClassResource(String className) {
		return className.replaceAll("\\.", "/")+".class";
	}

	public static String toClassName(String classResource) {
		return classResource.replaceAll("/", ".");
	}
}
