package io.opensaber.utils.converters;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.eclipse.rdf4j.model.util.ModelBuilder;

import io.opensaber.utils.converters.RDF2Graph;

public class RDF2GraphTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createGraphFromTriple() throws Exception {
		Graph graph = createGraph();
		ModelBuilder modelBuilder = createSimpleRDF();
		assertEquals(0,countGraphVertices(graph));
		editGraph(graph, modelBuilder.build());
//		Test for single vertex 
		assertEquals(1,countGraphVertices(graph));
//		test for that vertex having the property expected
		GraphTraversal<Vertex, Vertex> hasP = 
				graphTraversal(graph).has(FOAF.FIRST_NAME.toString(),"Pablo");
		assertTrue(hasP.hasNext());
		hasP.next();
		assertFalse(hasP.hasNext());
		modelBuilder = modelBuilder
				.add(FOAF.BIRTHDAY, "1987-08-25");
		editGraph(graph, modelBuilder.build());
//		test that a vertex did not get added since the object is only a Literal
		assertEquals(1,countGraphVertices(graph));
		modelBuilder = modelBuilder
				.add(RDF.TYPE, "ex:Artist");
		editGraph(graph, modelBuilder.build());
//		test that a vertex got added since the object is an IRI
		assertEquals(2,countGraphVertices(graph));
		modelBuilder = modelBuilder
				.add(FOAF.DEPICTION, "ex:Image");
		editGraph(graph, modelBuilder.build());
		assertEquals(3,countGraphVertices(graph));
	}

	@Test
	public void handleDupeInTriple() throws Exception {
		Graph graph = createGraph();
		ModelBuilder modelBuilder = createSimpleRDF()
				.add(FOAF.BIRTHDAY, "1987-08-25")
				.add(FOAF.BIRTHDAY, "1987-08-25");
		editGraph(graph, modelBuilder.build());
		assertEquals(1,countGraphVertices(graph));
	}

	@Test
	public void handleBNodeInTriple() throws Exception {
		Graph graph = createGraph();
		ValueFactory vf = SimpleValueFactory.getInstance();
		BNode address = vf.createBNode();
		BNode painting = vf.createBNode();
		BNode reaction = vf.createBNode();
		ModelBuilder modelBuilder = createSimpleRDF();
		modelBuilder
			.add("ex:homeAddress", address)
			.add("ex:creatorOf", painting)
			.subject(address)
				.add("ex:street", "31 Art Gallery")
				.add("ex:city", "Madrid")
				.add("ex:country", "Spain")
				.add(RDF.TYPE,"ex:PostalAddress")
			.subject(painting)
				.add(RDF.TYPE,"ex:CreativeWork")
				.add("ex:depicts", "cubes")
				.add("ex:reaction", reaction)
				.subject(reaction)
					.add("ex:rating","5")
					.add(RDF.TYPE,"ex:AggregateRating");
		editGraph(graph, modelBuilder.build());
//		1 subject, 3 Blank Nodes and 3 object IRIs
		assertEquals(7,countGraphVertices(graph));
		GraphTraversal<Vertex, Vertex> hasP = 
				graphTraversal(graph)
					.has(T.label,"http://example.org/Picasso")
					.out("http://example.org/creatorOf")
					.out("http://example.org/reaction");
		assertTrue(hasP.hasNext());
	}

	@Test
	public void handleRecursiveBNodeInTriple() throws Exception {
	}
	

	private long countGraphVertices(Graph graph) {
		return IteratorUtils.count(graph.vertices());
	}

	private void editGraph(Graph graph, Model simpleRDFModel) {
		clearGraph(graph);
		for(Statement rdfStatement: simpleRDFModel) {
			RDF2Graph.convertRDFStatement2Graph(rdfStatement, graph);
		}
	}

	private void clearGraph(Graph graph) {
		graphTraversal(graph).drop().iterate();
	}

	private GraphTraversal<Vertex, Vertex> graphTraversal(Graph graph) {
		return graph.traversal().V();
	}

	private void dumpGraph(Graph graph) throws IOException {
		graph.io(IoCore.graphson()).writeGraph("dump.json");
	}
	
	private ModelBuilder createSimpleRDF(){
		ModelBuilder builder = new ModelBuilder();
		return builder
				.setNamespace("ex", "http://example.org/")
				.subject("ex:Picasso")
				.add(FOAF.FIRST_NAME, "Pablo");
	}
	
	private Graph createGraph(){
		Graph graph = TinkerGraph.open();
		return graph;
	}

}
