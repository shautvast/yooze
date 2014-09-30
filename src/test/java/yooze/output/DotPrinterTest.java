package yooze.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.ClassModelBuilder;
import yooze.GraphBuilder;
import yooze.InclusionDecider;
import yooze.application.GraphBuilderFactory;
import yooze.domain.Graph;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class DotPrinterTest {

	@Test
	public void dotPrinting() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilderFactory.getClassesDirectoryBuilder();
		InclusionDecider i = new InclusionDecider();
		i.setPackageExcludePatterns(".*?Class4");
		i.setPackageIncludePatterns(".*?.Class.");
		ClassModelBuilder classModelBuilder = new ClassModelBuilder();
		classModelBuilder.setInclusionDecider(i);
		directoryBuilder.setClassModelBuilder(classModelBuilder);
		Graph graph = directoryBuilder.build("target/test-classes", "yooze.Class1");

		ByteArrayOutputStream bytes = new ByteArrayOutputStream(500);
		DotPrinter d = new DotPrinter(bytes);
		d.print(graph);
		String dotText = new String(bytes.toByteArray());
		d.close();
		String[] expectedDotTextLines = { "digraph \"test-classes\" {", "graph [size=100,100];",
				"\"yooze.Class1\" [shape=box, height=0.0];", "\"yooze.Class1\" -> \"yooze.Class2\"",
				"\"yooze.Class2\" [shape=box, height=0.0];", "\"yooze.Class2\" -> \"yooze.Class3\"",
				"\"yooze.Class3\" [shape=box, height=0.0];", "\"yooze.Class3\" -> \"yooze.Class1\"",
				"\"yooze.Class1\" [shape=box, height=0.0];", "\"yooze.Class2\" [shape=box, height=0.0];",
				"\"yooze.Class3\" [shape=box, height=0.0];", "}" };
		System.out.println(dotText);
		expectTextContainsLines(dotText, expectedDotTextLines);
	}

	private void expectTextContainsLines(String dotText, String[] expectedDotTextLines) {
		for (String line : expectedDotTextLines) {
			Assert.assertTrue("Not found:" + line, dotText.contains(line));
		}
	}

	@Test
	public void noReference() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilderFactory.getClassesDirectoryBuilder();

		InclusionDecider i = new InclusionDecider();
		i.setPackageExcludePatterns("");
		i.setPackageIncludePatterns("yooze.Class4");
		ClassModelBuilder classModelBuilder = new ClassModelBuilder();
		classModelBuilder.setInclusionDecider(i);
		directoryBuilder.setClassModelBuilder(classModelBuilder);

		Graph graph = directoryBuilder.build("target/test-classes", "yooze.Class4");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(1000);
		DotPrinter d = new DotPrinter(bytes);
		d.print(graph);
		String dotText = new String(bytes.toByteArray());
		System.out.println(dotText);
		String[] expectedDotTextLines = { "digraph \"test-classes\" {", "graph [size=100,100];",
				"\"yooze.Class4\" [shape=box, height=0.0];", "\"yooze.Class4\";", "}" };
		d.close();
		expectTextContainsLines(dotText, expectedDotTextLines);
	}
}
