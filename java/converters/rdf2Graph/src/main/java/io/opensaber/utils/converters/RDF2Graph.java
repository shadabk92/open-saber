package io.opensaber.utils.converters;

import java.util.Optional;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleBNode;
import org.eclipse.rdf4j.model.util.Models;

//import org.eclipse.rdf4j.model.util.ModelBuilder;

public final class RDF2Graph 
{
	private RDF2Graph() {}
	
	public static Graph convertRDFStatement2Graph(Statement rdfStatement, Graph graph) {
		Value subjectValue = rdfStatement.getSubject();
		IRI property = rdfStatement.getPredicate();
		Value objectValue = rdfStatement.getObject();
		updateGraph(subjectValue, property, objectValue, graph);
		return graph;
	}

	private static void updateGraph(Value subjectValue, IRI property, Value objectValue, Graph graph) {
		GraphTraversalSource t = graph.traversal();
		GraphTraversal<Vertex, Vertex> hasLabel = t.V().hasLabel(subjectValue.toString());
		Vertex s;
		if(hasLabel.hasNext()){
			s = hasLabel.next();
		} else {
			s = graph.addVertex(
					T.label,
					subjectValue.toString());
		}
		if (objectValue instanceof Literal) {
			Literal literal = (Literal)objectValue;
			s.property(property.toString(), literal.getLabel());

		} else if (objectValue instanceof IRI) {
			IRI objectIRI = (IRI)objectValue;
			Vertex o = graph.addVertex(
					T.label,
					objectIRI.toString());
			s.addEdge(property.toString(), o);
		} else if (objectValue instanceof BNode) {
			BNode objectBNode = (BNode)objectValue;
			Vertex o = graph.addVertex(
					T.label,
					objectBNode.toString());
			s.addEdge(property.toString(), o);
		}
	}
}
