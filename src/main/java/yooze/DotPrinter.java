package yooze;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import yooze.domain.ClassModel;
import yooze.domain.Graph;

/**
 * prints the graph as a graphviz dot file. Takes care of circular dependencies. Not threadsafe.
 */
public class DotPrinter extends PrintStream {

	private ArrayList<String> printedRelations;

	public DotPrinter(OutputStream out) {
		super(out);
	}

	public void print(Graph g) {
		printedRelations = new ArrayList<String>();
		println("digraph \"" + g.getName() + "\" {");
		println("graph [size=100,100];");
		for (ClassModel cm : g.getChildren()) {
			print(cm);
		}
		println("}");
		close();
	}

	private void print(ClassModel cm) {
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
				String relation = cm.getName() + "-" + ref.getName();
				if (!printedRelations.contains(relation)) {
					print("\"");
					print(cm.getName());
					print("\" -> \"");
					print(ref.getName());
					println("\"");
					printedRelations.add(relation);
					print(ref);
				}
			}
		}
	}

}
