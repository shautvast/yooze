package yooze.output;

import java.io.OutputStream;

import yooze.Statistics;
import yooze.domain.ClassModel;
import yooze.domain.Graph;

/**
 * prints the graph as a graphviz dot file. Takes care of circular dependencies. Not threadsafe.
 */
public class DotPrinter extends GraphPrinter {

	public DotPrinter(OutputStream out) {
		super(out);
	}

	public void print(Graph g) {
		println("digraph \"" + g.getName() + "\" {");
		println("graph [size=100,100];");
		for (ClassModel cm : g.getChildren()) {
			printClassModel(cm);
		}
		println("}");
	}

	private void printClassModel(ClassModel cm) {
		double boxsize = Math.sqrt(Statistics.getByteCodeSizeForClass(cm.getName()));

		print("\"");
		print(cm.getName());
		println("\" [shape=box, height=" + boxsize / 20 + "];");

		if (cm.getReferences() == null || cm.getReferences().size() == 0) {
			print("\"");
			print(cm.getName());
			println("\";");
		} else {
			for (ClassModel ref : cm.getReferences()) {
				String relation = String.format("\"%s\" -> \"%s\"", cm.getName(), ref.getName());
				if (shouldPrint(relation)) {
					println(relation);
					markAsPrinted(relation);
					printClassModel(ref); // recurse
				}
			}
		}
	}
}
