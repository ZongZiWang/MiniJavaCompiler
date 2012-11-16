package minijava.intermediate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

import util.Optimize;
import util.TypeChange;
/**
 * Primary Registers Allocation
 * @author ZongZiWang
 *
 */
public class ApplyResource {
	protected static PriorityQueue<Integer> regPool = new PriorityQueue<Integer>();
	protected static Hashtable<Integer, Integer> kindTable = new Hashtable<Integer, Integer>();
	private static int reservedRegNum;
	private static int maxUsedRegNum;
	private static int whileMode;
	private static int invokeLevel;

	protected static List<List<String>> regs = new ArrayList<List<String>>();
	protected static List<List<String>> types = new ArrayList<List<String>>();
	protected static boolean[] regUsed = new boolean[0x10000];
	protected static List<Boolean> invokeRange = new ArrayList<Boolean>();
	protected static List<Integer> nowInvokeRangeReg = new ArrayList<Integer>();
	protected static List<Integer> invokeLens = new ArrayList<Integer>();
	/**
	 * Should initialize this registers poll each method!
	 * @param invokeVirtualRegNum
	 * @return reservedRegNum
	 */
	public static int init(int invokeVirtualRegNum) {
		reservedRegNum = Math.max(invokeVirtualRegNum, 2);
		maxUsedRegNum = 0;
		whileMode = 0;
		invokeLevel = 0;
		
		regPool.clear();
		for (int i = reservedRegNum; i < 0x10000; i++) regPool.add(i);
		kindTable.clear();
		
		regs.clear();
		types.clear();
		invokeRange.clear();
		nowInvokeRangeReg.clear();
		
		regUsed = new boolean[0x10000];
		
		return reservedRegNum;
	}
	/**
	 * set before WhileStatement
	 */
	public static void setWhileMode() {
		whileMode++;
	}
	/**
	 * 
	 * @return if during WhileStatement
	 */
	public static boolean getWhileMode() {
		return (whileMode > 0);
	}
	/**
	 * exit after WhileStatement
	 */
	public static void exitWhileMode() {
		whileMode--;
	}
	/**
	 * set before MessageSend
	 * @param paramLength
	 */
	public static void setInvokeMode(int paramLength) {
		int i;
		if (paramLength > 4) {
			//range doesn't need types
			types.add(null);
			
			//range
			invokeRange.add(true);
			int start = applyInterval(paramLength+1);
			nowInvokeRangeReg.add(start);
			for (i = 0; i < paramLength+1; i++) {
				regUsed[start+i] = true;
				kindTable.put(start+i, TEMP);
				regPool.remove(start+i);
			}
			maxUsedRegNum = maxUsedRegNum < start+paramLength ? start+paramLength : maxUsedRegNum;
		} else {
			//virtual need types
			types.add(new ArrayList<String>());
			
			//not range
			invokeRange.add(false);
			nowInvokeRangeReg.add(0);
		}
		regs.add(new ArrayList<String>());
		invokeLens.add(paramLength+1);
		invokeLevel++;
	}
	/**
	 * apply free registers for a interval
	 * @param intervalLength
	 * @return
	 */
	private static int applyInterval(int intervalLength) {
		int cnt = 0;
		for (int i = reservedRegNum; i < 0x10000; i++) {
			if (!regUsed[i]) {
				cnt++;
				if (cnt == intervalLength) return i-cnt+1;
			} else cnt = 0;
		}
		return 0;
	}
	/**
	 * exit after MessageSend
	 */
	public static void exitInvokeMode() {
		regs.remove(invokeLevel-1);
		types.remove(invokeLevel-1);
		invokeRange.remove(invokeLevel-1);
		nowInvokeRangeReg.remove(invokeLevel-1);
		invokeLens.remove(invokeLevel-1);
		invokeLevel--;
	}
	/**
	 * _kind should be VAR for variable or TEMP or temporary register
	 * @param _kind
	 * @return register number or name
	 */
	public static int getReg(int _kind) {
		while (regUsed[regPool.peek()]) regPool.poll();
		kindTable.put(regPool.peek(), _kind);
		regUsed[regPool.peek()] = true;
		maxUsedRegNum = maxUsedRegNum < regPool.peek() ? regPool.peek(): maxUsedRegNum;
		return regPool.poll();
	}
	/**
	 * 
	 * @return max used register number for now
	 */
	public static int getMaxUsedRegNum() {
		return maxUsedRegNum;
	}
	/**
	 * register type for variable
	 */
	static public final int VAR = 0;
	/**
	 * register type for temporary register
	 */
	static public final int TEMP = 1;
	/**
	 * recycle useless register
	 * @param regStr
	 */
	public static void recycleReg(String regStr) {
		if (Optimize.isO0()) {
			int reg;
			if (regStr != null && TypeChange.isInt(regStr)) reg = Integer.parseInt(regStr);
			else return ;
			if (kindTable.get(reg) == null ||  kindTable.get(reg) == VAR) return;
			kindTable.remove(reg);
			regPool.add(reg);
			regUsed[reg] = false;
		}
	}
	/**
	 * 
	 * @return is invoke-virtual/range
	 */
	public static boolean isInvokeRange() {
		return invokeRange.get(invokeLevel-1);
	}
	/**
	 * during invoke-virtual/range
	 * @return which register number should be used now
	 */
	public static int getNowInvokeReg() {
		return nowInvokeRangeReg.get(invokeLevel-1);
	}
	/**
	 * insert invoke register number or name and register type
	 * @param outReg
	 * @param outType
	 */
	public static void addNowOutReg(String outReg, String outType) {
		regs.get(invokeLevel-1).add(outReg);
		if (isInvokeRange()) {
			nowInvokeRangeReg.set(invokeLevel-1, nowInvokeRangeReg.get(invokeLevel-1)+1);
		} else {
			types.get(invokeLevel-1).add(outType);
		}
	}
	/**
	 * 
	 * @param i
	 * @return invoke register number or name for parameter i
	 */
	public static String getNowOutReg(int i) {
		return regs.get(invokeLevel-1).get(i);
	}
	/**
	 * 
	 * @param i
	 * @return invoke register type for parameter i
	 */
	public static String getNowOutType(int i) {
		return types.get(invokeLevel-1).get(i);
	}
}