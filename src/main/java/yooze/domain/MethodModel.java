package yooze.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import yooze.MethodCache;

public class MethodModel {
	private ClassModel containingClass;
	private String name;
	private ParameterList parameterList;
	private final List<MethodCallModel> methodCalls = new ArrayList<MethodCallModel>();
	private final Set<MethodModel> callers = new HashSet<MethodModel>();

	public static MethodModel create(ClassModel containingClass, CtMethod method) {
		MethodModel methodModel = new MethodModel(containingClass, method);
		MethodCache.getInstance().add(methodModel);
		methodModel.scanSubsequentMethodCalls(method);
		return methodModel;
	}

	private MethodModel(ClassModel containingClass, CtMethod method) {
		this.containingClass = containingClass;
		name = method.getName();
		parameterList = ParameterList.create(method);
	}

	public void addCaller(MethodModel method) {
		callers.add(method);
	}

	public List<MethodModel> getCallers() {
		return new ArrayList<MethodModel>(callers);
	}

	public List<MethodCallModel> getMethodCalls() {
		return methodCalls;
	}

	private void scanSubsequentMethodCalls(CtMethod method) {
		try {
			method.instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					MethodCallModel methodCallModel = new MethodCallModel(m);
					methodCalls.add(methodCallModel);
				}
			});
		} catch (CannotCompileException e) {
		}
	}

	public String getName() {
		return name;
	}

	public String getFullname() {
		return containingClass.getName() + "." + name + "(" + parameterList.asText() + ")";
	}

	@Override
	public String toString() {
		return getFullname();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MethodModel)) {
			return false;
		}
		MethodModel other = (MethodModel) obj;
		return this.getFullname().equals(other.getFullname());
	}

	@Override
	public int hashCode() {
		return getFullname().hashCode();
	}
}
