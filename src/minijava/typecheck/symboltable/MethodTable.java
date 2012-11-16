package minijava.typecheck.symboltable;

import java.util.Hashtable;

import minijava.compiler.ErrorInfo;
import minijava.compiler.WarningInfo;
import minijava.typecheck.TypeCheck;

/**
 * Identified by method name and class name. Usage: Insert and find parameters and variables of this method.<br />
 * Used via <strong>"(any instance of SymbolTable).globalTable.getClassTable(class name).getMethodTable(this method name)"</strong> (Recommended)
 * @author ZongZiWang
 *
 */
public class MethodTable extends SymbolTable {
	protected TypeTable returnType;
	protected ParamTable paramTable;
	
	protected Hashtable<String, Integer> localVariablesLine;
	protected Hashtable<String, TypeTable> localVariables;
	
	/**
	 * constructor
	 * @param methodname
	 * @param classname
	 * @param returntype
	 * @param linenumber
	 * @param globaltable
	 */
	public MethodTable(String methodname, String classname, TypeTable returntype, int linenumber, GlobalTable globaltable) {
		methodName = methodname;
		className = classname;
		returnType = returntype;
		lineNumber = linenumber;
		globalTable = globaltable;
		paramTable = new ParamTable(methodName, className, lineNumber, globalTable);
		localVariablesLine = new Hashtable<String, Integer>();
		localVariables = new Hashtable<String, TypeTable>();
	}
	
