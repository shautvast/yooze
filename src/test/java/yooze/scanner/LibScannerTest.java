package yooze.scanner;

import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import yooze.InspectableClasspath;

public class LibScannerTest {
	@Test
	public void directoryWithoutJars_isNotAClasspathEntry() throws IOException {
		LibScanner libScanner = new LibScanner();
		List<InspectableClasspath> classpathList = libScanner.scanArchive("src");
		assertTrue("This should be empty", classpathList.isEmpty());
	}
}
