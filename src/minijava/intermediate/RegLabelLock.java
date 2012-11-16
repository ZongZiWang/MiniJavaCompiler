package minijava.intermediate;

import util.TypeChange;
import minijava.binary.ISA;
import minijava.typecheck.symboltable.MethodTable;

/**
 * Register and Goto's Label Lock/Unlock
 * @author ZongZiWang
 *
 */
public class RegLabelLock {
	/**
	 * lock all codes' goto's label
	 */
	public static void LabelLockAll() {
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode nowTargetCode = TargetCode.methods.get(i);
			LabelLock(nowTargetCode);
		}
	}
	/**
	 * lock nowTargetCode's all codes' goto's label <br />
	 * just convert relative line number to gotoDst and gotoSrc
	 * @param nowTargetCode
	 */
	public static void LabelLock(TargetCode nowTargetCode) {
		for (int j = 0; j < nowTargetCode.codeSize(); j++) {
			Code code = nowTargetCode.getCodeByIndex(j);
			switch (code.getCodeOp()) {
			case 0x28:
				code.setGotoDst(nowTargetCode.getCodeByLineNumber(code.getLineNumber()+Integer.parseInt(code.getParamValue("AA"))*2));
				code.getGotoDst().addGotoSrc(code);
				break;
			case 0x29:
				code.setGotoDst(nowTargetCode.getCodeByLineNumber(code.getLineNumber()+Integer.parseInt(code.getParamValue("AAAA"))*2));
				code.getGotoDst().addGotoSrc(code);
				break;
			case 0x2a:
				code.setGotoDst(nowTargetCode.getCodeByLineNumber(code.getLineNumber()+Integer.parseInt(code.getParamValue("AAAAAAAA"))*2));
				code.getGotoDst().addGotoSrc(code);
				break;
			case 0x34:
				code.setGotoDst(nowTargetCode.getCodeByLineNumber(code.getLineNumber()+Integer.parseInt(code.getParamValue("CCCC"))*2));
				code.getGotoDst().addGotoSrc(code);
				break;
			case 0x35:
				code.setGotoDst(nowTargetCode.getCodeByLineNumber(code.getLineNumber()+Integer.parseInt(code.getParamValue("CCCC"))*2));
				code.getGotoDst().addGotoSrc(code);
				break;
			case 0x38:
				code.setGotoDst(nowTargetCode.getCodeByLineNumber(code.getLineNumber()+Integer.parseInt(code.getParamValue("BBBB"))*2));
				code.getGotoDst().addGotoSrc(code);
				break;
			case 0x39:
				code.setGotoDst(nowTargetCode.getCodeByLineNumber(code.getLineNumber()+Integer.parseInt(code.getParamValue("BBBB"))*2));
				code.getGotoDst().addGotoSrc(code);
				break;

			default:
				break;
			}
		}
	}
	/**
	 * unlock all codes' goto's label
	 */
	public static void LabelUnlockAll() {
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode nowTargetCode = TargetCode.methods.get(i);
			LabelUnlock(nowTargetCode);
		}
	}
	/**
	 * unlock nowTargetCode's all codes' goto's label <br />
	 * just convert gotoDst to relative line number
	 * @param nowTargetCode
	 */
	public static void LabelUnlock(TargetCode nowTargetCode) {
		RecalculateLineNumber(nowTargetCode);
		for (int j = 0; j < nowTargetCode.codeSize(); j++) {
			Code code = nowTargetCode.getCodeByIndex(j);
			code.codeIdx = j;
			switch (code.getCodeOp()) {
			case 0x28:
				code.parameterMap.put("AA", (code.getGotoDst().getLineNumber()-code.getLineNumber())/2+"");
				break;
			case 0x29:
				code.parameterMap.put("AAAA", (code.getGotoDst().getLineNumber()-code.getLineNumber())/2+"");
				break;
			case 0x2a:
				code.parameterMap.put("AAAAAAAA", (code.getGotoDst().getLineNumber()-code.getLineNumber())/2+"");
				break;
			case 0x34:
				code.parameterMap.put("CCCC", (code.getGotoDst().getLineNumber()-code.getLineNumber())/2+"");
				break;
			case 0x35:
				code.parameterMap.put("CCCC", (code.getGotoDst().getLineNumber()-code.getLineNumber())/2+"");
				break;
			case 0x38:
				code.parameterMap.put("BBBB", (code.getGotoDst().getLineNumber()-code.getLineNumber())/2+"");
				break;
			case 0x39:
				code.parameterMap.put("BBBB", (code.getGotoDst().getLineNumber()-code.getLineNumber())/2+"");
				break;

			default:
				break;
			}
		}
	}
	/**
	 * recalculate all codes' line number
	 */
	public static void RecalculateLineNumberAll() {
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode nowTargetCode = TargetCode.methods.get(i);
			RecalculateLineNumber(nowTargetCode);
		}
	}
	/**
	 * recalculate nowTargetCode's all codes' line number
	 * @param nowTargetCode
	 */
	public static void RecalculateLineNumber(TargetCode nowTargetCode) {
		nowTargetCode.setLineNumber(0);
		for (int j = 0; j < nowTargetCode.codeSize(); j++) {
			Code code = nowTargetCode.getCodeByIndex(j);
			code.lineNumber = nowTargetCode.getLineNumber();
			nowTargetCode.setLineNumber(nowTargetCode.getLineNumber()+ISA.getLength(code.getCodeOp()));
		}
	}
	/**
	 * lock all codes' this/parameter register
	 */
	public static void RegLockAll() {
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode nowTargetCode = TargetCode.methods.get(i);
			RegLock(nowTargetCode);
		}
	}
	/**
	 * lock nowTargetCode's all codes' this/parameter register
	 * @param nowTargetCode
	 */
	public static void RegLock(TargetCode nowTargetCode) {
		MethodTable mt = nowTargetCode.getMethodTable();
		int paramReg = mt.getRegNum()-mt.getParamLength();
		for (int j = 0; j < nowTargetCode.codeSize(); j++) {
			Code code = nowTargetCode.getCodeByIndex(j);
			for (String key: ISA.getRegKey(code)) {
				String value = code.getParamTable().get(key);
				if (value != null && TypeChange.isInt(value)) {
					int reg = Integer.parseInt(value);
					if (reg == paramReg-1 && !mt.getMethodName().equals("main")) code.getParamTable().put(key, "this");
					if (reg >= paramReg) code.getParamTable().put(key, mt.getParamType(reg-paramReg).getVarName());
				}
			}
		}
	}
	/**
	 * unlock all codes' this/parameter register
	 */
	public static void RegUnlockAll() {
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode nowTargetCode = TargetCode.methods.get(i);
			RegUnlock(nowTargetCode);
		}
	}
	/**
	 * unlock nowTargetCode's all codes' this/parameter register
	 * @param nowTargetCode
	 */
	public static void RegUnlock(TargetCode nowTargetCode) {
		MethodTable mt = nowTargetCode.getMethodTable();
		int paramReg = mt.getRegNum()-mt.getParamLength();
		for (int j = 0; j < nowTargetCode.codeSize(); j++) {
			Code code = nowTargetCode.getCodeByIndex(j);
			for (String key: ISA.getRegKey(code)) {
				String value = code.getParamTable().get(key);
				if (value != null && !TypeChange.isInt(value)) {
					if (value.equals("this")) {
						code.getParamTable().put(key, paramReg-1+"");
						mt.setThisRegisterNumber(paramReg-1);
					} else {
						int paramIdx;
						for (paramIdx = 0; paramIdx < mt.getParamLength(); paramIdx++) {
							if (mt.getParamType(paramIdx).getVarName().equals(value)) break;
						}
						mt.getParamType(paramIdx).setReg(paramReg+paramIdx+"");
						code.getParamTable().put(key, paramReg+paramIdx+"");
					}
				}
			}
		}
	}
}
