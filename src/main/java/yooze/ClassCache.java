package yooze;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import yooze.domain.ClassModel;

public class ClassCache {
	private final Map<String, ClassModel> entries = new ConcurrentHashMap<String, ClassModel>();
	private InclusionDecider inclusionDecider;

	public boolean contains(String classname) {
		return entries.containsKey(classname);
	}

	public ClassModel createNewDummyModel(String name) {
		ClassModel classModel = new ClassModel(name);
		entries.put(name, classModel);
		return classModel;
	}

	public ClassModel get(String className) {
		return entries.get(className);
	}

	public void add(String className, ClassModel model) {
		entries.put(className, model);
	}

	public List<ClassModel> values() {
		List<ClassModel> models = new ArrayList<ClassModel>();
		for (ClassModel model : entries.values()) {
			if (!inclusionDecider.shouldSkip(model.getName())) {
				models.add(model);
			}
		}
		return models;
	}

	public void clear() {
		entries.clear();
	}

	public void setInclusionDecider(InclusionDecider inclusionDecider) {
		this.inclusionDecider = inclusionDecider;
	}

}
