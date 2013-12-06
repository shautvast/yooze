package yooze;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.domain.Graph;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class DotPrinterTest {

	@Test
	public void dotPrinting() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilder.getClassesDirectoryBuilder();
		directoryBuilder.setPackageExcludePatterns(".*?Class4");
		directoryBuilder.setPackageIncludePatterns(".*?.Class.");
		Graph graph = directoryBuilder.build("target/test-classes");

		ByteArrayOutputStream bytes = new ByteArrayOutputStream(500);
		DotPrinter d = new DotPrinter(bytes);
		d.print(graph);
		String dotText = new String(bytes.toByteArray());
		d.close();
		String expectedDotText = "digraph \"test-classes\" {\r\n" //
				+ "graph [size=100,100];\r\n"
				+ "\"yooze.Class1\" [shape=box, height=0.0];\r\n" //
				+ "\"yooze.Class1\" -> \"yooze.Class2\"\r\n"
				+ "\"yooze.Class2\" [shape=box, height=0.0];\r\n"
				+ "\"yooze.Class2\" -> \"yooze.Class3\"\r\n"
				+ "\"yooze.Class3\" [shape=box, height=0.0];\r\n"
				+ "\"yooze.Class3\" -> \"yooze.Class1\"\r\n"
				+ "\"yooze.Class1\" [shape=box, height=0.0];\r\n"
				+ "\"yooze.Class2\" [shape=box, height=0.0];\r\n" //
				+ "\"yooze.Class3\" [shape=box, height=0.0];\r\n" + "}\r\n";

		assertEquals(expectedDotText, dotText);
	}

	@Test
	public void noReference() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilder.getClassesDirectoryBuilder();
		directoryBuilder.setPackageExcludePatterns("");
		directoryBuilder.setPackageIncludePatterns("yooze.Class4");
		Graph graph = directoryBuilder.build("target/test-classes");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(1000);
		DotPrinter d = new DotPrinter(bytes);
		d.print(graph);
		String dotText = new String(bytes.toByteArray());
		String expectedDotText = "digraph \"test-classes\" {\r\n" //
				+ "graph [size=100,100];\r\n"//
				+ "\"yooze.Class4\" [shape=box, height=0.0];\r\n" //
				+ "\"yooze.Class4\";\r\n" //
				+ "}\r\n";
		d.close();
		assertEquals(expectedDotText, dotText);
	}
}
