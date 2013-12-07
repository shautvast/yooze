package yooze.output;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import yooze.domain.Graph;

/**
 * Facilitates subclasses to take care of circular dependencies, using shouldPrint and markAsPrinted
 * 
 */
public abstract class GraphPrinter extends PrintStream {

	private ArrayList<String> printedRelations;

	public GraphPrinter(OutputStream out) {
		super(out);
		printedRelations = new ArrayList<String>();
	}

	abstract public void print(Graph g);

	protected boolean shouldPrint(String relation) {
		return !printedRelations.contains(relation);
	}

	protected void markAsPrinted(String relation) {
		printedRelations.add(relation);
	}

	@SuppressWarnings("serial")
	protected static class CouldNotPrint extends RuntimeException {
		public CouldNotPrint(Throwable t) {
			super(t);
		}
	}
}
