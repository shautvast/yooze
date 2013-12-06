package yooze.etc;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import yooze.Util;
import yooze.domain.ClassModel;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;

/**
 * @deprecated does not seem to be in use
 * 
 */
public class ClassBuilder {

	private final ConcurrentHashMap<String, ClassModel> classes = new ConcurrentHashMap<String, ClassModel>();

	private ClassPool pool = ClassPool.getDefault();

	private ClassBuilder(List<ClassPath> classpaths) {
		super();
		for (ClassPath cpEntry : classpaths) {
			pool.appendClassPath(cpEntry);
		}
	}

	public ClassModel load(String className) throws NotFoundException {
		ClassModel model;
		if ((model = classes.get(className)) != null) {
			return model;
		}

		CtClass ctClass = pool.getCtClass(className);
		model = new ClassModel(className);
		ConstPool constPool = ctClass.getClassFile().getConstPool();
		for (Iterator<?> classNames = constPool.getClassNames().iterator(); classNames.hasNext();) {
			model.addReference(load(Util.toClassName(classNames.next().toString())));
		}
		return model;

	}
}
