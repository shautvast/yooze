package yooze;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import yooze.application.GraphBuilderFactory;
import yooze.domain.ClassModel;
import yooze.domain.Graph;

public class TgzBuilderTest {

	@Test
	public void tgzBuilder() throws IOException {
		GraphBuilder tgzBuilder = GraphBuilderFactory.getDefaultTgzBuilder();

		InclusionDecider i = new InclusionDecider();
		i.setPackageIncludePatterns("nl\\.");
		ClassModelBuilder classModelBuilder = new ClassModelBuilder(i);
		tgzBuilder.setClassModelBuilder(classModelBuilder);

		Graph graph = tgzBuilder.build(new DefaultResourceLoader().getResource("classpath:agent.tar.gz").getFile(),
				"nl.jssl.jas.Main");

		ArrayList<String> names = new ArrayList<String>();

		for (ClassModel cm : graph.getChildren()) {
			names.add(cm.getName());
		}
		assertTrue(names.contains("nl.jssl.jas.Main"));
	}

}
