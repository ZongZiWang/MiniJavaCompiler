package minijava.intermediate;

import java.util.*;

import util.Mode;
import util.Optimize;
import util.TypeChange;

import minijava.binary.*;
import minijava.intermediate.FlowGraph.BlockAnalysisException;
import minijava.typecheck.symboltable.*;


/**
 * TargetCode contains all Code for current method
 * @author ZongZiWang  you only need to use static method
 *
 */
public class TargetCode 
{
	/**
	 * now method's TargetCode
	 */
	static public TargetCode nowMethodTargetCode = null;
	/**
	 * all TargetCodes
	 */
	static public List<TargetCode> methods = new ArrayList<TargetCode>();
	/**
	 * initialize before new MiniJava Compilation
	 */
	static public void init() {
		nowMethodTargetCode = null;
		methods = new ArrayList<TargetCode>();
	}
	/**
	 * create new method's TargetCode
	 * @param currentMethodTable
	 */
	static public void NewMethod(MethodTable currentMethodTable)
	{
		nowMethodTargetCode = new TargetCode(currentMethodTable);
		methods.add(nowMethodTargetCode);
		nowMethodTargetCode.reservedRegNum = ApplyResource.init(currentMethodTable.getInvokeVirtualRegNum());
		for (TypeTable tt: currentMethodTable.globalTable.getClassTable(currentMethodTable.getClassName()).getMemberVariables().values()) {
			tt.initValue();
		}
	}
	/**
	 * 
	 * @param _codeOp first AddCode()  then Add all the Parameters by AddParam()
	 * @return Index of the Code
	 */
	static public int AddCode(int _codeOp, CodeStructure _codeBlock)
	{
		nowMethodTargetCode.nowCode = new Code(_codeOp, nowMethodTargetCode.codeLineNumber, nowMethodTargetCode.codeList.size());
		nowMethodTargetCode.codeList.add(nowMethodTargetCode.nowCode);
		nowMethodTargetCode.codeLineNumber += ISA.getLength(_codeOp);
		if (_codeBlock.getCodeIdx() == 0xffff) _codeBlock.setCodeIdx(nowMethodTargetCode.codeList.size()-1);
		return nowMethodTargetCode.codeList.size()-1;
	}
	/**
	 * 
	 * @param key		such as 'A' 'B' 'C' 'D' ...
	 * @param value		the String you Decide
	 */
	static public void AddParam(String key, String value)
	{
		nowMethodTargetCode.nowCode.parameterMap.put(key, value);
	}
	/**
	 * 
	 * @param key		such as 'A' 'B' 'C' 'D'
	 * @param value		the String this parameter's type	
	 */
	static public void AddType(String key, String value)
	{
		nowMethodTargetCode.nowCode.addType(key, value);
	}
	/**
	 * 
	 * @param _codeIdx	indicate which code the parameter is inserted
	 * @param key		such as 'A' 'B' 'C' 'D' ...
	 * @param value		the String you Decide
	 */
	static public void AddParam(int _codeIdx, String key, String value)
	{
		nowMethodTargetCode.codeList.get(_codeIdx).parameterMap.put(key, value);
	}
	/**
	 * 
	 * @param _codeIdx	indicate which code the gotoDst is inserted
	 * @param dst		destination code
	 */
	static public void AddGotoDst(int _codeIdx, Code dst) {
		nowMethodTargetCode.codeList.get(_codeIdx).setGotoDst(dst);
	}
	/**
	 * 
	 * @param _codeIdx
	 * @return codeOp of codes[_codeIdx]
	 */
	public int getCodeOp(int _codeIdx) {
		return nowMethodTargetCode.codeList.get(_codeIdx).getCodeOp();
	}
	/**
	 * Constructor
	 * @param currentMethodTable
	 */
	public TargetCode(MethodTable currentMethodTable) 
	{
		codeList = new ArrayList<Code>();
		codeLineNumber = 0;
		nowMethodTable = currentMethodTable;
	}
	
