package minijava.intermediate;

/**
 * Code Structure for Intermediate Code Visitor Return Type
 * @author ZongZiWang
 *
 */
public class CodeStructure {
	String context;
	String expReg;
	String expType;
	String idId;
	int literalVal;
	boolean isField = false;
	int codeIdx;
	/**
	 * context: "declare", "state", "express", "identify" means declaration, statement, expression, identifier;<br />
	 * also, you can add some context if you want 
	 * @param _context
	 */
	public CodeStructure(String _context) {
		context = _context;
		codeIdx = 0xffff;
	}
	/**
	 * expression register number
	 * @param reg
	 * @return true => set succeeded
	 */
	public boolean setReg(String reg) {
		expReg = reg;
		return true;
	}
	/**
	 * expression type
	 * @param type
	 * @return true => set succeeded
	 */
	public boolean setType(String type) {
		expType = type;
		return true;
	}
	/**
	 * identifier id
	 * @param id
	 * @return true => set succeeded
	 */
	public boolean setId(String id) {
		if (context.equals("identify")) idId = id;
		else return false;
		return true;
	}
	/**
	 * the first Code of this CodeStructure is TargetCode's No._codeIdx Code
	 * @param _codeIdx
	 * @return true => set succeeded
	 */
	public boolean setCodeIdx(int _codeIdx) {
		codeIdx = _codeIdx;
		return true;
	}
	/**
	 * set identifier is field
	 */
	public void setIsField() {
		isField = true;
	}
	/**
	 * set value of expression's immediately
	 * @param val
	 */
	public void setLiteralVal(int val) {
		literalVal = val;
	}
	/**
	 * 
	 * @return value of expression's immediately
	 */
	public int getLiteralVal() {
		return literalVal;
	}
	/**
	 * 
	 * @return if identifier is field
	 */
	public boolean getIsField() {
		return isField;
	}
	/**
	 * 
	 * @return expression register number
	 */
	public String getReg() {
		return expReg;
	}
	/**
	 * 
	 * @return expression type
	 */
	public String getType() {
		return expType;
	}
	/**
	 * 
	 * @return identifier id
	 */
	public String getId() {
		if (context.equals("identify")) return idId;
		return null;
	}
	/**
	 * 
	 * @return the first Code of this CodeStructure is TargetCode's which Code
	 */
	public int getCodeIdx() {
		return codeIdx;
	}
}
