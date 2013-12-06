package yooze.domain;

import java.util.ArrayList;
import java.util.List;


public class Graph {
	private String name;
	
	private final List<ClassModel> classes = new ArrayList<ClassModel>();

	public void add(ClassModel model) {
		classes.add(model);
	}

	public List<ClassModel> getChildren() {
		return classes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
