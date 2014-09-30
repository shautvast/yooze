package yooze;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import yooze.domain.MethodModel;

public class MethodCache {
	private final Map<String, MethodModel> entries = new ConcurrentHashMap<String, MethodModel>();
	private final static MethodCache instance = new MethodCache();

	public static MethodCache getInstance() {
		return instance;
	}

	public boolean contains(String classname) {
		return entries.containsKey(classname);
	}

	public MethodModel get(String fullName) {
		return entries.get(fullName);
	}

	public void add(MethodModel methodmodel) {
		entries.put(methodmodel.getFullname(), methodmodel);
	}

	public Collection<MethodModel> getMethods() {
		return entries.values();
	}

	public void reset() {
		entries.clear();
	}
}
