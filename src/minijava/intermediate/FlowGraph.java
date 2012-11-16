package minijava.intermediate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Mode;
import util.TypeChange;

import minijava.binary.DualExp;
import minijava.binary.ISA;
import minijava.binary.SingleAssignment;

/**
 * Contains static methods that analyze the intermediate code based on the flow graph
 * of the code structure.
 * @author Xie Jiaye
 */
public class FlowGraph {
	
	/**
	 * Maps a TargetCode instance to a map that maps CodeBlock's starting line
	 * to the CodeBlock itself. <br />
	 * Just for the convenience of CodeBlock queries.
	 */
	private static Map<TargetCode, Map<Integer, CodeBlock>> codeBlocks;
	
	/**
	 * Maps a TargetCode to the set of Loops within this TargetCode.
	 */
	private static Map<TargetCode, Set<Loop>> loops;	

	/**
	 * Thrown when something goes wrong during the analysis of CodeBlocks. <br />
	 * This should not happen if the algorithm is correct.
	 * @author Xie Jiaye
	 */
	static class BlockAnalysisException extends Exception {
		private static final long serialVersionUID = -2285031341312368455L;

		BlockAnalysisException(String msg){
			super(msg);
		}
	}
	
	/**
	 * Goes over every instance of TargetCode, i.e. intermediate code of every method,
	 * and cuts each method into basic code blocks, also recording the way they're
	 * linked to each other. The result is stored in {@link #codeBlocks}.
	 * @throws BlockAnalysisException
	 */
	private static void analyzeCodeBlock() throws BlockAnalysisException {
		
		codeBlocks = new HashMap<TargetCode, Map<Integer, CodeBlock>>();
		
		if (Mode.isDebugMode()) System.out.println("--- Analyzing Code Block ---");
		
		int nMethods = TargetCode.methods.size();
		for (int i = 0; i < nMethods; i++) {
			// analyze code block of method No. i
			TargetCode methodCode = TargetCode.getMethodTargetCode(i);
			int codeNum = methodCode.codeSize();
			
			// line number -> code block starting at this line
			Map<Integer, CodeBlock> blocks = new HashMap<Integer, CodeBlock>();
			// first line number of block 1 -> first line number of block 2
			// where block 1 may flow to block 2
			MultiMap<Integer, Integer> edges = new MultiMap<Integer, Integer>();
			List<Integer> startingLine = new ArrayList<Integer>();
			startingLine.add(0);
			List<Integer> codeLine = new ArrayList<Integer>();
			
			int totalCodeLength = 0;
			for (int j = 0; j < codeNum; j++) {
				Code code = methodCode.getCode(j);
				totalCodeLength += ISA.getLength(code.codeOp);
				codeLine.add(code.lineNumber);
				int dest;
				switch (code.codeOp) {
				case 0x28:	// goto +AA
					dest = code.lineNumber+2*TypeChange.parseInt(code.getParamValue("AA"));
					startingLine.add(dest);
					edges.put(code.lineNumber, dest);
					
					dest = code.lineNumber+ISA.getLength(code.codeOp);
					startingLine.add(dest);
					break;
				case 0x29:	// goto/16 +AAAA
					dest = code.lineNumber+2*TypeChange.parseInt(code.getParamValue("AAAA"));
					startingLine.add(dest);
					edges.put(code.lineNumber, dest);
					dest = code.lineNumber+ISA.getLength(code.codeOp);
					startingLine.add(dest);
					break;
				case 0x2a:	// goto/32 +AAAAAAAA
					dest = code.lineNumber+2*TypeChange.parseInt(code.getParamValue("AAAAAAAA"));
					startingLine.add(dest);
					edges.put(code.lineNumber, dest);
					dest = code.lineNumber+ISA.getLength(code.codeOp);
					startingLine.add(dest);
					break;
					// if-test vA, vB, +CCCC
				case 0x32:	// if-eq 
				case 0x33:	// if-ne
				case 0x34:	// if-lt
				case 0x35:	// if-ge
				case 0x36:	// if-gt
				case 0x37:	// if-le
					dest = code.lineNumber+ISA.getLength(code.codeOp);
					startingLine.add(dest);
					edges.put(code.lineNumber, dest);
					dest = code.lineNumber+2*TypeChange.parseInt(code.getParamValue("CCCC"));
					startingLine.add(dest);
					edges.put(code.lineNumber, dest);
					break;
					// if-testz vAA, +BBBB
				case 0x38:	// if-eqz
				case 0x39:	// if-nez
				case 0x3a:	// if-ltz
				case 0x3b:	// if-gez
				case 0x3c:	// if-gtz
				case 0x3d:	// if-lez 
					dest = code.lineNumber+ISA.getLength(code.codeOp);
					startingLine.add(dest);
					edges.put(code.lineNumber, dest);
					dest = code.lineNumber+2*TypeChange.parseInt(code.getParamValue("BBBB"));
					startingLine.add(dest);
					edges.put(code.lineNumber, dest);
					break;
				default:
					break;
				}
			}
			startingLine.add(totalCodeLength);
			Collections.sort(startingLine);
			int p = 0, q = 0;
			for (; p < startingLine.size()-1; p++) {
				int firstLine = startingLine.get(p);
				for (; q < codeLine.size()-1 && codeLine.get(q+1) < startingLine.get(p+1); q++);
				int lastLine = codeLine.get(q);
				blocks.put(firstLine, new CodeBlock(methodCode, firstLine, lastLine));
			}
			for (CodeBlock block : blocks.values()) {
				if (edges.containsKey(block.lastLine)) {
					for (int destLine : edges.get(block.lastLine)) {
						if (!blocks.containsKey(destLine)) {
							throw new BlockAnalysisException
								("No corresponding CodeBlock at line "+destLine);
						} else {
							CodeBlock destBlock = blocks.get(destLine);
							CodeBlock.flow(block, destBlock);
						}
					}
				}
			}
			
			for (CodeBlock block : blocks.values()) {
				if (block.next.isEmpty()) {
					// flow naturally to next CodeBlock
					int r = 0;
					for (; r < codeLine.size() && codeLine.get(r) <= block.lastLine; r++);
					if (r < codeLine.size()) {
						// not last CodeBlock
						int destLine = codeLine.get(r);
						if (!blocks.containsKey(destLine)) {
							throw new BlockAnalysisException
								("No corresponding CodeBlock at line "+destLine);
						} else {
							CodeBlock destBlock = blocks.get(destLine);
							CodeBlock.flow(block, destBlock);
						}	
					}
				}
			}
			
			codeBlocks.put(methodCode, blocks);
			
			if (Mode.isDebugMode()) {
				System.out.println("Method #"+i+":");
				System.out.println(blocks.values());
			}
		}
		if (Mode.isDebugMode()) System.out.println("--- Analyzing finished ---");
	}
	
