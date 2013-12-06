package yooze;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.domain.Graph;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class LargePackageTest {

	@Autowired
	private Config config;

	@Test
	public void largePackage() throws IOException {
		GraphBuilder earBuilder = GraphBuilder.getEarBuilder();
		earBuilder.setPackageIncludePatterns("");
		earBuilder.setPackageExcludePatterns("java.*");
		Graph graph = earBuilder.buildClassDepencyGraph(config.getEarFile(),
				"java.lang.String");
		DotPrinter dotPrinter = new DotPrinter(new FileOutputStream(
				"/tmp/example.dot"));
		dotPrinter.print(graph);
		dotPrinter.close();
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

}
