package minijava.intermediate;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import util.TypeChange;

import minijava.intermediate.InterferenceGraph.Node;
import minijava.intermediate.InterferenceGraph.Reference;

/**
 * Implements the graph coloring method to optimize register allocation.
 * @author Xie Jiaye
 */
public class GraphColoringMethod {

	/**
	 * Reallocate registers in targetCode with registers 0 ~ k-1. 
	 * Registers 0 ~ reserved-1 are reserved for special use. 
	 * @param targetCode
	 * @param k
	 * @param reserved
	 * @return Maximum number of registers used after the re-allocation. If anything
	 * goes wrong, -1 is returned. 
	 */
	public static int reallocateRegister(TargetCode targetCode, int k, int reserved) {
		
		try {
			FlowGraph.prepareLivelinessCheck();
			GraphColoringMethod method = new GraphColoringMethod(targetCode, k, reserved);
			return method.colorGraph();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Code context.
	 */
	TargetCode targetCode;
	/**
	 * Graph structure based on liveliness analysis.
	 */
	InterferenceGraph graph;
	/**
	 * Working stack when executing the graph coloring method.
	 */
	Stack<Node> stack;
	/**
	 * Maximum number of registers to be allocated.
	 */
	int k;
	/**
	 * Number of registers to be reserved. i.e. 0 ~ reserved-1 are not for allocation.
	 */
	int reserved;
	
	/**
	 * Initialization that determines the code context of the allocation, and
	 * register numbers to be allocated and reserved.
	 * @param targetCode
	 * @param k
	 * @param reserved
	 */
	GraphColoringMethod(TargetCode targetCode, int k, int reserved) {
		this.targetCode = targetCode;
		this.k = k;
		this.reserved = reserved;
	}
	
	/**
	 * Executes the graph coloring method.
	 * @return Maximum number of registers used after the re-allocation. If anything
	 * goes wrong, -1 is returned. 
	 */
	private int colorGraph() {

		// Step #1:
		// initialize graph
		graph = new InterferenceGraph(targetCode);
					
		// Step #2:
		// clear stack
		stack = new Stack<Node>();
					
		// Step #3:
		try {
			return removeSmallDegreeNodes();
		} catch (NoSolutionException e) {
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Removes all the nodes whose degrees are below k. Go on to merge nodes
	 * or to color nodes and return the result.
	 * @return Maximum number of registers used after the re-allocation.
	 * @throws Exception
	 */
	private int removeSmallDegreeNodes() throws Exception {
		// Step #3:
		// remove nodes whose degrees are below k (and related edges);
		// push them into stack;
		// repeat until there are no such nodes
		
		while (true) {
			Set<Node> removed = new HashSet<Node>();
			for (Node node : graph.nodes) {
				if (node.degree() < k) {
					stack.push(node);
					removed.add(node);
				}
			}
			if (removed.isEmpty())
				break;
			for (Node node : removed) {
				graph.remove(node);
			}
		}
		
		// if graph is empty, go Step #5 (coloring);
		// else go to Step #4 (merging)
		if (graph.nodes.isEmpty()) {
			return color();
		} else {
			return merge();
		}
	}
	
	/**
	 * Find all mergeable nodes and merge them.
	 * @return
	 * @throws Exception If no mergeable nodes are found,
	 * it's not possible to do the allocation.
	 */
	private int merge() throws Exception {
		// Step #4:
		Set<Node[]> toMerge;
		
		// 1st type of mergeable nodes:
		// n0 doesn't neighbor n1
		// && n0.neighbors <= n1.neighbors
		toMerge = new HashSet<Node[]>();
		for (Node n0 : graph.nodes) {
			for (Node n1 : graph.nodes) if (n0 != n1) {
				if (n0.neighbors.contains(n1)) continue;
				if (n1.neighbors.containsAll(n0.neighbors)) {
					toMerge.add(new Node[]{n0, n1});
				}
			}
		}
		if (!toMerge.isEmpty()) {
			for (Node[] n : toMerge) {
				graph.merge(n[0], n[1]);
			}
			// goto Step #3 again
			return removeSmallDegreeNodes();
		}
		
		// 2nd type of mergeable nodes:
		// n0 doesn't neighbor n1 
		// && n0.neighbors*n1.neighbors != empty
		// && !(n0.neighbors <= n1.neighbors)
		// && !(n1.neighbors <= n0.neighbors)
		toMerge = new HashSet<Node[]>();
		for (Node n0 : graph.nodes) {
			for (Node n1 : graph.nodes) if (n0 != n1) {
				if (n0.neighbors.contains(n1)) continue;
				Set<Node> tmp = new HashSet<Node>();
				tmp.addAll(n0.neighbors);
				tmp.removeAll(n1.neighbors);
				if (tmp.size() == n0.neighbors.size()) continue;
				toMerge.add(new Node[]{n0, n1});
			}
		}
		if (!toMerge.isEmpty()) {
			for (Node[] n : toMerge) {
				graph.merge(n[0], n[1]);
			}
			// goto Step #3 again
			return removeSmallDegreeNodes();
		}
		
		// No 1st or 2nd type of mergable nodes;
		// cannot color graph with k colors;
		throw new NoSolutionException();
	}
	
	/**
	 * Allocate register numbers for every node.
	 * @return Maximum number of registers used after the re-allocation.
	 */
	private int color() {
		// Step #5:
		// pop and color nodes in stack
		
		Set<Node> colored = new HashSet<Node>();
		Set<Node> uncolored = new HashSet<Node>();
		
		while (!stack.isEmpty()) {
			uncolored.add(stack.pop());
		}
		
		int max = 0;
		
		for (Node node : uncolored) if (node.fixed != null) {
			node.reg = TypeChange.parseInt(node.fixed);
			colored.add(node);
			if (node.reg > max) max = node.reg;
		}
		uncolored.removeAll(colored);
		
		for (Node node : uncolored) {
			boolean used[] = new boolean[k];
			for (int i = 0; i < k; i++) used[i] = false;
			for (Node neighbor : node.initNeighbors) {
				if (neighbor.reg > -1) {
					used[neighbor.reg] = true;
				}
			}
			int color = reserved;
			while (used[color]) color++;
			if (color > max) max = color;
			node.reg = color;
			colored.add(node);
		}
		uncolored.removeAll(colored);
		
		// apply register-allocation
		for (Node node : colored) {
			for (Reference ref : node.references) if (!ref.paramKey.equals("range")) {
				targetCode.getCodeByLineNumber(ref.lineNumber)
					.parameterMap.put(ref.paramKey, String.valueOf(node.reg));
			}
		}
		
		return max+1;
	}
	
	class NoSolutionException extends Exception {
		private static final long serialVersionUID = -8786963737094180001L;
	}
}