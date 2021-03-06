package yooze;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Test;

import yooze.application.GraphBuilderFactory;
import yooze.domain.MethodModel;
import yooze.dto.MethodDto;

public class MethodReferencesTest {

	@After
	public void reset() {
		MethodCache.getInstance().reset();
	}

	@Test
	public void test() throws IOException {
		GraphBuilder directoryBuilder = GraphBuilderFactory.getClassesDirectoryBuilder();

		InclusionDecider i = new InclusionDecider();
		i.setPackageIncludePatterns("yooze.Class.*");
		ClassModelBuilder classModelBuilder = new ClassModelBuilder(i);
		directoryBuilder.setClassModelBuilder(classModelBuilder);

		directoryBuilder.build("target/test-classes", "yooze.Class1");
		MethodModel mm1 = MethodCache.getInstance().get("yooze.Class1.rup(int)");
		Assert.assertNotNull(mm1);
		MethodModel mm2 = MethodCache.getInstance().get("yooze.Class3.dof()");
		Assert.assertNotNull(mm2);
		List<MethodModel> callers = mm1.getCallers();
		Assert.assertTrue(callers.contains(mm2));
		MethodDto dto = MethodDto.create(MethodCache.getInstance().get("yooze.Class1.zoef(yooze.Class2)"));
		JsonGenerator jg = new ObjectMapper().getJsonFactory().createJsonGenerator(
				new FileOutputStream("c:\\ff\\out.json"));
		jg.writeObject(dto);
		jg.close();
	}
}
