package minijava.intermediate;

import java.util.ArrayList;
import java.util.List;

import util.TypeChange;

/**
 * Check if register number is invalid
 * @author ZongZiWang
 *
 */
public class RegCheck {
	private static final int BEFORE = -1;
	private static final int AFTER = 1;
	private static final int INT = 0x02;
	private static final int OBJECT = 0x08;
	/**
	 * Check if all codes' register number is overflow
	 * @return
	 */
	public static boolean hasRegOverFlowAll() {
		boolean flag = false;
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode nowTargetCode = TargetCode.methods.get(i);
			flag = flag || hasRegOverFlow(nowTargetCode);
		}
		return flag;
	}
	
	private static List<Code> extendCodes;
	private static List<Code> related;
	
	/**
	 * Check if nowTargetCode's all codes' register number is overflow
	 * @param nowTargetCode
	 * @return true => there is at least one overflow
	 */
	public static boolean hasRegOverFlow(TargetCode nowTargetCode) {
		extendCodes = new ArrayList<Code>();
		related = new ArrayList<Code>();
		int nextSwap;
		
		boolean flag = false;
		
		for (int j = 0; j < nowTargetCode.codeSize(); j++) {
			nextSwap = 0;
			Code code = nowTargetCode.getCodeByIndex(j);
			switch (code.getCodeOp()) {
			case 0x01:
			case 0x07:
				if (overFlow(code, "A") || overFlow(code, "B")) {
					nowTargetCode.getCodeByIndex(j).codeOp++;
					nowTargetCode.getCodeByIndex(j).replaceKey("A", "AA");
					nowTargetCode.getCodeByIndex(j).replaceKey("B", "BBBB");
					flag = true;
				}
				break;
			case 0x02:
			case 0x08:
				if (overFlow(code, "AA")) {
					nowTargetCode.getCodeByIndex(j).codeOp++;
					nowTargetCode.getCodeByIndex(j).replaceKey("AA", "AAAA");
					flag = true;
				}
				break;
			case 0x28:
				if (overFlowNum(code, "AA")) {
					nowTargetCode.getCodeByIndex(j).codeOp++;
					nowTargetCode.getCodeByIndex(j).replaceKey("AA", "AAAA");
					flag = true;
				}
				break;
			case 0x29:
				if (overFlowNum(code, "AAAA")) {
					nowTargetCode.getCodeByIndex(j).codeOp++;
					nowTargetCode.getCodeByIndex(j).replaceKey("AAAA", "AAAAAAAA");
					flag = true;
				}
				break;
			case 0x12:
				if (overFlow(code, "A") || overFlowNum(code, "B")) {
					nowTargetCode.getCodeByIndex(j).codeOp++;
					nowTargetCode.getCodeByIndex(j).replaceKey("A", "AA");
					nowTargetCode.getCodeByIndex(j).replaceKey("B", "BBBB");
					flag = true;
				}
			case 0x13:
				if (overFlowNum(code, "BBBB")) {
					nowTargetCode.getCodeByIndex(j).codeOp++;
					nowTargetCode.getCodeByIndex(j).replaceKey("BBBB", "BBBBBBBB");
					flag = true;
				}
				break;
				
			case 0xd0:
			case 0xd1:
			case 0xd2:
			case 0xd5:
				if (overFlow(code, "A") || overFlow(code, "B")) {
					if (!overFlow(code, "CCCC", 2)) {
						nowTargetCode.getCodeByIndex(j).codeOp += 8;
						nowTargetCode.getCodeByIndex(j).replaceKey("A", "AA");
						nowTargetCode.getCodeByIndex(j).replaceKey("B", "BB");
						nowTargetCode.getCodeByIndex(j).replaceKey("CCCC", "CC");
						flag = true;
					} else {
						if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, INT);
						if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, AFTER, INT);
					}
				}
				break;
				
			case 0x6e:
				int b = TypeChange.parseInt(code.getParamValue("B"));
				
				if (overFlow(code, "D")) nextSwap = extendCode(code, "D", nextSwap, BEFORE, OBJECT);
				if (b > 1) if (overFlow(code, "E")) nextSwap = extendCode(code, "E", nextSwap, BEFORE, typeToInt(code.typeMap.get("E")));
				if (b > 2) if (overFlow(code, "F")) nextSwap = extendCode(code, "F", nextSwap, BEFORE, typeToInt(code.typeMap.get("F")));
				if (b > 3) if (overFlow(code, "G")) nextSwap = extendCode(code, "G", nextSwap, BEFORE, typeToInt(code.typeMap.get("G")));
				if (b > 4) if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, BEFORE, typeToInt(code.typeMap.get("A")));
				break;
			case 0x70:
				if (overFlow(code, "D")) nextSwap = extendCode(code, "D", nextSwap, BEFORE, OBJECT);
				break;
			case 0x71:
				if (overFlow(code, "D")) nextSwap = extendCode(code, "D", nextSwap, BEFORE, INT);
				break;
			case 0xff:
				if (overFlow(code, "E")) nextSwap = extendCode(code, "E", nextSwap, BEFORE, INT);
				break;
				
			case 0x21:
			case 0xb0:
			case 0xb1:
			case 0xb2:
			case 0xb5:
				if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, INT);
				if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, AFTER, INT);
				break;
			case 0x23:
				if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, INT);
				if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, AFTER, OBJECT);
				break;
			case 0x52:
			case 0x55:
				if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, OBJECT);
				if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, AFTER, INT);
				break;
			case 0x54:
				if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, OBJECT);
				if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, AFTER, OBJECT);
				break;

			case 0x34:
			case 0x35:
				if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, INT);
				if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, BEFORE, INT);
				break;
				
			case 0x59:
			case 0x5c:
				if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, OBJECT);
				if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, BEFORE, INT);
				break;
			case 0x5b:
				if (overFlow(code, "B")) nextSwap = extendCode(code, "B", nextSwap, BEFORE, OBJECT);
				if (overFlow(code, "A")) nextSwap = extendCode(code, "A", nextSwap, BEFORE, OBJECT);
				break;	

			default:
				break;
			}
		}
		
		int k = 0;
		for (int j = 0; j < nowTargetCode.codeSize(); j++) {
			for (; k < extendCodes.size() && related.get(k).equals(nowTargetCode.getCodeByIndex(j)); k++) {
				nowTargetCode.insertCode(extendCodes.get(k), j);
			}
		}
		
		return flag || (extendCodes.size() > 0);
	}
	
	/**
	 * 
	 * @param type
	 * @return type is INT or OBJECT
	 */
	private static int typeToInt(String type) {
		if (type.equals("int")) return INT;
		if (type.equals("boolean")) return INT;
		if (type.startsWith("^_^")) return INT;
		return OBJECT;
	}

	/**
	 * check if code's parameter[key] is overflow
	 * @param code
	 * @param key
	 * @return true => overflow
	 */
	protected static boolean overFlow(Code code, String key) {
		return overFlow(code, key, key.length());
	}
	/**
	 * check if code's parameter[key] is overflow more than length 'len'
	 * @param code
	 * @param key
	 * @param len
	 * @return true => overflow
	 */
	protected static boolean overFlow(Code code, String key, int len) {
		String value = code.getParamTable().get(key);
		if (value == null) return false;
		if (TypeChange.isInt(value)) {
			int reg = TypeChange.parseInt(value);
			if (reg < 0) reg = -reg;
			if (Integer.toHexString(reg).length() > len) {
				return true;
			}
		}
		return false;
	}
	/**
	 * check if code's parameter[key](a number) is overflow
	 * @param code
	 * @param key
	 * @return true => overflow
	 */
	protected static boolean overFlowNum(Code code, String key) {
		String value = code.getParamTable().get(key);
		if (value == null) return false;
		if (TypeChange.isInt(value)) {
			int reg = TypeChange.parseInt(value);
			if (key.length() == 1) {
				if (-0x8 <= reg && reg <= 0x7) return false;
			} else if (key.length() == 2) {
				if (-0x80 <= reg && reg <= 0x7f) return false;
			} else if (key.length() == 4) {
				if (-0x8000 <= reg && reg <= 0x7fff) return false;
			}
		}
		return true;
	}
	/**
	 * insert a new code for overflowed register number <br />
	 * code and key indicate which code's parameter[key] overflow <br />
	 * reg indicate new swap register number
	 * pos should be BEFORE or AFTER
	 * newOp indicate new code's codeOp
	 * @param code
	 * @param key
	 * @param reg
	 * @param pos
	 * @param newOp
	 * @return next swap resgiter number
	 */
	protected static int extendCode(Code code, String key, int reg, int pos, int newOp) {
		Code ec = new Code(newOp, code.getLineNumber()+pos, extendCodes.size());

		if (pos == BEFORE) {
			ec.parameterMap.put("AA", reg+"");
			ec.parameterMap.put("BBBB", code.getParamValue(key));
		} else {
			ec.parameterMap.put("AA", code.getParamValue(key));
			ec.parameterMap.put("BBBB", reg+"");
		}
		code.parameterMap.put(key, reg+"");
		extendCodes.add(ec);
		related.add(code);
		return reg+1;
	}
}
