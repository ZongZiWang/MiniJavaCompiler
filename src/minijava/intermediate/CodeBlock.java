package minijava.intermediate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minijava.binary.DualExp;
import minijava.binary.ISA;
import minijava.binary.SingleAssignment;
import util.TypeChange;

/**
 * Basic code block in a method, which means there are no jumping instructions within
 * the block except for the last instruction. Within the block codes are executed 
 * from the first line to the last line in sequence without being disturbed.
 * @author Xie Jiaye
 */
public class CodeBlock {
	
	/**
	 * Code context.
	 */
	TargetCode originalCode;
	/**
	 * Line number of the first Code in this block.
	 */
	int firstLine;
	/**
	 * Line number of the last Code in this block.
	 */
	int lastLine;
	
	// relationships between blocks
	/**
	 * Set of all {@link minijava.intermediate.CodeBlock CodeBlock}
	 * that may jump to this block.
	 */
	Set<CodeBlock> pre = new HashSet<CodeBlock>();
	/**
	 * Set of all {@link minijava.intermediate.CodeBlock CodeBlock} 
	 * that this CodeBlock may jump to.
	 */
	Set<CodeBlock> next = new HashSet<CodeBlock>();
	/**
	 * Set of all {@link minijava.intermediate.CodeBlock CodeBlock} 
	 * that dominate this CodeBlock.
	 */
	Set<CodeBlock> DOM = new HashSet<CodeBlock>();
	
	// def and use points of variables
	/**
	 * List of all the {@link minijava.intermediate.DefPoint DefPoint} that
	 * occur in this block; sorted in ascending order by their line number.
	 */
	List<DefPoint> defPoints = new ArrayList<DefPoint>();
	/**
	 * List of all the {@link minijava.intermediate.RefPoint RefPoint} that
	 * occur in this block; sorted in ascending order by their line number.
	 */
	List<RefPoint> refPoints = new ArrayList<RefPoint>();
	/**
	 * List of all the {@link minijava.intermediate.Point Point} that
	 * occur in this block; sorted in ascending order by their line number.
	 */
	List<Point> points = new ArrayList<Point>();
	
	// ud data stream analysis
	/**
	 * Set of all {@link minijava.intermediate.DefPoint DefPoint}
	 * that reach the first line of this block.
	 */
	Set<DefPoint> IN = new HashSet<DefPoint>();
	/**
	 * Set of all {@link minijava.intermediate.DefPoint DefPoint}
	 * that reach the line after the last line of this block.
	 */
	Set<DefPoint> OUT = new HashSet<DefPoint>();
	/**
	 * Set of all {@link minijava.intermediate.DefPoint DefPoint}
	 * that are generated within this block and reach the last line of this block.
	 */
	Set<DefPoint> GEN = new HashSet<DefPoint>();
	/**
	 * Set of all {@link minijava.intermediate.DefPoint DefPoint}
	 * that are redefined within this block.
	 */
	Set<DefPoint> KILL = new HashSet<DefPoint>();
	
	// du data stream analysis
	/**
	 * Set of all {@link minijava.intermediate.RefPoint RefPoint}
	 * within a block in {@link #next} such that they use the same 
	 * definitions reaching the first line of this block.
	 */
	Set<RefPoint> D_IN = new HashSet<RefPoint>();
	/**
	 * Set of all {@link minijava.intermediate.RefPoint RefPoint}
	 * within a block in {@link #next} such that they use the same
	 * definitions reaching the line after the last line of this block.
	 */
	Set<RefPoint> D_OUT = new HashSet<RefPoint>();
	/**
	 * Set of all {@link minijava.intermediate.RefPoint RefPoint}
	 * within this block such that there's no {@link minijava.intermediate.DefPoint
	 *  DefPoint}s of corresponding variable before this use point within block. 
	 */
	Set<RefPoint> D_USE = new HashSet<RefPoint>();
	/**
	 * Set of all {@link minijava.intermediate.RefPoint RefPoint}
	 * outside this block such that the variable they use are defined in this block.
	 */
	Set<RefPoint> D_DEF = new HashSet<RefPoint>();
	