	/**
	 * Examines and finds all the Loops in every instance of TargetCode. The result is
	 * stored in {@link #loops}. 
	 */
	private static void findLoops() {
		
		if (Mode.isDebugMode()) System.out.println("--- Finding Loops ---");
		
		loops = new HashMap<TargetCode, Set<Loop>>();
		
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode methodCode  = TargetCode.getMethodTargetCode(i);
			Map<Integer, CodeBlock> blockMap = codeBlocks.get(methodCode);
			Set<Loop> loopSet = new HashSet<Loop>();
			
			// ===== calculate dominate blocks for each block =====
			
			// for each block n : D(n) = N; whereas N = {n0, n1, .. nk}
			Collection<CodeBlock> blocks = blockMap.values();
			for (CodeBlock block : blocks) {
				block.DOM.clear();
				block.DOM.addAll(blocks);
			}
			
			// D(n0) = {n0};
			CodeBlock firstBlock = blockMap.get(0);
			if (firstBlock == null) return;
			firstBlock.DOM.clear();
			firstBlock.DOM.add(firstBlock);
			
			boolean change = true;
			while (change) {
				change = false;
				for (CodeBlock block : blocks) if (block != firstBlock) {
					Set<CodeBlock> oldDom = new HashSet<CodeBlock>();
					oldDom.addAll(block.DOM);
					// D(ni) = {ni} + D(p0)*D(p1)*...*D(pj), p0..pj in P(ni)
					block.DOM.clear();
					block.DOM.addAll(blocks);
					for (CodeBlock p : block.pre) {
						Set<CodeBlock> removed = new HashSet<CodeBlock>();
						for (CodeBlock b : block.DOM) {
							if (!p.DOM.contains(b)) {
								removed.add(b);
							}
						}
						block.DOM.removeAll(removed);
					}
					block.DOM.add(block);
					if (!oldDom.equals(block.DOM)) {
						change = true;
					}						
				}
			}
			
			// ===== dominate blocks calculation end =====
			
			// ===== search for backward edges =====
			
			Set<Edge> backwardEdges = new HashSet<Edge>();
			for (CodeBlock block : blocks) {
				for (CodeBlock n : block.next) {
					if (block.DOM.contains(n)) {
						backwardEdges.add(new Edge(block, n));
					}
				}
			}
			
			// ===== backward edges search end =====
			
			// ===== calculate loops with backward edges =====

			for (Edge edge : backwardEdges) {
				loopSet.add(new Loop(methodCode, edge));
			}
			
			// ===== loop calculation end =====
			
			if (Mode.isDebugMode()) {
				System.out.println("--- Loops Start ---");
				for (Loop loop : loopSet) {
					System.out.println(loop);
				}
				System.out.println("--- Loops End ---");
			}
			
			loops.put(methodCode, loopSet);
		}
		