	public boolean setReturnType(TypeTable returntype) {
		if (!returnType.getTypeName().equals("")) return false;
		returnType = returntype;
		return true;
	}
	public TypeTable getReturnType() {
		return returnType;
	}
	/**
	 * insert parameter, whose name is 'paramName' and type is 'paramType', at line 'linenumber'
	 * @param paramName
	 * @param paramType
	 * @param lineNumber
	 * @return
	 */
	public boolean insertParam (String paramName, TypeTable paramType, int lineNumber) {
		if (localVariables.containsKey(paramName)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.MutipleDeclarationException: in Method "+methodName+" of Class "+className);
			ErrorInfo.addlnInfo("\tEncountered \""+paramType.getTypeName()+" "+paramName+";\" at line"+lineNumber);
			TypeCheck.setError();
			return false;
		}
		localVariablesLine.put(paramName, lineNumber);
		localVariables.put(paramName, paramType);
		paramTable.insertParam(paramName, paramType);
		paramType.setInited(TypeTable.INITED);
		return true;
	}
	@Override
	public boolean insertVariable (String varName, TypeTable varType, int lineNumber) {
		if (localVariables.containsKey(varName)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.MutipleDeclarationException: in Method "+methodName+" of Class "+className);
			ErrorInfo.addlnInfo("\tEncountered \""+varType.getTypeName()+" "+varName+";\" at line"+lineNumber);
			TypeCheck.setError();
			return false;
		}
		localVariablesLine.put(varName, lineNumber);
		localVariables.put(varName, varType);
		return true;
	}
	/**
	 * 
	 * @param varName
	 * @return linenumber of variable 'varName'
	 */
	public int getVariableLine (String varName) {
		return localVariablesLine.get(varName);
	}
	@Override
	public TypeTable getVariableType (String varName) {
		return localVariables.get(varName);
	}
	/**
	 * 
	 * @param i
	 * @return type of parameter i
	 */
	public TypeTable getParamType (int i) {
		return paramTable.getParamType(i);
	}
	/**
	 * 
	 * @param varName
	 * @return if variable 'varName' is initialized
	 */
	public Boolean getVariableInited (String varName) {
		return localVariables.get(varName).getInited();
	}
	public void setVariableInited (String varName) {
		localVariables.get(varName).setInited(TypeTable.INITED);
	}
	/**
	 * 
	 * @param varName
	 * @return if variable 'varName' is used
	 */
	public Boolean getVariableUsed (String varName) {
		return localVariables.get(varName).getUsed();
	}
	public void setVariableUsed (String varName) {
		localVariables.get(varName).setUsed(true);
	}
	/**
	 * convert parameters' types as String[]
	 * @return String[] for parameters' types
	 */
	public String[] getParamTypeArray() {
		String[] strArr = new String[getParamLength()];
		for (int i = 0; i < getParamLength(); i++) strArr[i] = getParamType(i).getTypeName();
		return strArr;
	}
	/**
	 * 
	 * @return parameters' length
	 */
	public int getParamLength() {
		return paramTable.getLength();
	}
	/**
	 * 
	 * @return local variable number
	 */
	public int getLocalVarNum() {
		return localVariables.size()-paramTable.getLength();
	}

	
	/**
	 * Check if "paramList" matches this method's paramTable
	 * @param paramList
	 * @return true => if types of parameters of "paramList" matches those of this method's paramTable<br />
	 * false => if not
	 */
	public boolean isParamListCompatible (ParamTable paramList) {
		if (paramTable.getLength() != paramList.getLength()) return false;
		for (int i = 0; i < paramTable.getLength(); i++) {
			if (!globalTable.isTypeMatch(paramTable.getParamType(i), paramList.getParamType(i))) return false;
		}
		return true;
	}
	/**
	 * Check if this method can override "superMethod".
	 * @param superMethod
	 * @return true => if the type of the return value and all types of parameters are same as those of "superMethod"<br />
	 * false => if not
	 */
	public boolean canOverride (MethodTable superMethod) {
		if (!returnType.isSameType(superMethod.returnType)) return false;
		if (paramTable.getLength() != superMethod.paramTable.getLength()) return false;
		for (int i = 0; i < paramTable.getLength(); i++) {
			if (!paramTable.getParamType(i).isSameType(superMethod.paramTable.getParamType(i))) return false;
		}
		return true;
	}
	/**
	 * Check if there is undefined class during the method.
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkUndefinedClass () {
		boolean flag = false;
		if ((returnType.isClassType()) && (globalTable.getClassTable(returnType.getTypeName()) == null)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.UndefinedClassExistException");
			ErrorInfo.addlnInfo("\tEncountered Undefined Class \"class "+returnType.getTypeName()+"\" at line "+returnType.getLineNumber());
			TypeCheck.setError();
			flag = true;
		}
		for (TypeTable typeTable: localVariables.values()) {
			if ((typeTable.isClassType()) && (globalTable.getClassTable(typeTable.getTypeName()) == null)) {
				ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.UndefinedClassExistException");
				ErrorInfo.addlnInfo("\tEncountered Undefined Class \"class "+typeTable.getTypeName()+"\" at line "+typeTable.getLineNumber());
				TypeCheck.setError();
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * Check if there is unused variables during the method.<br />
	 * <strong>It works when "EnableWarning" of TypeCheckVisitor is true</strong>
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkUnusedVariables() {
		boolean flag = false;
		for (String varName: localVariables.keySet()) {
			TypeTable varType = localVariables.get(varName);
			if (!varType.getUsed()) {
				WarningInfo.addlnInfo("Warning in thread \"main\" minijava.typecheck.UnusedWarning: in Method \""+methodName+"\" (of Class "+className+")");
				WarningInfo.addlnInfo("\tEncountered unused Identifier \""+varName+"\" at line "+varType.getLineNumber());
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * this's register number
	 */
	protected int thisRegisterNumber = 0;
	/**
	 * 
	 * @return this's register number
	 */
	public int getThisRegisterNumber()
	{
		return thisRegisterNumber;
	}
	public void setThisRegisterNumber(int thisRNumber)
	{
		thisRegisterNumber = thisRNumber;
	}
	/**
	 * out register number
	 */
	protected int outRegNum = 0;
	/**
	 * 
	 * @return out register number
	 */
	public int getOutRegNum() {
		return outRegNum;
	}
	/**
	 * maximize the out register number
	 * @param currentOutRegNum
	 */
	public void updateOutRegNum(int currentOutRegNum) {
		if (currentOutRegNum > outRegNum) outRegNum = currentOutRegNum;
	}
	/**
	 * max invoke virtual register number
	 */
	protected int invokeVirtualRegNum = 0;
	/**
	 * 
	 * @return max invoke virtual register number
	 */
	public int getInvokeVirtualRegNum() {
		return invokeVirtualRegNum;
	}
	/**
	 * maximize the max invoke virtual register number
	 * @param currentInvokeVirtualRegNum
	 */
	public void updateInvokeVirtualRegNum(int currentInvokeVirtualRegNum) {
		if (currentInvokeVirtualRegNum <= 5 && currentInvokeVirtualRegNum > invokeVirtualRegNum) invokeVirtualRegNum = currentInvokeVirtualRegNum;
	}
	/**
	 * in register number
	 */
	protected int inRegNum = 0;
	/**
	 * 
	 * @return in register number
	 */
	public int getInRegNum() {
		return inRegNum;
	}
	/**
	 * maximize the in register number
	 * @param currentInRegNum
	 */
	public void updateInRegNum(int currentInRegNum) {
		if (currentInRegNum > inRegNum) inRegNum = currentInRegNum;
	}
	/**
	 * total register number
	 */
	protected int regNum = 0;
	/**
	 * (static method should not count this's register)
	 * @return total register number of this method
	 */
	public int getRegNum() {
		if (getMethodName().equals("main")) return regNum+getParamLength();
		else return regNum+getParamLength()+1;
	}
	/**
	 * update the total register number
	 * @param currentRegNum
	 * @return if the total register number differs before
	 */
	public boolean updateRegNum(int currentRegNum) {
		if (regNum == currentRegNum) return false;
		regNum = currentRegNum;
		return true;
	}
}
