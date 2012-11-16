package minijava.typecheck.symboltable;

/**
 * All other symbol table classes' parent, including fundamental elements' insert/set/get method<br />
 * @author ZongZiWang
 *
 */
public class SymbolTable {
	public GlobalTable globalTable;
	protected int lineNumber;
	protected String className;
	protected String methodName;
	protected String symbolName;
	protected String varName;
	/**
	 * insert variable, whose name is 'varName' and type is 'varType', at line 'linenumber'
	 * @param varName
	 * @param varType
	 * @param lineNumber
	 * @return
	 */
	public boolean insertVariable (String varName, TypeTable varType, int lineNumber) {
		return false;
	}
	/**
	 * 
	 * @param varName
	 * @return variable type of variable 'varName'
	 */
	public TypeTable getVariableType (String varName) {
		return null;
	}
	public boolean setGlobalTable(GlobalTable globaltable) {
		if (globalTable != null) return false;
		globalTable = globaltable;
		return true;
	}
	public GlobalTable getGlobalTable() {
		return globalTable;
	}
	public boolean setLineNumber(int linenumber) {
		if (lineNumber != 0) return false;
		lineNumber = linenumber;
		return true;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public boolean setClassName(String classname) {
		if (className != null) return false;
		className = classname;
		return true;
	}
	public String getClassName() {
		return className;
	}
	public boolean setMethodName(String methodname) {
		if (methodName !=null) return false;
		methodName = methodname;
		return true;
	}
	public String getMethodName() {
		return methodName;
	}
	public boolean setTypeName(String symbolname) {
		symbolName = symbolname;
		return true;
	}
	public String getTypeName() {
		return symbolName;
	}
	public boolean setVarName(String varname) {
		varName = varname;
		return true;
	}
	public String getVarName() {
		return varName;
	}
}
