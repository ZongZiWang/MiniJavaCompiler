package minijava.typecheck.symboltable;

import java.util.ArrayList;

/**
 * Usage: Insert and find parameters of the method.<br />
 * @author ZongZiWang
 *
 */
public class ParamTable extends SymbolTable {
	protected ArrayList<String> paramNames;
	protected ArrayList<TypeTable> paramTypes;
	protected int length;
	/**
	 * constructor
	 * @param methodname
	 * @param classname
	 * @param linenumber
	 * @param globaltable
	 */
	public ParamTable(String methodname, String classname, int linenumber, GlobalTable globaltable) {
		methodName = methodname;
		className = classname;
		lineNumber = linenumber;
		globalTable = globaltable;
		paramNames = new ArrayList<String>();
		paramTypes = new ArrayList<TypeTable>();
		length = 0;
	}
	/**
	 * insert parameter, whose name is 'paramName' and type is 'paramType'
	 * @param paramName
	 * @param paramType
	 * @return true => insert succeeded
	 */
	public boolean insertParam (String paramName, TypeTable paramType) {
		paramNames.add(paramName);
		paramTypes.add(paramType);
		length++;
		return true;
	}
	/**
	 * get length of parameters
	 * @return length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * get name of parameter i
	 * @param i
	 * @return name
	 */
	public String getParamName(int i) {
		if (i < length) return paramNames.get(i);
		return null;
	}
	/**
	 * get type of parameter i
	 * @param i
	 * @return TypeTable type
	 */
	public TypeTable getParamType(int i) {
		if (i < length) return paramTypes.get(i);
		return null;
	}
}