	// active variable analysis
	/**
	 * Set of all variables that are lively at the line after last line of this block.
	 */
	Set<String> V_OUT = new HashSet<String>();
	/**
	 * Set of all variables that are lively at the first line of this block.
	 */
	Set<String> V_IN = new HashSet<String>();
	/**
	 * Set of all variables that are defined but never used again in this block.
	 */
	Set<String> DEF = new HashSet<String>();
	/**
	 * Set of all variables that are used but never defined again in this block.
	 */
	Set<String> USE = new HashSet<String>();
	
	// available expressions analysis
	/**
	 * Set of all {@link minijava.intermediate.DualExpInstance DualExpInstance}
	 * that occur within this block.
	 */
	Set<DualExpInstance> E_ALL = new HashSet<DualExpInstance>();
	/**
	 * Set of all {@link minijava.intermediate.DualExpInstance DualExpInstance}
	 * that are calculated within block and that their operating variables are
	 * never redefined afterwards.
	 */
	Set<DualExpInstance> E_GEN = new HashSet<DualExpInstance>();
	/**
	 * Set of all {@link minijava.intermediate.DualExpInstance DualExpInstance}
	 * such that the operating numbers of the expression are defined within
	 * this block, and the whole expression is never recalculated again.
	 */
	Set<DualExpInstance> E_KILL = new HashSet<DualExpInstance>();
	/**
	 * Set of all {@link minijava.intermediate.DualExpInstance DualExpInstance}
	 * such that their corresponding {@link minijava.intermediate.DefPoint DefPoint}
	 * reach the first line of this block.
	 */
	Set<DualExpInstance> E_IN = new HashSet<DualExpInstance>();
	/**
	 * Set of all {@link minijava.intermediate.DualExpInstance DualExpInstance}
	 * such that their corresponding {@link minijava.intermediate.DefPoint DefPoint}
	 * reach the line after the last line of this block.
	 */
	Set<DualExpInstance> E_OUT = new HashSet<DualExpInstance>();
	
	// single assignment analysis
	/**
	 * Set of all {@link minijava.intermediate.SingleAsgnInstance SingleAsgnInstance}
	 * that occur within this block.
	 */
	Set<SingleAsgnInstance> C_ALL = new HashSet<SingleAsgnInstance>();
	/**
	 * Set of all {@link minijava.intermediate.SingleAsgnInstance SingleAsgnInstance}
	 * within this block such that their source and destination variables are never
	 * redefined afterwards.
	 */
	Set<SingleAsgnInstance> C_GEN = new HashSet<SingleAsgnInstance>();
	/**
	 * Set of all {@link minijava.intermediate.SingleAsgnInstance SingleAsgnInstance}
	 * outside this block such that their source or destination variables are redefined
	 * within this block.
	 */
	Set<SingleAsgnInstance> C_KILL = new HashSet<SingleAsgnInstance>();
	/**
	 * Set of all {@link minijava.intermediate.SingleAsgnInstance SingleAsgnInstance}
	 * such that they occur in every path from first block to this block's first line
	 * and their source and destination variables are never redefined after the last
	 * occurrence before they reach the first line of this block.
	 */
	Set<SingleAsgnInstance> C_IN = new HashSet<SingleAsgnInstance>();
	/**
	 * Set of all {@link minijava.intermediate.SingleAsgnInstance SingleAsgnInstance}
	 * such that they occur in every path from first block to the line after this block's
	 * last line and their source and destination variables are never redefined after
	 * the last occurrence before they reach the exit of this block.
	 */
	Set<SingleAsgnInstance> C_OUT = new HashSet<SingleAsgnInstance>();
	
	/**
	 * Constructor that defines the basic attributes of the block. 
	 * @param code	Code context.
	 * @param first		Line number of the first {@link minijava.intermediate.Code Code}
	 * in this block.
	 * @param last		Line number of the last {@link minijava.intermediate.Code Code}
	 * in this block.
	 */
	CodeBlock(TargetCode code, int first, int last) {
		originalCode = code;
		firstLine = first;
		lastLine = last;
	}
	
	// ========== def and use points calculation ========== 
	/**
	 * Find all the {@link minijava.intermediate.DefPoint DefPoint} that occur within
	 * this block and sort them in ascending order by line numbers. The result is stored
	 * in {@link #defPoints}.
	 */
	private void calcDefPoints() {
		defPoints.clear();
		for (int lineNumber = firstLine; lineNumber <= lastLine; ) {
			Code code = originalCode.getCodeByLineNumber(lineNumber);
			for (String key : ISA.getDst(code)) {
				String var = code.getParamValue(key);
				if (TypeChange.isInt(var))
					defPoints.add(new DefPoint(lineNumber, var));
			}
			lineNumber += ISA.getLength(code.codeOp);
		}
	}