	private List<Code> codeList;
	private int codeLineNumber;
	private Code nowCode;
	private MethodTable nowMethodTable;
	private int reservedRegNum;
	/**
	 * 
	 * @return reserved register number
	 */
	public int getReservedRegNum() {
		return reservedRegNum;
	}
	/**
	 * set current code line number as '_lineNumber'
	 * @param _lineNumber
	 */
	public void setLineNumber(int _lineNumber) {
		codeLineNumber = _lineNumber;
	}
	/**
	 * 
	 * @return current code line number
	 */
	public int getLineNumber() {
		return codeLineNumber;
	}
	/**
	 * convert CodeStructure to first line number of this CodeStructure
	 * @param cs
	 * @return first line number of this CodeStructure
	 */
	public static int CS2LineNumber(CodeStructure cs) {
		return nowMethodTargetCode.getCodeByIndex(cs.getCodeIdx()).getLineNumber();
	}
	/**
	 * convert CodeStructure to code length of this CodeStructure
	 * @param cs
	 * @return code length of this CodeStructure
	 */
	public static int CS2CodeLength(CodeStructure cs) {
		return ISA.getLength(nowMethodTargetCode.getCodeByIndex(cs.getCodeIdx()).getCodeOp());
	}
	/**
	 * 
	 * @param _codeIdx
	 * @param key
	 * @return parameter value of code[_codeIdx]'s parameter[key]
	 */
	public String getCodeParamValue(int _codeIdx, String key) {
		return nowMethodTargetCode.codeList.get(_codeIdx).getParamValue(key);
	}
	
