package yooze.output;

import java.io.OutputStream;

import yooze.domain.ClassModel;
import yooze.domain.Graph;

public class JsonPrinter extends GraphPrinter {

	public JsonPrinter(OutputStream out) {
		super(out);
	}

	@Override
	public void print(Graph g) {
		print("[");
		for (ClassModel cm : g.getChildren()) {
			printClassModel(cm);
		}
		print("]");
	}

	private void printClassModel(ClassModel cm) {
		for (ClassModel ref : cm.getReferences()) {
			String relation = String.format("{\"key\":\"%s\",\"parent\":\"%s\"},", ref.getName(), cm.getName());
			if (shouldPrint(relation)) {
				print(relation);
				markAsPrinted(relation);
				printClassModel(ref); // recurse
			}
		}
	}

}
