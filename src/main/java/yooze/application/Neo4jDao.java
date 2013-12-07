package yooze.application;

import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import yooze.domain.ClassModel;

public class Neo4jDao {
	private GraphDatabaseService graphDb;
	private Node rootNode;
	private ConcurrentHashMap<String, Node> nodes = new ConcurrentHashMap<String, Node>();

	public enum MyRelationshipTypes implements RelationshipType {
		CONTAINS, REFERENCES
	}

	public Neo4jDao(String database) {
		graphDb = new EmbeddedGraphDatabase(database);
		Transaction tx = graphDb.beginTx();
		try {
			rootNode = graphDb.createNode();
			rootNode.setProperty("name", "root");
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public void insertClass(ClassModel classModel) {
		Transaction tx = graphDb.beginTx();
		try {
			Node newNode = findOrCreateNode(classModel);

			for (ClassModel ref : classModel.getReferences()) {
				Node refNode = findOrCreateNode(ref);
				newNode.createRelationshipTo(refNode,
						MyRelationshipTypes.REFERENCES);
			}
			tx.success();
		} finally {
			tx.finish();
		}
	}

	private Node findOrCreateNode(ClassModel classModel) {
		Node newNode = findNode(classModel.getName());
		if (newNode == null) {
			Transaction tx = graphDb.beginTx();
			try {
				newNode = graphDb.createNode();
				newNode.setProperty("name", classModel.getName());
				
				tx.success();
			} finally {
				tx.finish();
			}
		}
		return newNode;
	}

	public Node findNode(String className) {
		Node node = nodes.get(className);
		if (node != null) {
			return node;
		}
		// do "tablescan". is there a better way?
		Traverser classesTraverser = rootNode.traverse(
				Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
				ReturnableEvaluator.ALL_BUT_START_NODE,
				MyRelationshipTypes.CONTAINS, Direction.OUTGOING);
		for (Node nextNode : classesTraverser) {
			if (nextNode.getProperty("name").equals(className)) {
				nodes.put(className, nextNode);
				return nextNode;
			}
		}
		return null;// not found

	}
}
