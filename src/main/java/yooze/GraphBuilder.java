package yooze;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javassist.ClassPath;
import javassist.ClassPool;
import yooze.domain.ClassModel;
import yooze.domain.Graph;
import yooze.scanner.ArchiveScanner;
import yooze.scanner.LibScanner;
import yooze.scanner.Scanner;
import yooze.scanner.TgzScanner;

/**
 * Builds a class dependency graph from given classpath. Delegates to
 * ClassModelBuilder.
 */
public class GraphBuilder {
	private Scanner scanner;
	private ClassModelBuilder classModelBuilder;
	private String[] packageIncludePatterns;
	private String[] packageExcludePatterns;

	/**
	 * Factory method for getting a builder that does earfiles
	 */
	public static GraphBuilder getEarBuilder() {
		return new GraphBuilder(new ArchiveScanner());
	}

	/**
	 * Factory method for getting a builder that does (downloaded) .tar.gz files
	 */
	public static GraphBuilder getDefaultTgzBuilder() {
		GraphBuilder tgzBuilder = new GraphBuilder(new TgzScanner());
		tgzBuilder.setPackageExcludePatterns("java.*", "sun.*", "com.sun.*");
		return tgzBuilder;
	}

	/**
	 * Factory method for getting a builder that scans a lib directory
	 * (containing jars)
	 */
	public static GraphBuilder getLibDirectoryBuilder() {
		return new GraphBuilder(new LibScanner());
	}

	/**
	 * Factory method for getting a builder that scans a directory containing
	 * classes
	 */
	public static GraphBuilder getClassesDirectoryBuilder() {
		return new GraphBuilder(new ClassesDirScanner());
	}

	private GraphBuilder(Scanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * primary function for this class.
	 * 
	 * @param archiveFilename
	 * @return a Graph containing all calculated dependencies
	 * @throws IOException
	 *             when file reading fails
	 */
	public Graph build(String archiveFilename, String className)
			throws IOException {
		return buildClassDepencyGraph(new File(archiveFilename), className);
	}

	Graph buildClassDepencyGraph(File archiveFile, String className)
			throws IOException {
		List<ClassPath> cpList = scanner.scanArchive(archiveFile);

		ClassPool pool = ClassPool.getDefault();
		for (ClassPath cp : cpList) {
			pool.appendClassPath(cp);
		}

		Graph graph = createClassDependencyGraph(pool, cpList, className);
		graph.setName(archiveFile.getName());
		return graph;
	}

	private Graph createClassDependencyGraph(ClassPool pool,
			List<ClassPath> classpath, String className) {
		Graph graph = new Graph();
		classModelBuilder = new ClassModelBuilder(pool);
		classModelBuilder.setPackageExcludePatterns(packageExcludePatterns);
		classModelBuilder.setPackageIncludePatterns(packageIncludePatterns);
		for (ClassPath lib : classpath) {
			assert (lib instanceof Inspectable);

			List<String> classes = ((Inspectable) lib).getClasses();
			for (String name : classes) {
				if (name.equals(className)) {
					ClassModel newModel = classModelBuilder
							.scanClassOrSkip(className);
					if (newModel != null) {
						graph.add(newModel);
					}
				}
			}
		}
		return graph;
	}

	public void setPackageIncludePatterns(String... packageIncludePatterns) {
		this.packageIncludePatterns = packageIncludePatterns;
	}

	public void setPackageExcludePatterns(String... packageExcludePatterns) {
		this.packageExcludePatterns = packageExcludePatterns;
	}

}