		if (Mode.isDebugMode()) System.out.println("--- Loops Found ---");
	}
	
	/**
	 * Includes all the steps to do the loop optimization. 
	 * @return	<b>true</b> if any optimization has been done; <b>false</b> other wise. 
	 * @throws BlockAnalysisException
	 */
	static boolean optimizeLoops() throws BlockAnalysisException {
		
		analyzeCodeBlock();
		analyzeUDDataStream();
		analyzeDUDataStream();
		analyzeDualExpression();
		findLoops();
		
		// ====== export loop constants start ======
		
		boolean exported = false;
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			Collection<Loop> loopSet = loops.get(TargetCode.getMethodTargetCode(i));
			
			for (Loop loop : loopSet) {
				loop.calcLoopConstants();
				loop.checkExportableLoopConstants();
				exported = exported || loop.exportLoopConstants();
			}
		}
		
		// ====== export loop constants end =====
		
		return exported;
	}
	
	/**
	 * Analyzes use-def chain in all the TargetCodes.
	 */
	private static void analyzeUDDataStream() {
		
		if (Mode.isDebugMode()) System.out.println("--- Analyzing UD Data Stream ---");
		
		// analyze data stream by method
		for (Map<Integer, CodeBlock> blocks : codeBlocks.values()) {
			Collection<CodeBlock> blockSet = blocks.values();
			
			// initialize definition points and GEN set within single block
			for (CodeBlock block : blockSet) {
				block.calcPoints();
				block.calcGen();
				block.KILL.clear();
				block.IN.clear();
				block.OUT.addAll(block.GEN);
			}
			
			// initialize KILL set with inter-block comparison
			for (CodeBlock blockConcerned : blockSet) {
				for (CodeBlock blockOther : blockSet) {
					if (blockConcerned != blockOther) {
						for (DefPoint fromOther : blockOther.defPoints) {
							for (DefPoint fromConcerned : blockConcerned.defPoints) {
								if (fromOther.variable.equals(fromConcerned.variable)) {
									blockConcerned.KILL.add(fromOther);
									break;
								}
							}
						}
					}
				}
			}
			
			// repeatedly calculate IN and OUT
			boolean change = true;
			while (change) {
				change = false;
				for (CodeBlock block : blockSet) {
					// block.IN += block.pre.OUT
					for (CodeBlock pre : block.pre) {
						block.IN.addAll(pre.OUT);
					}
					// OLDOUT = block.OUT
					Set<DefPoint> oldOut = new HashSet<DefPoint>();
					oldOut.addAll(block.OUT);
					// block.OUT = block.GEN + (block.IN - block.KILL)
					block.OUT.clear();
					block.OUT.addAll(block.GEN);
					Set<DefPoint> tmp = new HashSet<DefPoint>();
					tmp.addAll(block.IN);
					tmp.removeAll(block.KILL);
					block.OUT.addAll(tmp);
					// if (OLDOUT != block.OUT) change = true;
					if (!oldOut.equals(block.OUT))
						change = true;
				}
			}
			
			if (Mode.isDebugMode()) {
				// output
				for (CodeBlock block : blockSet) {
					System.out.println("Block starting at "+block.firstLine+" ending at "+block.lastLine);
					System.out.print("GEN: ");
					for (DefPoint dp : block.GEN)
						System.out.print("("+dp.variable+","+dp.lineNumber+"); ");
					System.out.println();
					System.out.print("KILL: ");
					for (DefPoint dp : block.KILL)
						System.out.print("("+dp.variable+","+dp.lineNumber+"); ");
					System.out.println();
					System.out.print("IN: ");
					for (DefPoint dp : block.IN)
						System.out.print("("+dp.variable+","+dp.lineNumber+"); ");
					System.out.println();
					System.out.print("OUT: ");
					for (DefPoint dp : block.OUT)
						System.out.print("("+dp.variable+","+dp.lineNumber+"); ");
					System.out.println();						
				}
			}
		}
		
		if (Mode.isDebugMode()) System.out.println("--- Analyzing finished ---");
	}

	/**
	 * Analyzes all the def-use chain in all TargetCodes.
	 */
	private static void analyzeDUDataStream() {
		
		if (Mode.isDebugMode()) System.out.println("--- Analyzing DU Data Stream ---");
		
		for (Map<Integer, CodeBlock> blocks : codeBlocks.values()) {
			Collection<CodeBlock> blockSet = blocks.values();
			
			for (CodeBlock block : blockSet) {
				block.calcPoints();
				block.calcDUse();
				block.D_DEF.clear();
				block.D_IN.clear();
				block.D_OUT.clear();
			}
			
			for (CodeBlock blockConcerned : blockSet) {
				Set<String> defined = new HashSet<String>();
				for (DefPoint dp : blockConcerned.defPoints) {
					defined.add(dp.variable);
				}
				for (CodeBlock blockOther : blockSet) {
					for (RefPoint rp : blockOther.refPoints) {
						if (defined.contains(rp.variable)) {
							blockConcerned.D_DEF.add(rp);
						}
					}
				}
			}
			
			boolean change = true;
			while (change) {
				change = false;
				for (CodeBlock block : blockSet) {
					
					// block.D_OUT += block.next.D_IN
					for (CodeBlock next : block.next) {
						block.D_OUT.addAll(next.D_IN);	
					}
					
					// OLDIN = block.D_IN
					Set<RefPoint> oldIn = new HashSet<RefPoint>();
					oldIn.addAll(block.D_IN);
					
					// block.D_IN = (block.D_OUT - block.D_DEF) + block.D_USE
					block.D_IN.clear();
					block.D_IN.addAll(block.D_OUT);
					block.D_IN.removeAll(block.D_DEF);
					block.D_IN.addAll(block.D_USE);
					
					// if (block.D_IN != OLDIN) change = true;
					if (!block.D_IN.equals(oldIn))
						change = true;
				}
			}
			
			if (Mode.isDebugMode()) {
				// output
				for (CodeBlock block : blockSet) {
					System.out.println("Block starting at "+block.firstLine+" ending at "+block.lastLine);
					System.out.print("D_DEF: ");
					for (RefPoint rp : block.D_DEF)
						System.out.print(rp.lineNumber+"; ");
					System.out.println();
					System.out.print("D_USE: ");
					for (RefPoint rp : block.D_USE)
						System.out.print(rp.lineNumber+"; ");
					System.out.println();
					System.out.print("D_IN: ");
					for (RefPoint rp : block.D_IN)
						System.out.print(rp.lineNumber+"; ");
					System.out.println();
					System.out.print("D_OUT: ");
					for (RefPoint rp : block.D_OUT)
						System.out.print(rp.lineNumber+"; ");
					System.out.println();						
				}
			}
		}
		
		if (Mode.isDebugMode()) System.out.println("--- Analyzing Finished ---");
	}
	
	/**
	 * Does all the preparation before {@link #isVarActive(TargetCode, int, String)} 
	 * should be called.
	 * @throws Exception
	 */
	public static void prepareLivelinessCheck() throws Exception {
		analyzeCodeBlock();
		analyzeActiveVariables();
	}
	
	/**
	 * Analyzes the data stream of lively variables.
	 */
	private static void analyzeActiveVariables() {
		
		if (Mode.isDebugMode()) System.out.println("--- Analyzing Active Variables ---");
		
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			Map<Integer, CodeBlock> blocks = codeBlocks.get(TargetCode.getMethodTargetCode(i));
			if (Mode.isDebugMode()) System.out.println("-- Method #"+i+" start --");
			Collection<CodeBlock> blockSet = blocks.values();
			
			for (CodeBlock block : blockSet) {
				block.calcPoints();
				block.calcDef();
				block.calcUse();
				block.V_IN.clear();
			}
			
			boolean change = true;
			while (change) {
				change = false;
				for (CodeBlock block : blockSet) {
					
					// block.V_OUT += block.next.V_IN
					for (CodeBlock next : block.next) {
						block.V_OUT.addAll(next.V_IN);	
					}
					
					// OLDIN = block.V_IN
					Set<String> oldIn = new HashSet<String>();
					oldIn.addAll(block.V_IN);
					
					// block.V_IN = (block.V_OUT - block.DEF) + block.USE
					block.V_IN.clear();
					block.V_IN.addAll(block.V_OUT);
					block.V_IN.removeAll(block.DEF);
					block.V_IN.addAll(block.USE);
					
					// if (block.V_IN != OLDIN) change = true;
					if (!block.V_IN.equals(oldIn))
						change = true;
				}
			}
			
			if (Mode.isDebugMode()) {
				// output
				for (CodeBlock block : blockSet) {
					System.out.println("Block starting at "+block.firstLine+" ending at "+block.lastLine);
					System.out.print("DEF: ");
					for (String var : block.DEF) {
						System.out.print(var+"; ");
					}
					System.out.println();
					System.out.print("USE: ");
					for (String var : block.USE) {
						System.out.print(var+"; ");
					}
					System.out.println();
					System.out.print("V_IN: ");
					for (String var : block.V_IN) {
						System.out.print(var+"; ");
					}
					System.out.println();
					System.out.print("V_OUT: ");
					for (String var : block.V_OUT) {
						System.out.print(var+"; ");
					}
					System.out.println();
				}	
	
				System.out.println("-- Method #"+i+" end --");		
			}
		}
		
		if (Mode.isDebugMode()) System.out.println("--- Analyze finished ---");
	}
	
	/**
	 * Check if a variable is lively at a specific point.
	 * @param targetCode	Code context.
	 * @param lineNumber	Line number that indicates the point being investigated. 
	 * @param var	Name of the variable interested.
	 * @return	<b>true</b> if this variable is lively at this point; i.e. it's still
	 * possible for it to reach a usage code after this point before another definition.
	 * <b>false</b> otherwise.
	 */
	public static boolean isVarActive(TargetCode targetCode, 
			int lineNumber, String var) {
		
		Map<Integer, CodeBlock> blockMap = codeBlocks.get(targetCode);
		
		// Which block is current line in?
		List<Integer> startLines = new ArrayList<Integer>();
		startLines.addAll(blockMap.keySet());
		Collections.sort(startLines);
		int index = 0;
		for (; index < startLines.size()-1 && startLines.get(index+1) <= lineNumber; index++);
		CodeBlock block = blockMap.get(startLines.get(index));
		
		// After lineNumber, which kind of Point is var encountering first?
		for (int i = 0; i < block.points.size(); i++) {
			Point point = block.points.get(i);
			if (point.lineNumber < lineNumber) continue;
			if (!point.variable.equals(var)) continue;
			
			if (point.lineNumber == lineNumber) {
				// if X is defined at lineNumber, it's active here
				if (point instanceof DefPoint)
					return true;
				else
					continue;
			}
			
			if (point instanceof RefPoint)
				return true;
			if (point instanceof DefPoint)
				return false;
		}
		
		// No Point mentioned var; whether var is active depends on V_OUT
		return block.V_OUT.contains(var);
	}

	/**
	 * Finds all the expressions like Z := X op Y, and analyze the data stream
	 * of such expressions. The result is stored in corresponding fields of 
	 * code blocks in {@link #codeBlocks}.
	 */
	private static void analyzeDualExpression() {
		
		for (Map<Integer, CodeBlock> blockMap : codeBlocks.values()) {
			// for one TargetCode
			
			Collection<CodeBlock> blockSet = blockMap.values();
			
			Set<DualExpInstance> E_ALL = new HashSet<DualExpInstance>();
			
			for (CodeBlock block : blockSet) {
				block.calcPoints();
				block.calcEGen();
				E_ALL.addAll(block.E_GEN);
				block.E_IN.clear();
				block.E_OUT.clear();
			}
			
			for (CodeBlock block : blockSet) {
				block.calcEKill(E_ALL);
			}
			
			// E_IN[B0] = {};
			// E_IN[B0] = E_GEN[B0]; 
			CodeBlock firstBlock = blockMap.get(0);
			firstBlock.E_OUT.addAll(firstBlock.E_GEN);
			
			// E_OUT[Bi] = U-E_KILL[Bi]; i = 1, 2, ... 
			for (CodeBlock block : blockSet) if (block != firstBlock) {
				block.E_OUT.addAll(E_ALL);
				block.E_OUT.removeAll(block.E_KILL);
			}
			
			boolean change = true;
			while (change) {
				change = false;
				for (CodeBlock block : blockSet) if (block != firstBlock) {
					// E_IN[Bi] = E_OUT[p0]*E_OUT[p1]*...*E_OUT[pj], p in pre[Bi]
					block.E_IN.clear();
					block.E_IN.addAll(E_ALL);
					for (CodeBlock p : block.pre) {
						Set<DualExpInstance> removed = new HashSet<DualExpInstance>();
						for (DualExpInstance dei : block.E_IN) {
							if (!removed.contains(dei) && !p.E_OUT.contains(dei)) {
								removed.add(dei);
							}
						}
						block.E_IN.removeAll(removed);
					}
					// oldOut = E_OUT[Bi];
					Set<DualExpInstance> oldOut = new HashSet<DualExpInstance>();
					oldOut.addAll(block.E_OUT);
					// E_OUT[Bi] = (E_IN[Bi]-E_KILL[Bi]) + E_GEN[Bi];
					block.E_OUT.clear();
					block.E_OUT.addAll(block.E_IN);
					block.E_OUT.removeAll(block.E_KILL);
					block.E_OUT.addAll(block.E_GEN);
					
					if (!block.E_OUT.equals(oldOut))
						change = true;
				}
			}
		}
	}
	
	/**
	 * Includes all the steps to remove removable single static assignments.
	 * @return	<b>true</b> if any optimization has been done; <b>false</b> otherwise.
	 * @throws BlockAnalysisException
	 */
	static boolean optimizeStaticSingleAssignments() throws BlockAnalysisException {
		int times = 0;
		do {
			times++;
			analyzeCodeBlock();
			analyzeDUDataStream();
			analyzeUDDataStream();
			analyzeSingleAssignmentStream();
		} while (removeStaticSingleAssignments());
		return (times > 1);
	}

	/**
	 * Finds all the expressions like X := Y, and analyze the data stream of such
	 * expressions. The result is stored in corresponding fields of code blocks in
	 * {@link #codeBlocks}.
	 */
	private static void analyzeSingleAssignmentStream() {
		
		if (Mode.isDebugMode()) System.out.println("--- analyzing single assignment stream ---");
		
		Map<TargetCode, Set<SingleAsgnInstance>> ans
			= new HashMap<TargetCode, Set<SingleAsgnInstance>>();

		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode targetCode = TargetCode.getMethodTargetCode(i);
			Map<Integer, CodeBlock> blockMap = codeBlocks.get(targetCode);
			
			// for one TargetCode
			
			Collection<CodeBlock> blockSet = blockMap.values();
			
			Set<SingleAsgnInstance> ALL = new HashSet<SingleAsgnInstance>();
			for (CodeBlock block : blockSet) {
				block.calcPoints();
				block.calcCGen();
				ALL.addAll(block.C_GEN);
				block.C_KILL.clear();
				block.C_IN.clear();
				block.C_OUT.clear();
			}

			ans.put(targetCode, ALL);
			
			for (CodeBlock block : blockSet) {
				block.calcCKill(ALL);
			}
						
			// C_IN[B0] = {};
			// C_OUT[B0] = C_GEN[B0];
			CodeBlock firstBlock = blockMap.get(0);
			firstBlock.C_OUT.addAll(firstBlock.C_GEN);
			
			boolean change = true;
			while (change) {
				change = false;
				for (CodeBlock block : blockSet) {
					// C_IN[Bi] = C_OUT[p0]*C_OUT[p1]*...*C_OUT[pJ], p in p[Bi], i >= 1
					if (block != firstBlock) {
						block.C_IN.clear();
						block.C_IN.addAll(ALL);
						Set<SingleAsgnInstance> removed = new HashSet<SingleAsgnInstance>();
						for (CodeBlock p : block.pre) {
							for (SingleAsgnInstance sai : block.C_IN) {
								if (!removed.contains(sai) && !p.C_OUT.contains(sai)) {
									removed.add(sai);
								}
							}
							block.C_IN.removeAll(removed);
						}
					}
					// C_OUT[B] = (C_IN[B]-C_KILL[B])+C_GEN[B]
					Set<SingleAsgnInstance> oldOut = new HashSet<SingleAsgnInstance>();
					oldOut.addAll(block.C_OUT);
					block.C_OUT.clear();
					block.C_OUT.addAll(block.C_IN);
					block.C_OUT.removeAll(block.C_KILL);
					block.C_OUT.addAll(block.C_GEN);
					if (!block.C_OUT.equals(oldOut)) {
						change = true;
					}
				}
			}
			
			if (Mode.isDebugMode()) {
				// output
				for (CodeBlock block : blockSet) {
					System.out.println("block starting at "+block.firstLine+" ending at "+block.lastLine);
					System.out.print("C_GEN: ");
					for (SingleAsgnInstance sai : block.C_GEN) {
						System.out.print(sai);
					}
					System.out.println();
					System.out.print("C_KILL: ");
					for (SingleAsgnInstance sai : block.C_KILL) {
						System.out.print(sai);
					}
					System.out.println();
					System.out.print("C_IN: ");
					for (SingleAsgnInstance sai : block.C_IN) {
						System.out.print(sai);
					}
					System.out.println();
					System.out.print("C_OUT: ");
					for (SingleAsgnInstance sai : block.C_OUT) {
						System.out.print(sai);
					}
					System.out.println();
				}
			}
		}
		
		if (Mode.isDebugMode()) System.out.println("--- single assignment stream analyzed ---");
	}

	/**
	 * For each TargetCode, examines every single assignment that appear at least once
	 * in this method to see if it's suitable for removing. If so, remove it and return
	 * directly without going on to other single assignments. If no such single assignment
	 * has been found, return false. 
	 * @return	<b>true</b> if a single assignment has been removed; <b>false</b> otherwise.
	 */
	private static boolean removeStaticSingleAssignments() {
		
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode targetCode = TargetCode.getMethodTargetCode(i);
			Map<Integer, CodeBlock> blockMap = codeBlocks.get(targetCode);
			// for one TargetCode
			
			Collection<CodeBlock> blockSet = blockMap.values();
			
			List<SingleAsgnInstance> ALL = new ArrayList<SingleAsgnInstance>();
			for (CodeBlock block : blockSet) {
				ALL.addAll(block.C_ALL);
			}
			
			Collections.sort(ALL, new Comparator<SingleAsgnInstance>() {

				@Override
				public int compare(SingleAsgnInstance sai0,
						SingleAsgnInstance sai1) {
					return sai0.lineNumber - sai1.lineNumber;
				}
			});
			
			for (SingleAsgnInstance sai : ALL) {
				
				// calculate corresponding DefPoint
				DefPoint defPoint = new DefPoint(sai.lineNumber, sai.X);
				
				// Step #1: find every RefPoint that defPoint can reach
				Set<RefPoint> canReach = new HashSet<RefPoint>();
				CodeBlock block = where(sai.lineNumber, blockSet); 
				// inside this block
				for (int j = 0; j < block.points.size(); j++) {
					Point p = block.points.get(j);
					if (p.lineNumber <= defPoint.lineNumber) continue;
					if (p.variable.equals(defPoint.variable)) {
						if (p instanceof RefPoint) canReach.add((RefPoint)p);
						else break;
					}
				}
				// outside this block
				if (block.OUT.contains(defPoint)) {
					for (RefPoint rp : block.D_OUT) {
						if (rp.variable.equals(sai.X)) {
							canReach.add(rp);
						}
					}
				}
				
				// Step #2: for every block Bi that contains a RefPoint in canReach,
				//          sai should be in C_IN[Bi], and no in-block DefPoint before
				//          the RefPoint that is in canReach.
				// p.s. if the RefPoint is in invoke-range, this single assignment cannot
				//      be removed.
				boolean canRemove = true;
				for (RefPoint refPoint : canReach) {
					if (refPoint.isFixed) {
						canRemove = false;
						break;
					}
					
					CodeBlock reachedBlock = where(refPoint.lineNumber, blockSet);
					if (reachedBlock != block && !reachedBlock.C_IN.contains(sai)) {
						canRemove = false;
						break;
					}
					for (int j = 0; j < reachedBlock.defPoints.size(); j++) {
						DefPoint dp = reachedBlock.defPoints.get(j);
						if (dp.lineNumber <= sai.lineNumber) continue;
						if (dp.lineNumber >= refPoint.lineNumber) break;
						if (dp.variable.equals(sai.X) || dp.variable.equals(sai.Y)) {
							canRemove = false;
							break;
						}
					}
				}
				
				if (canRemove) {
					if (Mode.isDebugMode()) System.out.println("removing ssa: "+sai);
					
					// Step #3: remove sai, and refer to Y instead of X in every RefPoint
					//          in canReach.
					
					Code codeToDelete = targetCode.getCodeByLineNumber(sai.lineNumber);
					//codesToDelete.add(codeToDelete);
					
					for (RefPoint refPoint : canReach) {
						Code code = targetCode.getCodeByLineNumber(refPoint.lineNumber);
						for (String key : ISA.getSrc(code)) {
							String value = code.getParamValue(key);
							if (sai.X.equals(value)) {
								code.parameterMap.put(key, sai.Y);
							}
						}
					}
					
					RegLabelLock.LabelLock(targetCode);
					targetCode.deleteCode(codeToDelete.codeIdx);
					RegLabelLock.LabelUnlock(targetCode);	
					
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Given a set of CodeBlocks, determine which CodeBlock a Code is in by its line number.
	 * @param lineNumber	Line number where the Code concerned is located.
	 * @param blockSet	Set of CodeBlock to choose from.
	 * @return	One of the CodeBlock within blockSet that contains this Code.
	 */
	private static CodeBlock where(int lineNumber, Collection<CodeBlock> blockSet) {
		for (CodeBlock block : blockSet) {
			if (lineNumber >= block.firstLine && lineNumber <= block.lastLine) return block;
		}
		return null;
	}

	/**
	 * Examines every def point; if this definition is not used ever after, remove it.
	 * @return	<b>true</b> if any optimization has been done; <b>false</b> otherwise.
	 * @throws BlockAnalysisException
	 */
	static boolean removeUnusedDefinition() throws BlockAnalysisException {
		analyzeCodeBlock();
		analyzeUDDataStream();
		analyzeDUDataStream();
		
		boolean optimized = false;
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode targetCode = TargetCode.getMethodTargetCode(i);
			List<Code> codesToDelete = new ArrayList<Code>();
			
			Collection<CodeBlock> blockSet = codeBlocks.get(targetCode).values();
			for (CodeBlock block : blockSet) {
				for (DefPoint dp : block.defPoints) {
					boolean isUsed = false;
					// check in-block reference
					for (int j = block.refPoints.size()-1; j >= 0; j--) {
						RefPoint rp = block.refPoints.get(j);
						if (rp.lineNumber <= dp.lineNumber) break;
						if (rp.variable.equals(dp.variable)) {
							isUsed = true;
							break;
						}
					}
					if (isUsed) continue;
					// check out-block reference
					if (block.OUT.contains(dp)) {
						for (CodeBlock n : block.next) {
							for (RefPoint rp : n.D_IN) {
								if (rp.variable.equals(dp.variable)) {
									isUsed = true;
									break;
								}
							}
							if (isUsed) break;
						}
					}
					if (isUsed) continue;
					
					Code code = targetCode.getCodeByLineNumber(dp.lineNumber);
					codesToDelete.add(code);
				}
			}

			if (!codesToDelete.isEmpty()) {
				optimized = optimized || true;
				Collections.sort(codesToDelete, new Comparator<Code>() {

					@Override
					public int compare(Code code0, Code code1) {
						return code1.lineNumber - code0.lineNumber;
					}
				});
				RegLabelLock.LabelLock(targetCode);
				for (Code code : codesToDelete) {
					targetCode.deleteCode(code.codeIdx);
				}
				RegLabelLock.LabelUnlock(targetCode);
			}
		}
		return optimized;
	}
}

/**
 * Assists to record jumping relationships between Codes established by jumping
 * instructions, before CodeBlocks are initialized. 
 * @author Xie Jiaye
 */
class Edge {
	CodeBlock from, to;
	Edge(CodeBlock from, CodeBlock to) {
		this.from = from;
		this.to = to;
	}
}


/**
 * Base class for {@link minijava.intermediate.DefPoint DefPoint} and
 * {@link minijava.intermediate.RefPoint RefPoint}, just for the convenience
 * of uniformity.
 * @author Xie Jiaye
 */
class Point {
	
	/**
	 * Line number of corresponding {@link minijava.intermediate.Code Code}.
	 */
	int lineNumber;
	/**
	 * Variable concerned.
	 */
	String variable;
	
	Point (int lineNumber, String variable) {
		this.lineNumber = lineNumber;
		this.variable = variable;
	}
	
	@Override
	public int hashCode() {
		return lineNumber;
	}
}

/**
 * Definition point. i.e. The variable is defined at the specific line number.
 * @author Xie Jiaye
 */
class DefPoint extends Point{
	DefPoint(int lineNumber, String variable) {
		super(lineNumber, variable);
	}
	
	@Override
	public String toString() {
		return "Def["+lineNumber+":"+variable+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefPoint)) return false;
		if (((DefPoint)obj).lineNumber != lineNumber) return false;
		if (!((DefPoint)obj).variable.equals(variable)) return false;
		return true;
	}
}

