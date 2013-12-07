package yooze;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private Pattern[] packageIncludePatterns;
	private Pattern[] packageExcludePatterns;
	private ClassPool pool;

	public ClassModelBuilder(ClassPool pool) {
		this.pool = pool;
	}

	public ClassModel scanClassOrSkip(String className) {
		if (shouldSkip(className))
			return null;
		if (ClassCache.contains(className)) {
			return ClassCache.get(className);
		}
		log.info("scanning {}", className);

		return scan(className);

	}

	private ClassModel scan(String className) {
		ClassModel model = new ClassModel(className);
		ClassCache.add(className, model);
		return tryScan(className, model);
	}

	private ClassModel tryScan(String className, ClassModel model) {
		CtClass ctClass = getClassFromJavassist(className);
		if (isScannable(ctClass)) {
			ConstPool constPool = ctClass.getClassFile().getConstPool();

			addClassReferences(model, constPool);
			addMethods(model, ctClass);
			resolveMethodReferences();
			return model;
		} else {
			throw new ClassNotFound(className);
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

	private boolean shouldSkip(String className) {
		if (!isIncluded(className)) {
			log.debug("skipping {}", className);
			return true;
		}
		if (isExcluded(className)) {
			log.debug("skipping {}", className);
			return true;
		}

		return false;
	}

	private boolean isExcluded(String className) {
		if (packageExcludePatterns != null) {
			for (Pattern excludePattern : packageExcludePatterns) {
				Matcher matcher = excludePattern.matcher(className);
				if (matcher.matches()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isIncluded(String className) {
		if (packageIncludePatterns != null) {
			for (Pattern includePattern : packageIncludePatterns) {
				Matcher matcher = includePattern.matcher(className);
				if (matcher.matches()) {
					return true;
				}
			}
		}
		return false;
	}

	public void setPackageIncludePatterns(String... packageIncludePatterns) {
		if (packageIncludePatterns != null) {
			this.packageIncludePatterns = new Pattern[packageIncludePatterns.length];
			int i = 0;
			for (String pattern : packageIncludePatterns) {
				this.packageIncludePatterns[i++] = Pattern.compile(pattern);
			}
		}
	}

	public void setPackageExcludePatterns(String... packageExcludePatterns) {
		if (packageExcludePatterns != null) {
			this.packageExcludePatterns = new Pattern[packageExcludePatterns.length];
			int i = 0;
			for (String pattern : packageExcludePatterns) {
				this.packageExcludePatterns[i++] = Pattern.compile(pattern);
			}
		}
	}
}
