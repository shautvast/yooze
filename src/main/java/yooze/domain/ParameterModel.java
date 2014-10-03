package yooze.domain;

import javassist.CtClass;
import yooze.ClassCache;

public class ParameterModel {
	private ClassModel type;
	private String name;

	public ParameterModel(CtClass typeAsCtClass) {
		String classname = typeAsCtClass.getName();
		type = ClassCache.getInstance().get(classname);
		if (type == null) {
			type = ClassCache.getInstance().createNewDummyModel(classname);
		}
		name = "";// javassist doesn't give me this (?)
	}

	public ClassModel getType() {
		return type;
	}
}
