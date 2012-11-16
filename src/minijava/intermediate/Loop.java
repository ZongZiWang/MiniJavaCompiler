package minijava.intermediate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import util.Mode;

/**
 * Group of {@link minijava.intermediate.CodeBlock CodeBlock} where a path
 * exists between any two of them in the flow graph.
 * @author Xie Jiaye
 */
public class Loop {
	/**
	 * Code context.
	 */
	private TargetCode originalCode;
	
	/**
	 * Set of {@link minijava.intermediate.CodeBlock CodeBlock} in this loop.
	 */
	private Set<CodeBlock> blocks = new HashSet<CodeBlock>();
	/**
	 * Working stack used for initializing the loop.
	 */
	private Stack<CodeBlock> stack = new Stack<CodeBlock>();
	/**
	 * The only block in this loop that dominates all other blocks.
	 */
	private CodeBlock entrance;
	/**
	 * Set of all blocks that may jump to a block outside this loop.
	 */
	private Set<CodeBlock> exits = new HashSet<CodeBlock>();
	
	/**
	 * Initialize a loop with a backward edge in the flow graph.
	 * @param targetCode
	 * @param backwardEdge
	 */
	Loop(TargetCode targetCode, Edge backwardEdge) {
		originalCode = targetCode;
		
		entrance = backwardEdge.to;
		blocks.add(entrance);
		insert(backwardEdge.from);
		
		while (!stack.isEmpty()) {
			CodeBlock m = stack.pop();
			for (CodeBlock p : m.pre)
				insert(p);
		}
		
		calcExits();
	}
	
	/**
	 * Add a block into this loop; called only when initializing this loop.
	 * @param m
	 */
	private void insert(CodeBlock m) {
		if (!blocks.contains(m)) {
			blocks.add(m);
			stack.push(m);	
		}
	}
	
	/**
	 * Find all the exit blocks. The result is stored in {@link #exits}.
	 */
	private void calcExits() {
		for (CodeBlock block : blocks) {
			for (CodeBlock n : block.next) {
				if (!blocks.contains(n)) {
					exits.add(block);
				}
			}
		}
	}
	
