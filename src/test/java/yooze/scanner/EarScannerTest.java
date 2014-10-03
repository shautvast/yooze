package yooze.scanner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import yooze.InspectableClasspath;

public class EarScannerTest {

	@Test
	public void scanner() throws IOException {
		List<InspectableClasspath> classpaths = new ArchiveScanner(new WarScanner())
				.scanArchive(new DefaultResourceLoader().getResource("classpath:examples.ear").getFile());
		for (InspectableClasspath path : classpaths) {
			if (path instanceof DirClassPath) {
				List<String> classes = ((InspectableClasspath) path).getClasses();
				assertThat(classes.size(), is(49));
			}
			if (path instanceof JarClassPath) {
				String name = ((InspectableClasspath) path).getResourceName();
				if (name.contains("standard")) {
					List<String> classes = ((InspectableClasspath) path).getClasses();
					assertTrue(classes.contains("org.apache.taglibs.standard.tag.common.sql.DataSourceUtil"));
				}
			}
		}
	}
}
