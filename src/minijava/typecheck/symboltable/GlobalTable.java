package minijava.typecheck.symboltable;

import java.util.Hashtable;
import java.util.Vector;

import minijava.compiler.ErrorInfo;
import minijava.dexbuilder.DexPrinter;
import minijava.typecheck.TypeCheck;

/**
 * Identified by main class name. Usage: Insert and find all classes.<br />
 * Used via <strong>"(any instance of SymbolTable).globalTable"</strong> (Recommended)
 * @author ZongZiWang
 *
 */
public class GlobalTable extends SymbolTable {
	
	public DexPrinter dexPrinter = new DexPrinter("classes.dex");
	
	protected Hashtable<String, ClassTable> classTableTable;
	protected String mainClassName;
	/**
	 * Constructor
	 * @param mainclassName
	 */
	public GlobalTable(String mainclassName) {
		mainClassName = mainclassName;
		globalTable = this;
		classTableTable = new Hashtable<String, ClassTable>();
	}

	public boolean setMainClassName(String className) {
		if (!mainClassName.equals("")) return false;
		mainClassName = className;
		return true;
	}
	/**
	 * 
	 * @return main class name
	 */
	public String getMainClassName() {
		return mainClassName;
	}
	/**
	 * insert classTable of class 'classname'
	 * @param className
	 * @param classTable
	 * @return true => insert succeeded
	 */
	public boolean insertClass (String className, ClassTable classTable) {
		if (classTableTable.containsKey(className)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.MutipleDeclarationException:");
			ErrorInfo.addlnInfo("\tEncountered \"class "+className+"\" at line"+classTable.lineNumber);
			TypeCheck.setError();
			return false;
		}
		classTableTable.put(className, classTable);
		return true;
	}
	/**
	 * 
	 * @param className
	 * @return class table of class 'className'
	 */
	public ClassTable getClassTable (String className) {
		return classTableTable.get(className);
	}

	/**
	 * Check if there is inheritance loop during the source file.<br />
	 * If there is, the Compiler should be shut down right now.
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkInheritanceLoop() {
		Hashtable<String, Integer> checkedClasses = new Hashtable<String, Integer>();
		Vector<String> inheritanceClass = new Vector<String>();
		Integer now = 1;
		for (ClassTable classTable: classTableTable.values()) {
			if (checkedClasses.containsKey(classTable.className)) continue;
			ClassTable tmp = classTable;
			inheritanceClass.clear();
			while (classTableTable.containsKey(tmp.parentClassName)) {
				inheritanceClass.add(tmp.className);
				if (checkedClasses.containsKey(tmp.className) && checkedClasses.get(tmp.className).equals(now)) {
					ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.InheritanceLoopException:");
					ErrorInfo.addInfo("\tEncountered Inheritance Loop \"class "+classTable.className);
					for (int i = 1; i < inheritanceClass.size(); i++) ErrorInfo.addInfo(" extends class "+inheritanceClass.get(i));
					ErrorInfo.addlnInfo("\" at line"+classTable.lineNumber);
					TypeCheck.setError();
					return true;
				}
				checkedClasses.put(tmp.className, now);
				tmp = classTableTable.get(tmp.parentClassName);
			}
			now++;
		}
		return false;
	}
	/**
	 * Check if there is undefined class during the source file.
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkUndefinedClass() {
		boolean flag = false;
		for (ClassTable classTable: classTableTable.values()) {
			if (classTable.getClassName().equals(mainClassName)) continue;
			if (classTable.checkUndefinedClass()) flag = true;
		}
		return flag;
	}
	/**
	 * Check if there is override error during the source file.
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkOverrideError() {
		boolean flag = false;
		for (ClassTable classTable: classTableTable.values()) {
			if (classTable.checkOverrideError()) flag = true;
		}
		return flag;
	}
	/**
	 * Check if there is unused variables during the source file.<br />
	 * <strong>It works when "EnableWarning" of TypeCheckVisitor is true</strong>
	 * @return true => there is<br />
	 * false => there is not
	 */
	public boolean checkUnusedVariables() {
		boolean flag = false;
		for (ClassTable classTable: classTableTable.values()) {
			if (classTable.checkUnusedVariables()) flag = true;
		}
		return flag;
	}
	
	/**
	 * Check if "superClass" is the ancestor of "subClass"
	 * @param subClass
	 * @param superClass
	 * @return true => "subClass" is the sub class of "superClass"<br />
	 * false => is not
	 */
	public boolean isSubClass(String subClass, String superClass) {
		while (getClassTable(subClass) != null && !subClass.equals(superClass)) {
			subClass = getClassTable(subClass).getParentClassName();
		}
		return getClassTable(subClass) != null;
	}
	/**
	 * Check if the type of "param" matches the type of "type"<br />
	 * @param type
	 * @param param
	 * @return true => if the types are same or the type of "type" is the ancestor of the type of "param"<br />
	 * false => does not match
	 */
	public boolean isTypeMatch(TypeTable type, TypeTable param) {
		if (type.isSameType(param)) return true;
		if (!(type.isClassType() && (param.isClassType()))) return false;
		if (isSubClass(param.getTypeName(),type.getTypeName())) return true;
		return false;
	}


}