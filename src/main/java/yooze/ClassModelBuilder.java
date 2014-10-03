package yooze;

import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yooze.domain.ClassModel;
import yooze.domain.MethodCallModel;
import yooze.domain.MethodModel;

/**
 * Builds a ClassModel.
 */
public class ClassModelBuilder {
	private static Logger log = LoggerFactory.getLogger(ClassModelBuilder.class);

	private ClassPool pool;
	private final InclusionDecider inclusionDecider;
	private ClassCache classCache;

	public ClassModelBuilder(InclusionDecider inclusionDecider) {
		this.inclusionDecider = inclusionDecider;
		classCache = ClassCache.getInstance();
		classCache.setInclusionDecider(inclusionDecider);
	}

	public ClassModel scanClassOrSkip(String className) {
		if (inclusionDecider.shouldSkip(className))
			return null;
		if (classCache.contains(className)) {
			return classCache.get(className);
		}
		log.info("scanning {}", className);

		return scan(className);

	}

	private ClassModel scan(String className) {
		ClassModel model = new ClassModel(className);
		classCache.add(className, model);
		return tryScan(className, model);
	}

	private ClassModel tryScan(String className, ClassModel model) {
		CtClass ctClass = getClassFromJavassist(className);
		model.setClass(ctClass);
		if (isScannable(ctClass)) {
			try {
				ConstPool constPool = ctClass.getClassFile().getConstPool();

				addClassReferences(model, constPool);
				addMethods(model, ctClass);
				resolveMethodReferences();
				return model;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getClass().getName() + ":" + e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	private CtClass getClassFromJavassist(String className) {
		try {
			return pool.get(className);
		} catch (NotFoundException e) {
			throw new ClassNotFound(className);
		}
	}

	private boolean isScannable(CtClass ctClass) {
		return !ctClass.isFrozen();
	}

	private void resolveMethodReferences() {
		for (MethodModel method : MethodCache.getInstance().getMethods()) {
			for (MethodCallModel methodCall : method.getMethodCalls()) {
				MethodModel calledMethod = methodCall.getCalledMethod();
				if (calledMethod != null && calledMethod != method) {
					calledMethod.addCaller(method);
				}
			}
		}
	}

	private void addMethods(ClassModel containingClass, CtClass ctClass) {
		CtMethod[] methods = ctClass.getMethods();
		for (CtMethod method : methods) {
			containingClass.addMethod(MethodModel.create(containingClass, method));
		}
	}

	@SuppressWarnings("unchecked")
	private void addClassReferences(ClassModel model, ConstPool constPool) {
		Set<String> classNames = constPool.getClassNames();
		for (String classResourcename : classNames) {
			String refClassName = Util.toClassName(classResourcename);
			addClassReference(model, refClassName);
		}
	}

	private void addClassReference(ClassModel model, String refClassName) {
		/* recursive invocation */
		ClassModel scannedClass = scanClassOrSkip(refClassName);
		if (scannedClass != null && !scannedClass.equals(model)) {
			model.addReference(scannedClass);
		}
	}

	public void setPool(ClassPool pool) {
		this.pool = pool;
	}

}
