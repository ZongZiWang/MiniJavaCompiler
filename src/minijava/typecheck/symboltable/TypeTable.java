package minijava.typecheck.symboltable;

import util.TypeChange;
import minijava.intermediate.ApplyResource;

/**
 * Represent a type or the type of a class/method return value/variable.
 * @author ZongZiWang
 *
 */
public class TypeTable extends SymbolTable {
	public static final int UNINITED = 0;
	public static final int INITING = 1;
	public static final int INITED = 2;
	protected int inited;
	protected Boolean used;
	
	/**
	 * Constructor through type name.
	 * @param name
	 * @param methodname
	 * @param classname
	 * @param linenumber
	 * @param globaltable
	 */
	public TypeTable(String varname, String typename, String methodname, String classname, int linenumber, GlobalTable globaltable) {
		this.setVarName(varname);
		this.setTypeName(typename);
		this.setClassName(classname);
		this.setMethodName(methodname);
		this.setLineNumber(linenumber);
		this.setGlobalTable(globaltable);
		inited = UNINITED;
		used = false;
	}

	/**
	 * Constructor through type number: <br />
	 * i: 0 => "int[]"; 1 => "boolean"; 2 => "int"; 3 => "unnamedclass"
	 * @param i
	 * @param methodname
	 * @param classname
	 * @param linenumber
	 * @param globaltable
	 */
	public TypeTable(String varname, int i, String methodname, String classname, int linenumber, GlobalTable globaltable) {
		this.setVarName(varname);
		if (i == 0) this.setTypeName("int[]");
		if (i == 1) this.setTypeName("boolean");
		if (i == 2) this.setTypeName("int");
		if (i == 3) this.setTypeName("unnamedclass");
		this.setClassName(classname);
		this.setMethodName(methodname);
		this.setLineNumber(linenumber);
		this.setGlobalTable(globaltable);
		inited = UNINITED;
		used = false;
	}
	public void setInited(int init) {
		inited = init;
	}
	public Boolean getInited() {
		return (inited == INITED);
	}
	public void setUsed(Boolean use) {
		used = use;
	}
	public Boolean getUsed() {
		return used;
	}
	/**
	 * check if this type is class type
	 * @return true => is class type <br />
	 * false => is not
	 */
	public boolean isClassType() {
		if (symbolName.equals("int")) return false;
		if (symbolName.equals("int[]")) return false;
		if (symbolName.equals("boolean")) return false;
		return true;
	}
	/**
	 * check if this type is same as the type 'other'
	 * @param other
	 * @return true => is same as the type 'other'
	 * false => is not
	 */
	public boolean isSameType(TypeTable other) {
		return symbolName.equals(other.getTypeName());
	}

	/**
	 * this variable is a field
	 */
	static public final int FIELD = 0;
	/**
	 * this variable is a parameter
	 */
	static public final int PARAM = 1;
	/**
	 * this variable is a local variable
	 */
	static public final int LOCAL = 2;
	int kind = -1;
	/**
	 * set the kind of this variable, FIELD or PARAM or LOCAL
	 * @param _kind
	 */
	public void setKind(int _kind) {
		kind = _kind;
		if (_kind == FIELD) setInited(INITED);
	}
	/**
	 * 
	 * @return variable kind
	 */
	public int getKind() {
		return kind;
	}
	String reg = null;
	/**
	 * (if needed, allocate register)
	 * @return register number or name
	 */
	public String getReg() {
		if (reg != null) return reg;
		if (kind != PARAM) reg = ""+ApplyResource.getReg(ApplyResource.VAR);
		else reg = varName;
		return reg;
	}
	/**
	 * if needed, allocate register
	 */
	public void setReg() {
		if (reg == null || !TypeChange.isInt(reg)) reg = ""+ApplyResource.getReg(ApplyResource.VAR);
	}
	/**
	 * set register as '_reg'
	 * @param _reg
	 */
	public void setReg(String _reg) {
		reg = _reg;
	}

	/**
	 * initialize the variable before convert to intermediate code
	 */
	public void initValue() {
		reg = null;
		value = null;
		varState = FREE;
	}
	String value = null;
	/**
	 * 
	 * @return variable immediately value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * set the immediately of this TypeTable
	 * @param calculatedValue as the immediately
	 */
	public void setValue(String calculatedValue) {
		value = calculatedValue;
	}
	/**
	 * this field is not loaded
	 */
	public static final int FREE = 0;
	/**
	 * this field is loaded (can be referred by register)
	 */
	public static final int LOADED = 1;
	/**
	 * this field is dirty (should be written back before jump)
	 */
	public static final int DIRTY = 2;
	int varState;
	/**
	 * set the state of this TypeTable, FREE or LOADED or DIRTY
	 * @param state
	 */
	public void setState(int state) {
		varState = state;
	}
	/**
	 * 
	 * @return variable state
	 */
	public int getState() {
		return varState;
	}

}
