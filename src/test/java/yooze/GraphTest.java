package yooze;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yooze.domain.ClassModel;
import yooze.domain.Graph;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class GraphTest {

	@Test
	public void buildGraph() throws IOException{
//		 new Yooze("/tmp/test").createNeoGraph("target/test-classes", ".*?.Class.");
		GraphBuilder libDirectoryBuilder = GraphBuilder.getLibDirectoryBuilder();
		libDirectoryBuilder.setPackageIncludePatterns(".*?.Class.");
		libDirectoryBuilder.setPackageExcludePatterns("");
		 Graph graph = libDirectoryBuilder.build("target/test-classes");
		 ClassModel class1=graph.getChildren().get(0);
		 
		 assertTrue(class1!=null);
		 ClassModel class2Dummy=new ClassModel("yooze.Class2"); 
		 assertTrue(class1.getReferences().contains(class2Dummy));
		 
		 ClassModel class2=class1.getReferences().get(0);
		 assertTrue (class2.getName().equals("yooze.Class2"));
		 ClassModel class3=class2.getReferences().get(0);
		 assertTrue (class3.getName().equals("yooze.Class3"));
		 
		 assertTrue(class2.getReferences().contains(class3));
		 assertTrue(class3.getReferences().contains(class1));
		 
	}
}
