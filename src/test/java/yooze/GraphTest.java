package yooze;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import yooze.application.GraphBuilderFactory;
import yooze.domain.ClassModel;
import yooze.domain.Graph;

public class GraphTest {

	@Test
	public void buildGraph() throws IOException {
		GraphBuilder libDirectoryBuilder = GraphBuilderFactory.getClassesDirectoryBuilder();

		InclusionDecider i = new InclusionDecider();
		i.setPackageIncludePatterns(".*?.Class.");
		ClassModelBuilder classModelBuilder = new ClassModelBuilder(i);
		libDirectoryBuilder.setClassModelBuilder(classModelBuilder);

		Graph graph = libDirectoryBuilder.build("target/test-classes", "yooze.Class1");
		ClassModel class1 = graph.getChildren().get(0);
		Assert.assertNotNull(class1);

		ClassModel class2Dummy = new ClassModel("yooze.Class2");
		assertTrue(class1.getReferences().contains(class2Dummy));

		ClassModel class2 = class1.getReferences().get(0);
		assertTrue(class2.getName().equals("yooze.Class2"));
		ClassModel class3 = class2.getReferences().get(0);
		assertTrue(class3.getName().equals("yooze.Class3"));

		assertTrue(class2.getReferences().contains(class3));
		assertTrue(class3.getReferences().contains(class1));

	}
}