/**
 * Reference point. i.e. The variable is referenced, i.e. used, at the specific
 * line number.
 * @author Xie Jiaye
 */
class RefPoint extends Point{
	/**
	 * Special flag recording if this reference is from an invoke-range instruction.
	 * If so, this RefPoint cannot be modified.
	 */
	boolean isFixed;
	RefPoint(int lineNumber, String variable, boolean isFixed) {
		super(lineNumber, variable);
		this.isFixed = isFixed;
	}
	
	@Override
	public String toString() {
		return "Ref["+lineNumber+":"+variable+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RefPoint)) return false;
		if (((RefPoint)obj).lineNumber != lineNumber) return false;
		if (!((RefPoint)obj).variable.equals(variable)) return false;
		return true;
	}
}

/**
 * Map where a key is mapped to more than one values.
 * For the convenience of initializing the flow graph.
 * @author Xie Jiaye
 * @param <T1>	Type of key.
 * @param <T2>	Type of values.
 */
class MultiMap<T1, T2> {
	private Map<T1, Set<T2>> map = new HashMap<T1, Set<T2>>();
	
	void put(T1 key, T2 value) {
		if (map.containsKey(key)) {
			map.get(key).add(value);
		} else {
			Set<T2> values = new HashSet<T2>();
			values.add(value);
			map.put(key, values);
		}
	}
	