	static private ClassTable currentClassTable;
	static private int classNum;
	static private int methodNum;
	/**
	 * initialize before show intermediate code
	 */
	private static void initShowCode() {
		currentClassTable = null;
		classNum = 0;
		methodNum = 0;
	}
	/**
	 * show all intermediate codes
	 */
	public static void showAllCode() {
		initShowCode();
		for (int i = 0; i < methods.size(); i++) {
			methods.get(i).showCode();
		}
	}
	/**
	 * show nowMethodTargetCode intermediate codes
	 */
	private void showCode() {
		if (!Mode.isDebugMode()) return ;
		ClassTable ct = nowMethodTable.globalTable.getClassTable(nowMethodTable.getClassName());
		if (ct != currentClassTable) {
			currentClassTable = ct;
			diplayClassInfo(ct);
			classNum++;
			methodNum = 0;
		}
		displayMethodInfo(nowMethodTable);
		methodNum++;
	}
	/**
	 * display 'ct' class information
	 * @param ct
	 */
	private void diplayClassInfo(ClassTable ct) {
		System.out.println("Class #"+classNum);
		String classDescriptor = ct.globalTable.dexPrinter.GetTypeNameByIdx(ct.globalTable.dexPrinter.GetTypeIdx(ct.getClassName()));
		System.out.println("  Class descriptor  : '"+classDescriptor+"'");
		System.out.println("  Access flags      : 0x0000 ()");
		System.out.println("  Superclass        : '"+TypeChange.toDexType(ct.getParentClassName())+"'");
		System.out.println("  Interfaces        -");
		System.out.println("  Static fields     -");
		System.out.println("  Instance fields   -");
		int cnt = 0;
		for (String ttKeys: ct.getMemberVariables().keySet()) displayFieldInfo(cnt++, ttKeys, ct.getMemberVariables().get(ttKeys), classDescriptor);
		System.out.println("  Direct methods    -");
		System.out.println("    #0              : (in "+classDescriptor+")");
		System.out.println("      name          : '<init>'");
		System.out.println("      type          : '()V'");
		System.out.println("      access        : 0x10000 (CONSTRUCTOR)");
		System.out.println("      code          -");
		System.out.println("      registers     : 1");
		System.out.println("      ins           : 1");
		System.out.println("      outs          : 1");
		System.out.println("      insns size    : 4 16-bit code units");
		System.out.println("      catches       : (none)");
		System.out.println("      positions     : ");
		System.out.println("      0x0000 line=?");
		System.out.println("      locals        : ");
		System.out.println("        0x0000 - 0x0004 reg=0 this "+classDescriptor);
		
	}
	/**
	 * display 'mt' method information
	 * @param mt
	 */
	private void displayMethodInfo(MethodTable mt) {
		System.out.println("  Virtual methods   -");
		System.out.println("    #"+methodNum+"              : (in "+TypeChange.toDexType(mt.getClassName())+")");
		System.out.println("      name          : '"+mt.getMethodName()+"'");
		System.out.print("      type          : '(");
		for (int i = 0; i < mt.getParamLength(); i++) System.out.print(TypeChange.toDexType(mt.getParamType(i).getTypeName()));
		System.out.println(")"+TypeChange.toDexType(mt.getReturnType().getTypeName())+"'");
		System.out.println("      access        : 0x0001 (PUBLIC)");
		System.out.println("      registers     : "+mt.getRegNum());
		System.out.println("      ins           : "+mt.getInRegNum());
		System.out.println("      outs          : "+mt.getOutRegNum());
		System.out.println("      insns size    : "+codeLineNumber/2+" 16-bit code units");
		System.out.println("      catches       : (none)");
		System.out.println("      positions     : ");
		System.out.println("      locals        : ");
		System.out.println("      code          -");

		for (int i = 0; i < codeList.size(); i++) {
			System.out.println(ISA.getAsmCode(codeList.get(i), mt));
		}
		
	}
	/**
	 * display 'fieldName' field information
	 * @param currentFieldNum
	 * @param fieldName
	 * @param tt
	 * @param classDescriptor
	 */
	private void displayFieldInfo(int currentFieldNum, String fieldName, TypeTable tt, String classDescriptor) {
		System.out.println("    #"+currentFieldNum+"              : (in "+classDescriptor+")");
		System.out.println("      name          : '"+fieldName+"'");
		System.out.println("      type          : '"+TypeChange.toDexType(tt.getTypeName())+"'");
		System.out.println("      access        : 0x0000 ()");
		
	}
	/**
	 * 
	 * @param i
	 * @return method i's TargetCode
	 */
	public static TargetCode getMethodTargetCode(int i) {
		return methods.get(i);
	}
	/**
	 * 
	 * @return now MethodTable
	 */
	public MethodTable getMethodTable() {
		return nowMethodTable;
	}
	/**
	 * 
	 * @return how many code there is
	 */
	public int codeSize() {
		return codeList.size();
	}
	/**
	 * 
	 * @param _codeIdx
	 * @return codes[_codeIdx]
	 */
	public Code getCode(int _codeIdx) {
		return getCodeByIndex(_codeIdx);
	}
	/**
	 * 
	 * @param _codeIdx
	 * @return codes[_codeIdx]
	 */
	public Code getCodeByIndex(int _codeIdx) {
		return codeList.get(_codeIdx);
	}
	/**
	 * 
	 * @param _lineNumber
	 * @return code whose line number is '_lineNumber'
	 */
	public Code getCodeByLineNumber(int _lineNumber) {
		for (int i = 0; i < codeList.size(); i++) {
			if (codeList.get(i).getLineNumber() == _lineNumber) return codeList.get(i);
		}
		return null;
	}
	/**
	 * show Binary Code
	 */
	public void showBinaryCode() {
		if (!Mode.isDebugMode()) return ;
		for (int i = 0; i < codeList.size(); i++) {
			System.out.println(ISA.getBinCode(codeList.get(i)));
		}
	}
	/**
	 * insert Binary Code to Dex File
	 */
	public void insertBinaryCodeToDex() {
		byte[] codeBytes = new byte[codeLineNumber];
		int nowBytes = 0;
		for (int i = 0; i < codeList.size(); i++) {
			String codeBin = ISA.getBinCode(codeList.get(i));
			for (int j = 0; j < codeBin.length(); j += 2) {
				codeBytes[nowBytes++] = TypeChange.StringToBytes(codeBin.substring(j, j+2));
			}
		}
		nowMethodTable.globalTable.dexPrinter.AddMethodCodeToMethod(nowMethodTable.getClassName(), nowMethodTable.getMethodName(), (short)nowMethodTable.getRegNum(), (short)nowMethodTable.getInRegNum(), (short)nowMethodTable.getOutRegNum(), (short)(codeLineNumber/2), codeBytes);
	}
	/**
	 * reallocate register number <br />
	 * should be called after intermediate code generated
	 * @return
	 */
	public static boolean reallocateRegister() {
		boolean optimized = false;
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode tc = TargetCode.methods.get(i);
			optimized = (tc.getMethodTable().updateRegNum
					(GraphColoringMethod.reallocateRegister(tc, 65536, tc.reservedRegNum)))
					|| optimized;
		}
		return optimized;
	}
	/**
	 * optimize loops
	 * @return
	 */
	public static boolean optimizeLoops() {
		try {
			return FlowGraph.optimizeLoops();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * optimize static single assignments
	 * @return
	 */
	public static boolean optimizeStaticSingleAssignments() {
		try {
			return FlowGraph.optimizeStaticSingleAssignments();
		} catch (BlockAnalysisException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * remove unused definition
	 * @return
	 */
	public static boolean removeUnusedDefinition() {
		try {
			return FlowGraph.removeUnusedDefinition();
		} catch (BlockAnalysisException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * insert extendCode before or after code[posIdx] <br />
	 * depends on the line number of extendCode
	 * @param extendCode
	 * @param posIdx
	 */
	public void insertCode(Code extendCode, int posIdx) {
		if (extendCode.getLineNumber() > getCodeByIndex(posIdx).getLineNumber()) {
			codeList.add(posIdx+1, extendCode);
		} else {
			Code tmp = getCodeByIndex(posIdx);
			codeList.set(posIdx, extendCode);
			codeList.add(posIdx+1, tmp);
			extendCode.gotoSrc = tmp.gotoSrc;
			if (tmp.gotoSrc != null) for (Code srcCode: tmp.gotoSrc) {
				srcCode.setGotoDst(extendCode);
			}
			tmp.gotoSrc = null;
		}
	}
	/**
	 * delete code[posIdx]
	 * @param posIdx
	 */
	public void deleteCode(int posIdx) {
		if (posIdx < codeSize()-1) {
			if (codeList.get(posIdx+1).gotoSrc != null) {
				if (codeList.get(posIdx).gotoSrc != null) codeList.get(posIdx+1).gotoSrc.addAll(codeList.get(posIdx).gotoSrc);
			} else codeList.get(posIdx+1).gotoSrc = codeList.get(posIdx).gotoSrc;
			if (codeList.get(posIdx).gotoSrc != null) for (Code srcCode: codeList.get(posIdx).gotoSrc) {
				srcCode.setGotoDst(codeList.get(posIdx+1));
			}
		}
		codeList.remove(posIdx);
	}
	/**
	 * 
	 * @return if the last code is 'move' or 'const' instruction for a variable
	 */
	public boolean isLastMoveOrConstToVar() {
		int op = codeList.get(codeSize()-1).codeOp;
		switch (op) {
		case 0x01:
		case 0x07:
		case 0x12:
			return true;
		case 0x02:
		case 0x08:
		case 0x13:
		case 0x14:
			return true;
		case 0x03:
		case 0x09:
			return true;

		default:
			break;
		}
		return !Optimize.isO1();
	}
	/**
	 * refresh all codes' register number <br />
	 * e.g. 0,3,4,6 => 0,1,2,3
	 * @return true => refreshed
	 */
	public static boolean refreshRegistersAll() {
		boolean flag = false;
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode tc = TargetCode.methods.get(i);
			flag |= tc.refreshRegisters();
		}
		return flag;
	}
	/**
	 * refresh now Method TargetCode's all codes' register number <br />
	 * e.g. 0,3,4,6 => 0,1,2,3
	 * @return true => refreshed
	 */
	public boolean refreshRegisters() {
		Set<Integer> allRegs = new HashSet<Integer>();
		PriorityQueue<Integer> regUsed = new PriorityQueue<Integer>();
		List<Integer> regList = new ArrayList<Integer>();
		Hashtable<Integer, Integer> regTable = new Hashtable<Integer, Integer>();
		for (int j = 0; j < codeSize(); j++) {
			for (String key : ISA.getRegKey(codeList.get(j))) {
				String regName = codeList.get(j).parameterMap.get(key);
				if (regName != null && TypeChange.isInt(regName)) {
					int reg = TypeChange.parseInt(regName);
					allRegs.add(reg);
				}
			}
		}
		regUsed.addAll(allRegs);
		while (!regUsed.isEmpty()) regList.add(regUsed.poll());
		
		nowMethodTable.updateRegNum(regList.size());
		if (regList.size() == 0 || regList.get(regList.size()-1) == regList.size()-1) return false;
		
		for (int i = 0; i < regList.size(); i++) regTable.put(regList.get(i), i);
		for (int j = 0; j < codeSize(); j++) {
			Hashtable<String, String> tmp = new Hashtable<String, String>();
			for (String key : ISA.getRegKey(codeList.get(j))) {
				String regName = codeList.get(j).parameterMap.get(key);
				if (regName != null && TypeChange.isInt(regName)) {
					int reg = TypeChange.parseInt(regName);
					if (reg != regTable.get(reg)) {
						tmp.put(key, ""+regTable.get(reg));
					}
				}
			}
			codeList.get(j).parameterMap.putAll(tmp);
		}
		return true;
	}
	
}