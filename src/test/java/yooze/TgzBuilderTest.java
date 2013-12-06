package yooze;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.domain.ClassModel;
import yooze.domain.Graph;
import static org.junit.Assert.assertTrue;

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
		Graph graph = tgzBuilder.buildClassDepencyGraph(config.getTgzFile());
				
		ArrayList<String> names = new ArrayList<String>();

		for (ClassModel cm : graph.getChildren()) {
			names.add(cm.getName());
		}
		assertTrue(names.contains("nl.jssl.jas.Main"));
		assertTrue(names.contains("nl.jssl.jas.agent.Agent"));
		assertTrue(names
				.contains("nl.jssl.jas.instrumentation.ClassTransformer"));
		assertTrue(names
				.contains("nl.jssl.jas.instrumentation.JavassistInstrumenter"));
		assertTrue(names.contains("nl.jssl.jas.measurement.Measurement"));
		assertTrue(names.contains("nl.jssl.jas.measurement.Stopwatch"));
		assertTrue(names.contains("nl.jssl.testjas.TestClass"));
		assertTrue(names.contains("nl.jssl.testjas.Instrument"));
		assertTrue(names.contains("nl.jssl.testjas.AgentTest"));

	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
