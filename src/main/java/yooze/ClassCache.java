package yooze;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import yooze.domain.ClassModel;

public class ClassCache {
	private final static Map<String, ClassModel> entries = new ConcurrentHashMap<String, ClassModel>();

	public static boolean contains(String classname) {
		return entries.containsKey(classname);
	}

	public static ClassModel createNewDummyModel(String name) {
		ClassModel classModel = new ClassModel(name);
		entries.put(name, classModel);
		return classModel;
	}

	public static ClassModel get(String className) {
		return entries.get(className);
	}

	public static void add(String className, ClassModel model) {
		entries.put(className, model);
	}

	public static void clear() {
		entries.clear();
	}
}
