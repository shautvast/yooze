package yooze;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.domain.Graph;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class DotPrinterTest {

	@Before
	public void clearClassCache() {
		ClassCache.clear();
	}

	@Test
	public void dotPrinting() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilder
				.getClassesDirectoryBuilder();
		directoryBuilder.setPackageExcludePatterns(".*?Class4");
		directoryBuilder.setPackageIncludePatterns(".*?.Class.");
		Graph graph = directoryBuilder.build("target/test-classes",
				"yooze.Class1");

		ByteArrayOutputStream bytes = new ByteArrayOutputStream(500);
		DotPrinter d = new DotPrinter(bytes);
		d.print(graph);
		String dotText = new String(bytes.toByteArray());
		d.close();
		String[] expectedDotTextLines = { "digraph \"test-classes\" {",
				"graph [size=100,100];",
				"\"yooze.Class1\" [shape=box, height=0.0];",
				"\"yooze.Class1\" -> \"yooze.Class2\"",
				"\"yooze.Class2\" [shape=box, height=0.0];",
				"\"yooze.Class2\" -> \"yooze.Class3\"",
				"\"yooze.Class3\" [shape=box, height=0.0];",
				"\"yooze.Class3\" -> \"yooze.Class1\"",
				"\"yooze.Class1\" [shape=box, height=0.0];",
				"\"yooze.Class2\" [shape=box, height=0.0];",
				"\"yooze.Class3\" [shape=box, height=0.0];", "}" };
		expectTextContainsLines(dotText, expectedDotTextLines);
	}

	private void expectTextContainsLines(String dotText,
			String[] expectedDotTextLines) {
		for (String line : expectedDotTextLines) {
			Assert.assertTrue("Not found:" + line, dotText.contains(line));
		}
	}

	@Test
	public void noReference() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilder
				.getClassesDirectoryBuilder();
		directoryBuilder.setPackageExcludePatterns("");
		directoryBuilder.setPackageIncludePatterns("yooze.Class4");
		Graph graph = directoryBuilder.build("target/test-classes",
				"yooze.Class4");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(1000);
		DotPrinter d = new DotPrinter(bytes);
		d.print(graph);
		String dotText = new String(bytes.toByteArray());
		System.out.println(dotText);
		String[] expectedDotTextLines = { "digraph \"test-classes\" {",
				"graph [size=100,100];",
				"\"yooze.Class4\" [shape=box, height=0.0];",
				"\"yooze.Class4\";", "}" };
		d.close();
		expectTextContainsLines(dotText, expectedDotTextLines);
	}
}