	Set<T2> get(T1 key) {
		return map.get(key);
	}
	
	boolean containsKey(T1 key) {
		return map.containsKey(key);
	}
}

/**
 * An expression in the form of "Z: = X op Y".
 * @author Xie Jiaye
 */
class DualExpInstance {
	// Z := X op Y;
	
	/**
	 * Line number of the instruction that calculates this expression.
	 */
	int lineNumber;
	/**
	 * First operating variable.
	 */
	String X;
	/**
	 * Second operating variable.
	 */
	String Y;
	/**
	 * Destination variable.
	 */
	String Z;
	
	/**
	 * Constructs a new instance of this expression using the model of
	 * the expression and a code containing specific information. 
	 * @param exp
	 * @param code
	 */
	DualExpInstance(DualExp exp, Code code) {
		lineNumber = code.lineNumber;
		X = code.getParamValue(exp.X);
		Y = code.getParamValue(exp.Y);
		Z = code.getParamValue(exp.Z);
	}
	
	@Override
	public String toString() {
		String x = X, y = Y, z = Z;
		if (x.startsWith("^_^")) x = String.valueOf(TypeChange.parseInt(x));
		else x = "v"+x;
		if (y.startsWith("^_^")) y = String.valueOf(TypeChange.parseInt(y));
		else y = "v"+y;
		return z+" := "+x+" op "+y;
	}
}

/**
 * An assignment in the form of "X := Y".
 * @author Xie Jiaye
 */
class SingleAsgnInstance {
	// X := Y;
	
	/**
	 * Line number of the instruction that executes this assignment.
	 */
	int lineNumber;
	/**
	 * Destination variable.
	 */
	String X;
	/**
	 * Source variable.
	 */
	String Y;
	
	/**
	 * Constructs a new instance of this assignment using the model of
	 * the expression and a code containing specific information.
	 * @param asgn
	 * @param code
	 */
	SingleAsgnInstance(SingleAssignment asgn, Code code) {
		lineNumber = code.lineNumber;
		X = code.getParamValue(asgn.X);
		Y = code.getParamValue(asgn.Y);
	}
	
	@Override
	public String toString() {
		return "v"+X+":=v"+Y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SingleAsgnInstance)) return false;
		SingleAsgnInstance sai = (SingleAsgnInstance)obj;
		return (sai.lineNumber == lineNumber && sai.X.equals(X) && sai.Y.equals(Y));
	}
	
	@Override
	public int hashCode() {
		return lineNumber;
	}
}