	/**
	 * Find all the {@link minijava.intermediate.RefPoint RefPoint} that occur within
	 * this block and sort them in ascending order by line numbers. The result is stored
	 * in {@link #refPoints}.
	 */
	private void calcRefPoints() {
		refPoints.clear();
		for (int lineNumber = firstLine; lineNumber <= lastLine; ) {
			Code code = originalCode.getCodeByLineNumber(lineNumber);
			String[] src = ISA.getSrc(code);
			// specially handle invoke-range
			if (src.length == 3 && src[0].equals("range")) {
				int start = TypeChange.parseInt(code.getParamValue(src[1]));
				int cnt = TypeChange.parseInt(code.getParamValue(src[2]));
				for (int reg = start; reg < start+cnt; reg++) {
					String var = String.valueOf(reg);
					refPoints.add(new RefPoint(lineNumber, var, true));
				}
			} else
				// nothing special
				for (String key : ISA.getSrc(code)) {
					String var = code.getParamValue(key);
					if (TypeChange.isInt(var))
					refPoints.add(new RefPoint(lineNumber, var, false));
				}
			lineNumber += ISA.getLength(code.codeOp);
		}
	}
	
	/**
	 * Includes the steps to calculate {@link #defPoints} and {@link #refPoints}, and
	 * then combine them into {@link #points}. Still sorted in ascending order by line
	 * numbers.
	 */
	void calcPoints() {
		points.clear();
		calcDefPoints();
		calcRefPoints();
		points.addAll(refPoints);
		points.addAll(defPoints);
		Collections.sort(points, new Comparator<Point>() {

			@Override
			public int compare(Point point0, Point point1) {
				int dl = point0.lineNumber - point1.lineNumber;
				if (dl != 0) return dl;
				else {
					if (point0 instanceof RefPoint) return -1;
					else return 1;
				}
			}
		});
	}
	// ========== def and use points calculated ==========
	
	// ========== for reach-def analysis ==========
	/**
	 * Calculates {@link #GEN} using the information within this block only.
	 */
	void calcGen() {
		GEN.clear();
		for (int i = defPoints.size()-1; i >= 0; i--) {
			DefPoint current = defPoints.get(i);
			boolean isGen = true;
			for (DefPoint dp : GEN) {
				if (dp.variable.equals(current.variable)) {
					isGen = false;
					break;
				}
			}
			if (isGen)
				GEN.add(current);
		}
	}
	// ========== reach-def analysis end ==========
	
	// ========== for active-var analysis ==========
	/**
	 * Calculates {@link #DEF} using the information within this block only.
	 */
	void calcDef() {
		DEF.clear();
		for (int i = 0; i < defPoints.size(); i++) {
			DefPoint dp = defPoints.get(i);
			String var = dp.variable;
			boolean isDef = true;
			for (int j = 0; j < refPoints.size(); j++) {
				RefPoint rp = refPoints.get(j);
				if (rp.lineNumber > dp.lineNumber)
					break;
				if (rp.variable.equals(var)) {
					isDef = false;
					break;
				}
			}
			if (isDef) {
				DEF.add(var);
			}
		}
	}
	
	/**
	 * Calculates {@link #USE} using the information within this block only.
	 */
	void calcUse() {
		USE.clear();
		for (int i = 0; i < refPoints.size(); i++) {
			RefPoint rp = refPoints.get(i);
			String var = rp.variable;
			boolean isRef = true;
			for (int j = 0; j < defPoints.size(); j++) {
				DefPoint dp = defPoints.get(j);
				if (dp.lineNumber > rp.lineNumber)
					break;
				if (dp.variable.equals(var)) {
					isRef = false;
					break;
				}
			}
			if (isRef) {
				USE.add(var);
			}
		}
	}
	// ========== active-var analysis end ==========
	
