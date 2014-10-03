package yooze;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import yooze.application.GraphBuilderFactory;
import yooze.domain.Graph;
import yooze.output.DotPrinter;

public class LargePackageTest {

	@Test
	public void largePackage() throws IOException {
		GraphBuilder earBuilder = GraphBuilderFactory.getEarBuilder();

		InclusionDecider i = new InclusionDecider();
		i.setPackageIncludePatterns("");
		i.setPackageExcludePatterns("java.*");
		ClassModelBuilder classModelBuilder = new ClassModelBuilder(i);
		earBuilder.setClassModelBuilder(classModelBuilder);

		Graph graph = earBuilder.build(new DefaultResourceLoader().getResource("classpath:examples.ear").getFile(),
				"java.lang.String");
		DotPrinter dotPrinter = new DotPrinter(new FileOutputStream("/tmp/example.dot"));
		dotPrinter.print(graph);
		dotPrinter.close();
	}

}
