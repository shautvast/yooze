package yooze;

import java.util.List;

import javassist.ClassPath;

public interface InspectableClasspath extends ClassPath {
	public String getResourceName();

	public List<String> getClasses();
}
