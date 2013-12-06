package yooze.etc;

import java.io.IOException;

import yooze.GraphBuilder;
import yooze.domain.ClassModel;
import yooze.domain.Graph;

public class Yooze {
	private String neo4jDb;

	public static void main(String[] args) throws IOException {
		String neoDb = args[0];
		String earfile = args[1];
		String in = args[2];
		String ex = args[2];
		String startingClassname = args[3];
		new Yooze(neoDb).createNeoGraph(earfile, in, ex, startingClassname);

	}

	public Yooze(String neo4jDb) {
		super();
		this.neo4jDb = neo4jDb;
	}

	public void createNeoGraph(String archive, String packageIncludePatterns,
			String packageExcludePatterns, String startingClass)
			throws IOException {
		GraphBuilder libDirectoryBuilder = GraphBuilder
				.getLibDirectoryBuilder();
		libDirectoryBuilder.setPackageExcludePatterns(packageExcludePatterns);
		libDirectoryBuilder.setPackageIncludePatterns(packageIncludePatterns);

		Graph graph = libDirectoryBuilder.build(archive, startingClass);

		Neo4jDao neo4jDao = new Neo4jDao(neo4jDb);
		for (ClassModel model : graph.getChildren()) {
			neo4jDao.insertClass(model);
		}
	}

}
