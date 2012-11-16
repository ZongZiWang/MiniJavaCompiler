package minijava.intermediate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import util.TypeChange;

import minijava.binary.ISA;

/**
 * Graph structure set up for {@link GraphColoringMethod}.
 * @author Xie Jiaye
 */
public class InterferenceGraph {
	
	/**
	 * All the nodes in this graph.
	 */
	Set<Node> nodes = new HashSet<Node>();
	
	/**
	 * Initialize the graph using variable liveliness analysis. A new node is
	 * created for each variable. Variables that are lively at the same time
	 * (at least once) lead to an edge between their corresponding nodes. 
	 * @param targetCode	Code context for variables to study.
	 */
	InterferenceGraph(TargetCode targetCode) {
		int nCode = targetCode.codeSize();
		
		// variable -> places of usage and definitions
		Map<String, Set<Reference>> varRefMap = new HashMap<String, Set<Reference>>();
		for (int i = 0; i < nCode; i++) {
			// collect all nodes
			Code code = targetCode.getCode(i);
			String dstKey[] = ISA.getDst(code);
			for (String key : dstKey) {
				String var = code.getParamValue(key);
				if (!TypeChange.isInt(var))
					continue;
				if (varRefMap.containsKey(var)) {
					varRefMap.get(var).add(new Reference(code.lineNumber, key));
				} else {
					Set<Reference> refs = new HashSet<Reference>();
					refs.add(new Reference(code.lineNumber, key));
					varRefMap.put(var, refs);
				}
			}
			
			String srcKey[] = ISA.getSrc(code);
			if (srcKey.length == 3 && srcKey[0].equals("range")) {
				int start = TypeChange.parseInt(code.getParamValue(srcKey[1]));
				int cnt = TypeChange.parseInt(code.getParamValue(srcKey[2]));
				for (int reg = start; reg < start+cnt; reg++) {
					String var = String.valueOf(reg);
					if (varRefMap.containsKey(var)) {
						varRefMap.get(var).add(new Reference(code.lineNumber, "range"));
					} else {
						Set<Reference> refs = new HashSet<Reference>();
						refs.add(new Reference(code.lineNumber, "range"));
						varRefMap.put(var, refs);
					}
				}
				
			} else {
				for (String key : srcKey) {
					String var = code.getParamValue(key);
					if (!TypeChange.isInt(var))
						continue;
					if (varRefMap.containsKey(var)) {
						varRefMap.get(var).add(new Reference(code.lineNumber, key));
					} else {
						Set<Reference> refs = new HashSet<Reference>();
						refs.add(new Reference(code.lineNumber, key));
						varRefMap.put(var, refs);
					}
				}
			}
		}
		// transfer string to nodes
		for (Entry<String, Set<Reference>> entry : varRefMap.entrySet()) {
			String var = entry.getKey();
			Node node = new Node();
			node.variables.add(var);
			node.references.addAll(entry.getValue());
			for (Reference ref : node.references) {
				if (ref.paramKey.equals("range")) {
					node.fixed = var;
					break;
				}
			}
			nodes.add(node);
		}
		for (int i = 0; i < nCode; i++) {
			Code code = targetCode.getCode(i);
			
			Set<Node> active = new HashSet<Node>();
			for (Node node : nodes) for (String var : node.variables) {
				if (FlowGraph.isVarActive(targetCode, code.lineNumber, var)) {
					active.add(node);
				}
			}
			for (Node a : active) {
				for (Node b : active) if (a != b){
					a.neighbors.add(b);
					a.initNeighbors.add(b);
					b.neighbors.add(a);
					b.initNeighbors.add(a);
				}
			}
		}
		/*
		System.out.println("--- Initial Nodes ---");
		for (Node n : nodes) {
			String var = n+"; neighbors:[";
			for (Node nb : n.initNeighbors) {
				var += nb+",";
			}
			var += "]";
			System.out.println(var);
		}
		System.out.println("--- End Initial Nodes ---");
		*/
	}
	
	/**
	 * Removes a node and its neighbor relationships from the graph.
	 * @param node
	 */
	void remove(Node node) {
		for (Node neighbor : node.neighbors) {
			neighbor.neighbors.remove(node);
		}
		nodes.remove(node);
	}
	
	/**
	 * Merges two nodes into one. Actually it's done by inserting a new node that
	 * combines the two nodes' information into the graph, and then removing
	 * the two old nodes. 
	 * @param n0
	 * @param n1
	 * @throws Exception
	 */
	void merge(Node n0, Node n1) throws Exception {
		if (nodes.contains(n0) && nodes.contains(n1)) {
			Node newNode = new Node();
			newNode.variables.addAll(n0.variables);
			newNode.variables.addAll(n1.variables);
			newNode.neighbors.addAll(n0.neighbors);
			newNode.neighbors.addAll(n1.neighbors);
			newNode.initNeighbors.addAll(n0.initNeighbors);
			newNode.initNeighbors.addAll(n1.initNeighbors);
			newNode.references.addAll(n0.references);
			newNode.references.addAll(n1.references);
			for (Node neighbor : newNode.neighbors) {
				neighbor.neighbors.add(newNode);
			}
			int flag = 0;
			if (n0.fixed != null) flag |= 1;
			if (n1.fixed != null) flag |= 2;
			switch (flag) {
			case 0:
				break;
			case 1:
				newNode.fixed = n0.fixed;
				break;
			case 2:
				newNode.fixed = n1.fixed;
				break;
			case 3:
				throw new Exception("Fixed nodes cannot merge!");
			}
			remove(n0);
			remove(n1);
			nodes.add(newNode);
		}
	}

	/**
	 * Node structure in the graph.
	 * @author Xie Jiaye
	 */
	class Node {
		/**
		 * Variables that this node stands for. When initialized, it contains one
		 * variable. Merged when nodes merge.
		 */
		Set<String> variables = new HashSet<String>();
		/**
		 * Neighbor nodes that have variables that are not mergable to this node's
		 * variables according to liveliness check. Elements may be deleted when 
		 * nodes are removed. Merged when nodes merge.
		 */
		Set<Node> neighbors = new HashSet<Node>();
		/**
		 * A unremovable backup of {@link #neighbors}. i.e. this set is manipulated
		 * almost the same way as {@link #neighbors} except that no nodes can be
		 * removed from it.
		 */
		Set<Node> initNeighbors = new HashSet<Node>();
		/**
		 * Number of register allocated for this node. Initial value is -1.
		 */
		int reg = -1;
		/**
		 * Special flag set up for the instruction invoke-range.
		 * If this node contains a variable that is a parameter of invoke-range,
		 * this node's register number should not be modified, so this field should
		 * carry the node's initial register. Otherwise this field is <b>null</b>.
		 */
		String fixed = null;
		
		/**
		 * Set of all {@link Reference} that use or define variables in this node.
		 */
		Set<Reference> references = new HashSet<Reference>();
		
		/**
		 * @return This node's degree in current graph.
		 */
		int degree() {
			return neighbors.size();
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof Node && ((Node)obj).variables.equals(variables));
		}
		
		@Override
		public String toString() {
			return variables.toString();
		}
	}
	
	/**
	 * Records the places where a variable of this node is used or defined.
	 * @author Xie Jiaye
	 */
	class Reference {
		int lineNumber;
		String paramKey;
		Reference(int lineNumber, String paramKey) {
			this.lineNumber = lineNumber;
			this.paramKey = paramKey;
		}
	}
}
