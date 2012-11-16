package minijava.intermediate;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import minijava.binary.ISA;

/**
 * Intermediate Code Structure
 * @author ZongZiWang
 *
 */
public class Code
{
	int codeIdx;
	int codeOp;
	int lineNumber;
	Hashtable<String, String> parameterMap;
	Hashtable<String, String> typeMap;
	Code gotoDst;
	Set<Code> gotoSrc;
	/**
	 * Duplicate Constructor
	 * @param _code
	 */
	public Code(Code _code) {
		codeIdx = _code.codeIdx;
		codeOp = _code.codeOp;
		lineNumber = _code.lineNumber;
		parameterMap = new Hashtable<String, String>(_code.parameterMap);
		gotoDst = _code.gotoDst;
		if (gotoSrc != null) gotoSrc = new HashSet<Code>(_code.gotoSrc);
	}
	/**
	 * Constructor
	 * @param _codeOp
	 * @param _lineNumber
	 * @param _codeIdx
	 */
	public Code(int _codeOp, int _lineNumber, int _codeIdx)
	{
		codeOp = _codeOp;
		codeIdx = _codeIdx;
		lineNumber = _lineNumber;
		parameterMap = new Hashtable<String, String>();
		gotoDst = null;
	}
	public int getCodeOp() {
		return codeOp;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public Hashtable<String, String> getParamTable() {
		return parameterMap;
	}
	/**
	 * 
	 * @param string
	 * @return parameter value for key 'string'
	 */
	public String getParamValue(String string) {
		return parameterMap.get(string);
	}
	public Code getGotoDst() {
		return gotoDst;
	}
	/**
	 * set goto destination Code Structure
	 * @param dst
	 */
	public void setGotoDst(Code dst) {
		gotoDst = dst;
	}
	/**
	 * replace old destination register with new destination register
	 * @param oldReg
	 * @param newReg
	 * @return true => replace succeeded <br />
	 * false => replace failed
	 */
	public boolean replace(String oldReg, String newReg) {
		String[] strArr = ISA.getDst(this);
		for (int i = 0; i < strArr.length; i++) {
			if (parameterMap.get(strArr[i]).equals(oldReg)) {
				parameterMap.put(strArr[i], newReg);
				return true;
			}
		}
		return false;
	}
	/**
	 * replace old key with new key
	 * @param oldKey
	 * @param newKey
	 * @return true => replace succeeded <br />
	 * false => replace failed
	 */
	public boolean replaceKey(String oldKey, String newKey) {
		if (!parameterMap.keySet().contains(oldKey)) return false;
		parameterMap.put(newKey, parameterMap.get(oldKey));
		parameterMap.remove(oldKey);
		return true;
	}
	/**
	 * add goto source Code Structures
	 * @param code
	 * @return true => add succeeded
	 */
	public boolean addGotoSrc(Code code) {
		if (gotoSrc == null) gotoSrc = new HashSet<Code>();
		gotoSrc.add(code);
		return true;
	}
	/**
	 * clear goto source Code Structures
	 * @return true => clear succeeded
	 */
	public boolean clearGotoSrc() {
		gotoSrc = null;
		return true;
	}
	/**
	 * insert (key, value) for parameter of Code Structrue
	 * @param key
	 * @param value
	 */
	public void addType(String key, String value) {
		if (typeMap == null) typeMap = new Hashtable<String, String>();
		typeMap.put(key, value);
	}
}