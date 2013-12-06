package yooze.domain;

import yooze.ClassCache;
import javassist.CtClass;

public class ParameterModel {
	private ClassModel type;
	private String name;

	public ParameterModel(CtClass typeAsCtClass) {
		String classname = typeAsCtClass.getName();
		type = ClassCache.get(classname);
		if (type == null) {
			type = ClassCache.createNewDummyModel(classname);
		}
		name = "";// javassist doesn't give me this (?)
	}

	public ClassModel getType() {
		return type;
	}
}
