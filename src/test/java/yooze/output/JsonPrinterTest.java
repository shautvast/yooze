package yooze.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import yooze.ClassCache;
import yooze.GraphBuilder;
import yooze.application.GraphBuilderFactory;
import yooze.domain.Graph;

public class JsonPrinterTest {
	@Before
	public void clearClassCache() {
		ClassCache.clear();
	}

	@Test
	public void jsonShouldBeWellFormed() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilderFactory.getClassesDirectoryBuilder();
		directoryBuilder.setPackageExcludePatterns(".*?Class4");
		directoryBuilder.setPackageIncludePatterns(".*?.Class.");
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