	/**
	 * @param lineNumber
	 * @return	<b>true</b> if this line falls in one of the blocks from {@link #blocks}.
	 * <b>false</b> otherwise.
	 */
	private boolean withinLoop(int lineNumber) {
		for (CodeBlock block : blocks) {
			if (lineNumber >= block.firstLine && lineNumber <= block.lastLine)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if a variable is static throughout the loop.
	 * @param var
	 * @return	<b>true</b> if var is an immediate value or it's in every
	 * block's {@link CodeBlock#IN IN} and {@link CodeBlock#OUT OUT}.
	 * <b>false</b> otherwise.
	 */
	private boolean isConst(String var) {

		// already in loop constants
		for (DualExpInstance dei : loopConstants) {
			if (dei.Z.equals(var))
				return true;
		}
		
		// immediate value
		if (var.startsWith("^_^")) return true;
		
		// in every block's IN and OUT
		for (CodeBlock block : blocks) {
			boolean foundIn = false;
			for (DefPoint dp : block.IN) {
				if (dp.variable.equals(var) && !withinLoop(dp.lineNumber)) {
					foundIn = true;
					break;
				}
			}
			if (!foundIn) return false;
			boolean foundOut = false;
			for (DefPoint dp  : block.OUT) {
				if (dp.variable.equals(var) && !withinLoop(dp.lineNumber)) {
					foundOut = true;
					break;
				}
			}
			if (!foundOut) return false;
		}
		return true;
	}
	
	/**
	 * Set of all the static variables within this loop.
	 */
	private List<DualExpInstance> loopConstants = new ArrayList<DualExpInstance>();
	
	/**
	 * Calculates all the possible static variables. The result is stored in 
	 * {@link #loopConstants}.
	 */
	void calcLoopConstants() {
		boolean change = true;
		while (change) {
			change = false;
			for (CodeBlock block : blocks) {
				for (DualExpInstance dei : block.E_ALL) 
					if (!loopConstants.contains(dei) && isConst(dei.X) && isConst(dei.Y)) {
						change = true;
						loopConstants.add(dei);
					}
			}
		}
		if (Mode.isDebugMode()) {
			System.out.println("Initial Loop Constants:");
			printConstants();
		}
	}
	
	/**
	 * Prints {@link #loopConstants}. For debug use.
	 */
	private void printConstants() {
		System.out.println("--- Loop Constants Start---");
		String result = "[";
		for (DualExpInstance dei :  loopConstants) {
			result += dei+", ";
		}
		result += "]";
		System.out.println(result);
		System.out.println("--- Loop Constants End ---");
	}
	
	/**
	 * @param lineNumber
	 * @return	A block within loop that contains this line of code. If no such block
	 * is found, return <b>null</b>.
	 */
	private CodeBlock where(int lineNumber) {
		for (CodeBlock block : blocks) {
			if (lineNumber >= block.firstLine && lineNumber <= block.lastLine)
				return block;
		}
		return null;
	}
	
	/**
	 * For all the possible static variables, check if those definition expressions
	 * can be hoisted.
	 */
	void checkExportableLoopConstants() {
		Set<DualExpInstance> removed = new HashSet<DualExpInstance>();
		for (DualExpInstance dei : loopConstants) {
			CodeBlock place = where(dei.lineNumber);
			
			boolean isExportable = true;
			
			// the block that dei is in DOMs evey other block in this loop
			for (CodeBlock exit : exits) {
				if (!exit.DOM.contains(place)) {
					isExportable = false;
					break;
				}
			}
			
			if (!isExportable) {
				removed.add(dei);
				continue;
			}
			
			// dei.Z is not defined elsewhere in this loop
			for (CodeBlock block : blocks) {
				for (DefPoint dp : block.defPoints)
					if (dp.lineNumber != dei.lineNumber && dp.variable.equals(dei.Z)) {
						isExportable = false;
						break;
					}
				if (!isExportable) break;
			}

			if (!isExportable) {
				removed.add(dei);
				continue;
			}
			
			// all other ref points in this loop use definition from dei
			// calculate du chain of dei.Z from dei.lineNumber
			Set<RefPoint> refs = new HashSet<RefPoint>();
			for (int i = place.refPoints.size()-1; i >= 0; i--) {
				RefPoint rp = place.refPoints.get(i);
				if (rp.lineNumber < dei.lineNumber) break;
				if (rp.variable.equals(dei.Z)) refs.add(rp);
			}
			refs.addAll(place.D_OUT);
			for (CodeBlock block : blocks) {
				for (RefPoint rp : block.refPoints) {
					if (rp.variable.equals(dei.Z) && !refs.contains(rp)) {
						isExportable = false;
						break;
					}
				}
				if (!isExportable) {
					break;
				}
			}
			if (!isExportable) {
				removed.add(dei);
			}
		}
		loopConstants.removeAll(removed);
		if (Mode.isDebugMode()) {
			System.out.println("Exportable Loop Constants:");
			printConstants();
		}
	}
	
	/**
	 * Hoist all the static variables.
	 * @return <b>true</b> if optimization has been done; <b>false</b> otherwise.
	 */
	boolean exportLoopConstants() {
		
		for (int i = 0; i < loopConstants.size(); i++) {
			DualExpInstance dei = loopConstants.get(i);
			
			RegLabelLock.LabelLock(originalCode);
			
			Code firstCode = originalCode.getCodeByLineNumber(entrance.firstLine);
			Code codeToMove = originalCode.getCodeByLineNumber(dei.lineNumber);
			Code newCode = new Code(codeToMove);
			newCode.lineNumber = originalCode.getCodeByIndex(firstCode.codeIdx-1).lineNumber+1;
			
			originalCode.insertCode(newCode, firstCode.codeIdx-1);
			originalCode.deleteCode(codeToMove.codeIdx+1);
			
			RegLabelLock.LabelUnlock(originalCode);
		}
		
		return !loopConstants.isEmpty();
	}
	
	@Override
	public String toString() {
		String str = "[";
		for (CodeBlock b : blocks) {
			str += b+", ";
		}
		if (blocks.isEmpty())
			str += "]";
		else
			str += "\b\b]";
		return str;
	}
}
