package yooze;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javassist.ClassPath;
import javassist.ClassPool;
import yooze.domain.ClassModel;
import yooze.domain.Graph;

/**
 * Builds a class dependency graph from given classpath. Delegates to
 * ClassModelBuilder.
 */
public class GraphBuilder {
	private Scanner scanner;
	private ClassModelBuilder classModelBuilder;

	public GraphBuilder(Scanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * Builds a graph from a give starting point(class)
	 * 
	 * @param archiveFilename
	 * @param className
	 *            the name of the class that is the starting point. Only classes
	 *            referenced from here will be included.
	 * @return a Graph containing all calculated dependencies
	 * @throws IOException
	 *             when file reading fails
	 */
	public Graph build(String archiveFilename, String className) throws IOException {
		return buildClassDepencyGraph(new File(archiveFilename), className, new IncludeDecision() {
			public boolean shouldIncludeClass(String name, String startingpointname) {
				return startingpointname == null || name.equals(startingpointname);
			}
		});
	}

	public Graph build(File archive, String className) throws IOException {
		return buildClassDepencyGraph(archive, className, new IncludeDecision() {
			public boolean shouldIncludeClass(String name, String startingpointname) {
				return name.equals(startingpointname);
			}
		});
	}

	/**
	 * Builds a graph for all classes (all included via package patterns and not
	 * excluded)
	 * 
	 * @param archiveFilename
	 * @return a Graph containing all calculated dependencies
	 * @throws IOException
	 *             when file reading fails
	 */
	public Graph build(String archiveFilename) throws IOException {
		return buildClassDepencyGraph(new File(archiveFilename), null, new IncludeDecision() {
			public boolean shouldIncludeClass(String name, String startingpointname) {
				return true;
			}
		});
	}

	private Graph buildClassDepencyGraph(File archiveFile, String className, IncludeDecision e) throws IOException {
		List<InspectableClasspath> cpList = scanner.scanArchive(archiveFile);

		ClassPool pool = ClassPool.getDefault();
		for (ClassPath cp : cpList) {
			pool.appendClassPath(cp);
		}

		Graph graph = createClassDependencyGraph(pool, cpList, className, e);
		graph.setName(archiveFile.getName());
		return graph;
	}

	private Graph createClassDependencyGraph(ClassPool pool, List<InspectableClasspath> classpath, String className,
			IncludeDecision decide) {
		Graph graph = new Graph();
		classModelBuilder.setPool(pool);

		for (InspectableClasspath lib : classpath) {
			for (String name : lib.getClasses()) {
				if (decide.shouldIncludeClass(name, className)) {
					ClassModel newModel = classModelBuilder.scanClassOrSkip(name);
					if (newModel != null) {
						graph.add(newModel);
					}
				}
			}
		}
		return graph;
	}

	interface IncludeDecision {
		boolean shouldIncludeClass(String name, String startingpointname);
	}

	public void setClassModelBuilder(ClassModelBuilder classModelBuilder) {
		this.classModelBuilder = classModelBuilder;
	}
}
