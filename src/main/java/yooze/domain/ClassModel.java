package yooze.domain;

import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;

/**
 * 
 */
public class ClassModel {
	final private String name;

	final private List<ClassModel> references = new ArrayList<ClassModel>();
	final private List<MethodModel> methods = new ArrayList<MethodModel>();
	private CtClass classDefinition;

	public ClassModel(String name) {
		super();
		this.name = name;
	}

	public void addReference(ClassModel classModel) {
		references.add(classModel);
	}

	public void addMethod(MethodModel method) {
		methods.add(method);
	}

	public String getName() {
		return name;
	}

	public List<ClassModel> getReferences() {
		return new ArrayList<ClassModel>(references);
	}

	@Override
	public String toString() {
		return "class " + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassModel other = (ClassModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public void setClass(CtClass ctClass) {
		this.classDefinition = ctClass;
	}

}