	// ========== for du-link analysis ==========
	/**
	 * Calculates {@link #D_USE} using the information within this block only.
	 */
	void calcDUse() {
		D_USE.clear();
		Set<String> defined = new HashSet<String>();
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			if (point instanceof DefPoint) {
				defined.add(point.variable);
			} else if (point instanceof RefPoint 
					&& !defined.contains(point.variable)) {
				D_USE.add((RefPoint)point);
			}
		}
	}
	// ========== du-link analysis end ==========
	
	// ========== for binop expression analysis ==========
	/**
	 * Calculates {@link #E_GEN} using the information within this block only.
	 */
	void calcEGen() {
		E_ALL.clear();
		E_GEN.clear();
		for (int lineNumber = firstLine; lineNumber <= lastLine; ) {
			Code code = originalCode.getCodeByLineNumber(lineNumber);
			DualExp de = ISA.getDualExp(code.codeOp);
			if (de != null) {
				DualExpInstance dei = new DualExpInstance(de, code);
				E_ALL.add(dei);
				boolean isGen = true;
				for (DefPoint dp : defPoints) {
					if (dp.lineNumber <= lineNumber) continue;
					if (dp.variable.equals(dei.X) || dp.variable.equals(dei.Y)) {
						// killed in block
						isGen = false;
						break;
					}
				}
				if (isGen) {
					E_GEN.add(dei);
				}
			}
			lineNumber += ISA.getLength(code.codeOp);
		}
	}
	
	/**
	 * Calculates {@link #E_KILL} using the information within this block and 
	 * information provided by the param all.
	 * @param all The whole set of {@link minijava.intermediate.DualExpInstance
	 * DualExpInstance} in current code context.
	 */
	void calcEKill(Set<DualExpInstance> all) {
		E_KILL.clear();
		for (DualExpInstance dei : all) {
			if (E_GEN.contains(dei)) continue;
			for (int i = defPoints.size()-1; i >= 0; i--) {
				DefPoint dp = defPoints.get(i);
				if (dp.variable.equals(dei.X) || dp.variable.equals(dei.Y)) {
					E_KILL.add(dei);
					break;
				}
			}
		}
	}
	// ========== binop expression analysis end ==========
	
	// ========== for single assignment analysis ==========
	/**
	 * Calculates {@link #C_GEN} using the information within this block only.
	 */
	void calcCGen() {
		C_ALL.clear();
		C_GEN.clear();
		for (int lineNumber = firstLine; lineNumber <= lastLine; ) {
			Code code = originalCode.getCodeByLineNumber(lineNumber);
			SingleAssignment sa = ISA.getSingleAssignment(code.codeOp);
			if (sa != null) {
				SingleAsgnInstance sai = new SingleAsgnInstance(sa, code);
				C_ALL.add(sai);
				boolean isGen = true;
				for (DefPoint dp : defPoints) {
					if (dp.lineNumber <= lineNumber) continue;
					if (dp.variable.equals(sai.X) || dp.variable.equals(sai.Y)) {
						// killed in block
						isGen = false;
						break;
					}
				}
				if (isGen) {
					C_GEN.add(sai);
				}
			}
			lineNumber += ISA.getLength(code.codeOp);
		}
	}	
	
	/**
	 * Calculates {@link #C_KILL} using the information within this block and 
	 * information provided by the param all.
	 * @param all The whole set of {@link minijava.intermediate.DualExpInstance
	 * DualExpInstance} in current code context.
	 */
	void calcCKill(Set<SingleAsgnInstance> all) {
		C_KILL.clear();
		for (SingleAsgnInstance sai : all) {
			if (C_ALL.contains(sai)) continue;
			for (int i = defPoints.size()-1; i >= 0; i--) {
				DefPoint dp = defPoints.get(i);
				if (dp.variable.equals(sai.X) || dp.variable.equals(sai.Y)) {
					C_KILL.add(sai);
					break;
				}
			}
		}
	}
	// ========== single assignment analysis end ==========
	
	@Override
	public String toString() {
		String strPre = "[";
		for (CodeBlock block : pre) {
			strPre += block.firstLine+", ";
		}
		strPre += "]";
		
		String strNext = "[";
		for (CodeBlock block : next) {
			strNext += block.firstLine+", ";
		}
		strNext += "]";
	
		return "(first: "+firstLine+"; last: "+lastLine+"; pre: "+strPre+"; next: "+strNext+")";
	}
	
	/**
	 * Used to establish a jump-to and jump-from relationship between blocks
	 * when initializing the flow graph.
	 * @param from
	 * @param to
	 */
	static void flow(CodeBlock from, CodeBlock to) {
		from.next.add(to);
		to.pre.add(from);
	}
}
