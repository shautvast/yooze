package yooze.domain;

import javassist.NotFoundException;
import javassist.expr.MethodCall;
import yooze.ClassCache;
import yooze.MethodCache;

public class MethodCallModel {
	private MethodCall methodCall;

	public MethodCallModel(MethodCall m) {
		this.methodCall = m;
	}

	public ClassModel getCalledClass() {
		return ClassCache.get(methodCall.getClassName());
	}

	public MethodModel getCalledMethod() {
		try {
			ParameterList parameterList = ParameterList.create(methodCall.getMethod());
			return MethodCache.getInstance().get(
					createQualifiedMethodname(parameterList));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private String createQualifiedMethodname(ParameterList parameterList) {
		return methodCall.getClassName() + "." + methodCall.getMethodName() + "(" + parameterList.asText() + ")";
	}
}
