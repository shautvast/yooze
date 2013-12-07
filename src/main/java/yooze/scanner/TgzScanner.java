package yooze.scanner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;

import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;

import yooze.InspectableClasspath;
import yooze.Scanner;

/**
 * @author sander
 * 
 */
public class TgzScanner implements Scanner {
	public List<InspectableClasspath> scanArchive(String archiveName) throws IOException {
		return scanArchive(new File(archiveName));
	}

	public List<InspectableClasspath> scanArchive(File file) throws IOException {
		List<InspectableClasspath> classpaths = new ArrayList<InspectableClasspath>();
		TarInputStream tarInputStream = new TarInputStream(new GZIPInputStream(new BufferedInputStream(
				new FileInputStream(file))));
		TarEntry entry;
		while ((entry = tarInputStream.getNextEntry()) != null) {
			if (entry.getName().endsWith(".jar")) {
				int count;
				byte data[] = new byte[2048];

				File tempFile = File.createTempFile(singleName(entry.getName()), ".jar");
				FileOutputStream fos = new FileOutputStream(tempFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos);

				while ((count = tarInputStream.read(data)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				classpaths.add(new JarClassPath(new JarFile(tempFile)));
			}
		}
		tarInputStream.close();
		return classpaths;
	}

	private String singleName(String name) {
		int slash = name.lastIndexOf("/");
		if (slash > -1) {
			return name.substring(slash + 1);
		} else {
			return name;
		}
	}
}
