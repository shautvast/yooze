package yooze;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.domain.ClassModel;
import yooze.domain.Graph;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class TgzBuilderTest {

	@Autowired
	private Config config;

	@Test
	public void tgzBuilder() throws IOException {
		GraphBuilder tgzBuilder = GraphBuilder.getDefaultTgzBuilder();
		tgzBuilder.setPackageIncludePatterns("nl.*");
		tgzBuilder.setPackageExcludePatterns("");
		Graph graph = tgzBuilder.buildClassDepencyGraph(config.getTgzFile(),
				"nl.jssl.jas.Main");

		ArrayList<String> names = new ArrayList<String>();

		for (ClassModel cm : graph.getChildren()) {
			names.add(cm.getName());
		}
		assertTrue(names.contains("nl.jssl.jas.Main"));
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
