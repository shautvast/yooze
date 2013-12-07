package yooze.scanner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.Config;
import yooze.InspectableClasspath;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class EarScannerTest {

	@Autowired
	private Config config;

	@Test
	public void scanner() throws IOException {
		List<InspectableClasspath> classpaths = new ArchiveScanner(new WarScanner()).scanArchive(config.getEarFile());
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

	public void setConfig(Config config) {
		this.config = config;
	}

}
