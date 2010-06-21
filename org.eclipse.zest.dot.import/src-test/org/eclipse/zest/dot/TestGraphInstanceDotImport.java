/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.dot;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.junit.Test;

/**
 * Tests for dynamic import of DOT to a Zest graph instance.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestGraphInstanceDotImport {
	private final IGraphCreator interpreter = new GraphCreatorInterpreter();

	@Test
	public void minimalUsage() {
		Shell shell = new Shell();
		/* The DOT input, can be given as a String, File or IFile: */
		DotImport dotImport = new DotImport("digraph Simple { 1;2; 1->2 }"); //$NON-NLS-1$
		/* Create a Zest graph instance in a parent, with a style: */
		Graph graph = dotImport.newGraphInstance(shell, SWT.NONE);
		// open(shell); // sets title, layout, and size, opens the shell
		System.out.println(graph);
	}

	@Test
	public void dotImport() {
		Shell shell = new Shell();
		DotImport importer = new DotImport("digraph Sample{1;2;1->2}"); //$NON-NLS-1$
		Graph graph = importer.newGraphInstance(shell, SWT.NONE);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		// open(shell);
	}

	@Test
	public void undeclaredNodes() {
		Graph graph = new DotImport("digraph{1->2;1->3}").newGraphInstance(
				new Shell(), SWT.NONE);
		Assert.assertEquals(3, graph.getNodes().size());
		Assert.assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void subgraphs() {
		Shell shell = new Shell();
		DotImport dotImport = new DotImport(
				"digraph{subgraph cluster_0{1->2}; subgraph cluster_1{1->3}; 1->4}");
		Graph graph = dotImport.newGraphInstance(shell, SWT.NONE);
		Assert.assertEquals(4 /* TODO: 4 nodes, 2 containers */, graph.getNodes()
				.size());
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void digraphType() {
		Shell shell = new Shell();
		Graph graph = interpreter.create(shell, SWT.NONE,
				parse("digraph Sample{1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		// open(shell);
	}

	@Test
	public void graphType() {
		Shell shell = new Shell();
		Graph graph = interpreter.create(shell, SWT.NONE,
				parse("graph Sample{1;2;1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertNotSame(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		// open(shell);

	}

	@Test
	public void nodeDefaultLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void nodeCount() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
	}

	@Test
	public void edgeCount() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2;1->2;2->2;1->1}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void nodeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1[label=\"Node1\"];}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void edgeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2;1->2[label=\"Edge1\"]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void edgeStyle() {
		Shell parent = new Shell();
		Graph graph = interpreter.create(parent, SWT.NONE,
				parse("graph Sample{1;2;1->2[style=dashed]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SWT.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
		// open(parent);
	}

	@Test
	public void globalEdgeStyle() {
		Shell parent = new Shell();
		Graph graph = interpreter.create(parent, SWT.NONE,
				parse("graph Sample{edge[style=dashed];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SWT.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
		// open(parent);
	}

	@Test
	public void globalEdgeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{edge[label=\"Edge1\"];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void globalNodeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{node[label=\"Node1\"];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void layoutSpring() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=spring];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SpringLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutGrid() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=grid];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(GridLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutRadial() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=radial];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(RadialLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutTree() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=tree];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutHorizontalTreeViaLayout() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=htree];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(HorizontalTreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutHorizontalTreeViaAttribute() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{rankdir=LR;1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(HorizontalTreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyLayout() {
		interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=cool];1;}")); //$NON-NLS-1$
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyStyle() {
		interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2;1->2[style=\"dashed++\"]}")); //$NON-NLS-1$
	}

	/**
	 * Import instance by interpreting the parsed AST.
	 */
	@Test
	public void viaInterpreter() {
		test(interpreter);

	}

	/**
	 * EXPERIMENTAL - NOT WORKING Import instance by compiling the generated
	 * Zest graph using the Java Compiler API (downside: requires Java 6)
	 */
	// @Test
	// public void viaJavaCompilerApi() {
	/*
	 * Implementation using Java compiler API requires Java 6.
	 */
	// test(new GraphCreatorViaJavaCompilerApi());
	// }

	/**
	 * EXPERIMENTAL - NOT WORKING Import instance by compiling the generated
	 * Zest graph using the Eclipse JDT compiler (API is internal, probably
	 * makes no sense to use this)
	 */
	// @Test
	// public void viaInternalJdtCompiler() {
	/*
	 * Implementation using the restricted internal Eclipse compiler.
	 */
	// test(new GraphCreatorViaInternalJdtCompiler());
	//
	// }

	/**
	 * Test importing a DOT graph to a Zest graph instance directly. Internally,
	 * this compiles the generated Zest class and loads it using Reflection.
	 */
	static void test(final IGraphCreator converter) {

		/*
		 * This is not really working, it only appears to be, as the generated
		 * file will be compiled by the IDE and will then be available to the
		 * classloader. A symptom of this is that it will only work starting
		 * with the second run.
		 */
		Shell shell = new Shell();
		DotAst dot1 = parse("digraph TestingGraph {1;2;3;4; 1->2;2->3;2->4}"); //$NON-NLS-1$
		Graph graph = converter.create(shell, SWT.NONE, dot1);
		Assert.assertNotNull("Created graph must exist!", graph); //$NON-NLS-1$
		// open(shell); // blocks UI when running tests
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
		System.out.println(String.format(
				"Imported '%s' to Graph '%s' of type '%s'", dot1, graph, graph //$NON-NLS-1$
						.getClass().getSimpleName()));

		/*
		 * Check a unique name works, i.e. no existing classes on the classpath
		 * could be used instead of the new one:
		 */
		DotAst dot2 = parse("digraph TestingGraph" + System.currentTimeMillis() //$NON-NLS-1$
				+ "{1;2;3;4; 1->2;2->3;2->4}"); //$NON-NLS-1$
		graph = converter.create(shell, SWT.NONE, dot2);
		Assert.assertNotNull("Created graph must exist!", graph); //$NON-NLS-1$
		// open(shell); // blocks UI when running tests
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
		System.out.println(String.format(
				"Imported '%s' to Graph '%s' of type '%s'", dot2, graph, graph //$NON-NLS-1$
						.getClass().getSimpleName()));

		/*
		 * Check if a DOT graph with the same name is changed when the generated
		 * class is loaded:
		 */
		DotAst dot3 = parse("digraph TestingGraph{1;2;3 1->2;2->3}"); //$NON-NLS-1$
		graph = converter.create(shell, SWT.NONE, dot3);
		Assert.assertNotNull("Created graph must exist!", graph); //$NON-NLS-1$
		Assert.assertEquals(3, graph.getNodes().size());
		Assert.assertEquals(2, graph.getConnections().size());
		System.out.println(String.format(
				"Imported '%s' to Graph '%s' of type '%s'", dot3, graph, graph //$NON-NLS-1$
						.getClass().getSimpleName()));
		// open(shell); // blocks UI when running tests
	}

	private static DotAst parse(String dot) {
		return new DotImport(dot).getDotAst();
	}

	static void open(final Shell shell) {
		shell.setText("Testing"); //$NON-NLS-1$
		shell.setLayout(new FillLayout());
		shell.setSize(200, 250);
		shell.open();
		while (!shell.isDisposed()) {
			while (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}
}
