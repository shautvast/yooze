package yooze.application;

import yooze.GraphBuilder;
import yooze.scanner.ArchiveScanner;
import yooze.scanner.ClassesDirScanner;
import yooze.scanner.LibScanner;
import yooze.scanner.TgzScanner;
import yooze.scanner.WarScanner;

public class GraphBuilderFactory {
	/**
	 * Factory method for getting a builder that does earfiles
	 */
	public static GraphBuilder getEarBuilder() {
		return new GraphBuilder(new ArchiveScanner(new WarScanner()));
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
	 * Factory method for getting a builder that scans a lib directory (containing jars)
	 */
	public static GraphBuilder getLibDirectoryBuilder() {
		return new GraphBuilder(new LibScanner());
	}

	/**
	 * Factory method for getting a builder that scans a directory containing classes
	 */
	public static GraphBuilder getClassesDirectoryBuilder() {
		return new GraphBuilder(new ClassesDirScanner());
	}
}
