package minijava.typecheck.symboltable;

import java.util.Hashtable;

import minijava.compiler.ErrorInfo;
import minijava.typecheck.TypeCheck;

/**
 * Identified by class name. Usage: Insert and find attributes and methods of this class.<br />
 * Used via <strong>"(any instance of SymbolTable).globalTable.getClassTable(this class name)"</strong> (Recommended)
 * @author ZongZiWang
 *
 */
public class ClassTable extends SymbolTable {
	protected String parentClassName;
	
	protected Hashtable<String, MethodTable> memberMethods;
	protected Hashtable<String, Integer> memberVariablesLine;
	protected Hashtable<String, TypeTable> memberVariables;
	/**
	 * Constructor
	 * @param classname
	 * @param parentclassName
	 * @param linenumber
	 * @param globaltable
	 */
	public ClassTable(String classname, String parentclassName, int linenumber, GlobalTable globaltable) {
		className = classname;
		parentClassName = parentclassName;
		lineNumber = linenumber;
		globalTable = globaltable;
		memberMethods = new Hashtable<String, MethodTable>();
		memberVariablesLine = new Hashtable<String, Integer>();
		memberVariables = new Hashtable<String, TypeTable>();
	}
	
	public boolean setParentClassName(String parentclassName) {
		if (!parentClassName.equals("")) return false;
		parentClassName = parentclassName;
		return true;
	}
	/**
	 * 
	 * @return parent class name
	 */
	public String getParentClassName() {
		return parentClassName;
	}
	@Override
	public boolean insertVariable (String varName, TypeTable varType, int lineNumber) {
		if (memberVariables.containsKey(varName)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.MutipleDeclarationException: in Class "+className);
			ErrorInfo.addlnInfo("\tEncountered \""+varType.symbolName+" "+varName+";\" at line"+lineNumber);
			TypeCheck.setError();
			return false;
		}
		memberVariablesLine.put(varName, lineNumber);
		memberVariables.put(varName, varType);
		return true;
	}
	/**
	 * insert MethodTable 'method' of method 'methodName'
	 * @param methodName
	 * @param method
	 * @return
	 */
	public boolean insertMethod (String methodName, MethodTable method) {
		if (memberMethods.containsKey(methodName)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.MutipleDeclarationException: in Class "+className);
			ErrorInfo.addlnInfo("\tEncountered \""+method.returnType.symbolName+" "+methodName+"();\" at line"+method.lineNumber);
			TypeCheck.setError();
			return false;
		}
		memberMethods.put(methodName, method);
		return true;
	}
	/**
	 * 
	 * @param varName
	 * @return linenumber of variable 'varName'
	 */
	public int getVariableLine (String varName) {
		if (memberVariables.get(varName) != null) return memberVariablesLine.get(varName);
		if (parentClassName != "java.lang.Object") return globalTable.getClassTable(parentClassName).getVariableLine(varName);
		return 0;
	}
	@Override
	public TypeTable getVariableType (String varName) {
		if (memberVariables.get(varName) != null) return memberVariables.get(varName);
		if (globalTable.getClassTable(parentClassName) != null)
			return globalTable.getClassTable(parentClassName).getVariableType(varName);
		return null;
	}
	/**
	 * 
	 * @param methodName
	 * @return MethodTable of method 'methodName'
	 */
	public MethodTable getMethodTable (String methodName) {
		if (memberMethods.get(methodName) != null) return memberMethods.get(methodName);
		if (globalTable.getClassTable(parentClassName) != null)
			return globalTable.getClassTable(parentClassName).getMethodTable(methodName);
		return null;
	}
	
	/**
	 * Check if there is undefined class during the class.
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkUndefinedClass () {
		boolean flag = false;
		if (globalTable.getClassTable(parentClassName) == null && !parentClassName.equals("java.lang.Object")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.UndefinedClassExistException");
			ErrorInfo.addlnInfo("\tEncountered Undefined Class \"class "+parentClassName+"\" at line "+lineNumber);
			TypeCheck.setError();
			flag = true;
		}
		for (TypeTable typeTable: memberVariables.values()) {
			if ((typeTable.isClassType()) && (globalTable.getClassTable(typeTable.getTypeName()) == null)) {
				ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.UndefinedClassExistException");
				ErrorInfo.addlnInfo("\tEncountered Undefined Class \"class "+typeTable.getTypeName()+"\" at line "+typeTable.lineNumber);
				TypeCheck.setError();
				flag = true;
			}
		}
		for (MethodTable methodTable: memberMethods.values()) {
			if (methodTable.methodName.equals("main")) continue;
			if (methodTable.checkUndefinedClass()) flag = true;
		}
		return flag;
	}
	/**
	 * Check if there is override error during the class.
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkOverrideError () {
		boolean flag = false;
		for (MethodTable methodTable: memberMethods.values()) {
			ClassTable currentClassTable = globalTable.getClassTable(parentClassName);
			while (currentClassTable != null) {
				if (currentClassTable.memberMethods.containsKey(methodTable.methodName)) {
					MethodTable superMethod = currentClassTable.memberMethods.get(methodTable.methodName);
					if (!methodTable.canOverride(superMethod)) {
						ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.OverrideErrorException");
						ErrorInfo.addlnInfo("\tEncountered Override Error while \"method "+methodTable.getMethodName()+"\" of class "+className+" overrides that of class "+currentClassTable.getClassName()+" at line "+methodTable.getLineNumber());
						TypeCheck.setError();
						flag = true;
					}
				}
				currentClassTable = globalTable.getClassTable(currentClassTable.parentClassName);
			}
		}
		return flag;
	}
	/**
	 * Check if there is unused variables during the class.<br />
	 * <strong>It works when "EnableWarning" of TypeCheckVisitor is true</strong>
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkUnusedVariables() {
		boolean flag = false;
		for (MethodTable methodTable: memberMethods.values()) {
			if (methodTable.methodName.equals("main")) continue;
			if (methodTable.checkUnusedVariables()) flag = true;
		}
		return flag;
	}

	/**
	 * 
	 * @return MemberVariables' Hashtable
	 */
	public Hashtable<String, TypeTable> getMemberVariables() {
		return memberVariables;
	}

}