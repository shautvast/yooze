package yooze.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import yooze.ClassModelBuilder;
import yooze.GraphBuilder;
import yooze.InclusionDecider;
import yooze.application.GraphBuilderFactory;
import yooze.domain.Graph;

public class JsonPrinterTest {

	@Test
	public void jsonShouldBeWellFormed() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilderFactory.getClassesDirectoryBuilder();

		InclusionDecider i = new InclusionDecider();
		i.setPackageExcludePatterns(".*?Class4");
		i.setPackageIncludePatterns(".*?.Class.");
		ClassModelBuilder classModelBuilder = new ClassModelBuilder();
		classModelBuilder.setInclusionDecider(i);
		directoryBuilder.setClassModelBuilder(classModelBuilder);

		Graph graph = directoryBuilder.build("target/test-classes", "yooze.Class1");

		ByteArrayOutputStream bytes = new ByteArrayOutputStream(500);
		GraphPrinter d = new JsonPrinter(bytes);
		d.print(graph);
		String json = new String(bytes.toByteArray());
		d.close();
		Assert.assertEquals(
				"[{\"key\":\"yooze.Class2\",\"parent\":\"yooze.Class1\"},{\"key\":\"yooze.Class3\",\"parent\":\"yooze.Class2\"},{\"key\":\"yooze.Class1\""
						+ ",\"parent\":\"yooze.Class3\"},]", json);
	}
}
