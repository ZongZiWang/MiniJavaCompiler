package minijava.binary;

import minijava.intermediate.*;
import minijava.typecheck.symboltable.SymbolTable;
import util.TypeChange;

/**
 * Instruction Set Architecture for TargetCode to Assembly Code or Binary Code <br />
 * @author ZongZiWang
 *
 */
public class ISA {
	/**
	 * instruction length for each opcode
	 */
	static final int len[] = new int[] {
		2,2,4,6,2,4,6,2,//0x00 ~ 0x07
		4,6,2,2,2,2,2,2,//0x08 ~ 0x0f
		2,2,2,4,6,4,4,6,//0x10 ~ 0x17
		10,4,4,6,4,2,2,4,//0x08 ~ 0x1f
		4,2,4,4,6,6,6,2,//0x20 ~ 0x27
		2,4,6,6,6,4,4,4,//0x28 ~ 0x2f
		4,4,4,4,4,4,4,4,//0x30 ~ 0x37
		4,4,4,4,4,4,0,0,//0x38 ~ 0x3f
		0,0,0,0,4,4,4,4,//0x40 ~ 0x47
		4,4,4,4,4,4,4,4,//0x48 ~ 0x4f
		4,4,4,4,4,4,4,4,//0x50 ~ 0x57
		4,4,4,4,4,4,4,4,//0x58 ~ 0x5f
		4,4,4,4,4,4,4,4,//0x60 ~ 0x67
		4,4,4,4,4,4,6,6,//0x68 ~ 0x6f
		6,6,6,0,6,6,6,6,//0x70 ~ 0x77
		6,0,2,2,2,2,2,2,//0x78 ~ 0x7f
		2,2,2,2,2,2,2,2,//0x80 ~ 0x87
		2,2,2,2,2,2,2,2,//0x88 ~ 0x8f
		4,4,4,4,4,4,4,4,//0x90 ~ 0x97
		4,4,4,4,4,4,4,4,//0x98 ~ 0x9f
		4,4,4,4,4,4,4,4,//0xa0 ~ 0xa7
		4,4,4,4,4,4,4,4,//0xa8 ~ 0xaf
		2,2,2,2,2,2,2,2,//0xb0 ~ 0xb7
		2,2,2,2,2,2,2,2,//0xb8 ~ 0xbf
		2,2,2,2,2,2,2,2,//0xc0 ~ 0xc7
		2,2,2,2,2,2,2,2,//0xc8 ~ 0xcf
		4,4,4,4,4,4,4,4,//0xd0 ~ 0xd7
		4,4,4,4,4,4,4,4,//0xd8 ~ 0xdf
		4,4,4,0,0,0,0,0,//0xe0 ~ 0xe7
		0,0,0,0,0,0,0,0,//0xe8 ~ 0xef
		0,0,0,0,0,0,0,0,//0xf0 ~ 0xf7
		0,0,0,0,0,0,0,0,//0xf8 ~ 0xff
		};
	/**
	 * get instruction length of code whose opcode is '_codeOp' <br />
	 * @param _codeOp
	 * @return instruction length
	 */
	public static int getLength(int _codeOp) {
		switch (_codeOp) {
			case 0xff: return 6;
			default: return len[_codeOp];
		}
	}
	/**
	 * get dual expression of code whose opcode is '_codeOp' <br />
	 * @param _codeOp
	 * @return dual expression like 'Z = X op Y'
	 */
	public static DualExp getDualExp(int _codeOp) {
		switch (_codeOp) {
		case 0x44:
		case 0x90:
		case 0x91:
		case 0x92:
		case 0x95:
			
		case 0xd8:
		case 0xd9:
		case 0xda:
		case 0xdd:
			return new DualExp("BB", _codeOp, "CC", "AA");
		case 0xd0:
		case 0xd1:
		case 0xd2:
		case 0xd5:
			return new DualExp("B", _codeOp, "CCCC", "A");

		default:
			return null;
		}
	}
	/**
	 * get single assignment of code whose opcode is '_codeOp' <br />
	 * @param _codeOp
	 * @return single assignment like 'X = Y'
	 */
	public static SingleAssignment getSingleAssignment(int _codeOp) {
		switch (_codeOp) {
		case 0x01:
		case 0x07:
			return new SingleAssignment("A", "B");
		case 0x02:
		case 0x08:
			return new SingleAssignment("AA", "BBBB");
		case 0x03:
		case 0x09:
			return new SingleAssignment("AAAA", "BBBB");
		default:
			return null;
		}
	}
	/**
	 * get all register keys of code whose opcode is '_codeOp' <br />
	 * @param code
	 * @return String[] for all register keys
	 */
	public static String[] getRegKey(Code code) {
		if (code.getCodeOp() == 0x74) return new String[]{"CCCC"};
		String[] strSrc = getSrc(code);
		String[] strDst = getDst(code);
		String[] strArr = new String[strSrc.length+strDst.length];
		for (int i = 0; i < strSrc.length; i++) strArr[i] = strSrc[i];
		for (int i = 0; i < strDst.length; i++) strArr[i+strSrc.length] = strDst[i];
		return strArr;
	}
	/**
	 * get source register keys of code whose opcode is '_codeOp' <br />
	 * @param code
	 * @return String[] for source register keys
	 */
	public static String[] getSrc(Code code) {
		String[] strArr;
		switch (code.getCodeOp()) {
		case 0x01:
			return new String[]{"B"};
		case 0x02:
			return new String[]{"BBBB"};
		case 0x03:
			return new String[]{"BBBB"};
		case 0x07:
			return new String[]{"B"};
		case 0x08:
			return new String[]{"BBBB"};
		case 0x09:
			return new String[]{"BBBB"};
		case 0x0a:
			return new String[]{};
		case 0x0c:
			return new String[]{};
		case 0x0e:
			return new String[]{};
		case 0x0f:
			return new String[]{"AA"};
		case 0x11:
			return new String[]{"AA"};
		case 0x12:
			return new String[]{};
		case 0x13:
			return new String[]{};
		case 0x14:
			return new String[]{};
		case 0x21:
			return new String[]{"B"};
		case 0x22:
			return new String[]{};
		case 0x23:
			return new String[]{"B"};
		case 0x28:
			return new String[]{};
		case 0x29:
			return new String[]{};
		case 0x2a:
			return new String[]{};
		case 0x34:
			return new String[]{"A","B"};
		case 0x35:
			return new String[]{"A","B"};
		case 0x38:
			return new String[]{"AA"};
		case 0x39:
			return new String[]{"AA"};
		case 0x44:
			return new String[]{"AA", "BB", "CC"};
		case 0x4b:
			return new String[]{"AA", "BB", "CC"};
		case 0x52:
			return new String[]{"B"};
		case 0x54:
			return new String[]{"B"};
		case 0x55:
			return new String[]{"B"};
		case 0x59:
			return new String[]{"A", "B"};
		case 0x5b:
			return new String[]{"A", "B"};
		case 0x5c:
			return new String[]{"A", "B"};
		case 0x62:
			return new String[]{};
		case 0x6e:
			int tmp6e = TypeChange.parseInt(TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("B")), 1));
			strArr = new String[tmp6e];
			strArr[0] = "D";
			if (tmp6e > 1) strArr[1] = "E";
			if (tmp6e > 2) strArr[2] = "F";
			if (tmp6e > 3) strArr[3] = "G";
			if (tmp6e > 4) strArr[4] = "A";
			return strArr;
		case 0x70:
			int tmp70 = TypeChange.parseInt(TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("B")), 1));
			strArr = new String[tmp70];
			strArr[0] = "D";
			if (tmp70 > 1) strArr[1] = "E";
			if (tmp70 > 2) strArr[2] = "F";
			if (tmp70 > 3) strArr[3] = "G";
			if (tmp70 > 4) strArr[4] = "A";
			return strArr;
		case 0x71:
			int tmp71 = TypeChange.parseInt(TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("B")), 1));
			strArr = new String[tmp71];
			strArr[0] = "D";
			if (tmp71 > 1) strArr[1] = "E";
			if (tmp71 > 2) strArr[2] = "F";
			if (tmp71 > 3) strArr[3] = "G";
			if (tmp71 > 4) strArr[4] = "A";
			return strArr;
		case 0x74:
			strArr = new String[3];
			strArr[0] = "range";//range for dealing with
			strArr[1] = "CCCC";
			strArr[2] = "AA";
			return strArr;

		case 0x90:
			return new String[]{"BB", "CC"};
		case 0x91:
			return new String[]{"BB", "CC"};
		case 0x92:
			return new String[]{"BB", "CC"};
		case 0x95:
			return new String[]{"BB", "CC"};
		case 0xb0:
			return new String[]{"A", "B"};
		case 0xb1:
			return new String[]{"A", "B"};
		case 0xb2:
			return new String[]{"A", "B"};
		case 0xb5:
			return new String[]{"A", "B"};
		case 0xd0:
			return new String[]{"B"};
		case 0xd1:
			return new String[]{"B"};
		case 0xd2:
			return new String[]{"B"};
		case 0xd5:
			return new String[]{"B"};
		case 0xd8:
			return new String[]{"BB"};
		case 0xd9:
			return new String[]{"BB"};
		case 0xda:
			return new String[]{"BB"};
		case 0xdd:
			return new String[]{"BB"};
		
		case 0xff:
			int tmpff = TypeChange.parseInt(TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("B")), 1));
			strArr = new String[tmpff];
			strArr[0] = "D";
			if (tmpff > 1) strArr[1] = "E";
			if (tmpff > 2) strArr[2] = "F";
			if (tmpff > 3) strArr[3] = "G";
			if (tmpff > 4) strArr[4] = "A";
			return strArr;
			
		default:
		case 0x00: 
			return new String[]{};
		}
	}
	/**
	 * get destination register keys of code whose opcode is '_codeOp' <br /> 
	 * @param code
	 * @return String[] for destination register keys
	 */
	public static String[] getDst(Code code) {
		switch (code.getCodeOp()) {
		case 0x01:
			return new String[]{"A"};
		case 0x02:
			return new String[]{"AA"};
		case 0x03:
			return new String[]{"AAAA"};
		case 0x07:
			return new String[]{"A"};
		case 0x08:
			return new String[]{"AA"};
		case 0x09:
			return new String[]{"AAA"};
		case 0x0a:
			return new String[]{"AA"};
		case 0x0c:
			return new String[]{"AA"};
		case 0x0e:
			return new String[]{};
		case 0x0f:
			return new String[]{};
		case 0x11:
			return new String[]{};
		case 0x12:
			return new String[]{"A"};
		case 0x13:
			return new String[]{"AA"};
		case 0x14:
			return new String[]{"AA"};
		case 0x21:
			return new String[]{"A"};
		case 0x22:
			return new String[]{"AA"};
		case 0x23:
			return new String[]{"A"};
		case 0x28:
			return new String[]{};
		case 0x29:
			return new String[]{};
		case 0x2a:
			return new String[]{};
		case 0x34:
			return new String[]{};
		case 0x35:
			return new String[]{};
		case 0x38:
			return new String[]{};
		case 0x39:
			return new String[]{};
		case 0x44:
			return new String[]{"AA"};
		case 0x4b:
			return new String[]{};
		case 0x52:
			return new String[]{"A"};
		case 0x54:
			return new String[]{"A"};
		case 0x55:
			return new String[]{"A"};
		case 0x59:
			return new String[]{};
		case 0x5b:
			return new String[]{};
		case 0x5c:
			return new String[]{};
		case 0x62:
			return new String[]{"AA"};
		case 0x6e:
			return new String[]{};
		case 0x70:
			return new String[]{};
		case 0x71:
			return new String[]{};
		case 0x74:
			return new String[]{};

		case 0x90:
			return new String[]{"AA"};
		case 0x91:
			return new String[]{"AA"};
		case 0x92:
			return new String[]{"AA"};
		case 0x95:
			return new String[]{"AA"};
		case 0xb0:
			return new String[]{"A"};
		case 0xb1:
			return new String[]{"A"};
		case 0xb2:
			return new String[]{"A"};
		case 0xb5:
			return new String[]{"A"};
		case 0xd0:
			return new String[]{"A"};
		case 0xd1:
			return new String[]{"A"};
		case 0xd2:
			return new String[]{"A"};
		case 0xd5:
			return new String[]{"A"};
		case 0xd8:
			return new String[]{"AA"};
		case 0xd9:
			return new String[]{"AA"};
		case 0xda:
			return new String[]{"AA"};
		case 0xdd:
			return new String[]{"AA"};
		
		case 0xff:
			return new String[]{};
			
		default:
		case 0x00: 
			return new String[]{};
		}
	}
	/**
	 * get Assembly Code of 'code' in the context of 'argu'<br />
	 * @param code
	 * @param argu
	 * @return String for Assembly Code
	 */
	public static String getAsmCode(Code code, SymbolTable argu) {
		
		String codeStr = TypeChange.IntToString(code.getLineNumber(), 4)+": ";
		
		String A, AA, AAAA, AAAAAAAA, B, BB, BBBB, BBBBBBBB, CC, CCCC, D, E, F, G, NNNN;
		A = code.getParamValue("A");
		if (A != null && TypeChange.isInt(A)) A = TypeChange.IntToString(TypeChange.parseInt(A), 1);
		AA = code.getParamValue("AA");
		if (AA != null && TypeChange.isInt(AA)) AA = TypeChange.IntToString(TypeChange.parseInt(AA), 2);
		AAAA = code.getParamValue("AAAA");
		if (AAAA != null && TypeChange.isInt(AAAA)) AAAA = TypeChange.IntToString(TypeChange.parseInt(AAAA), 4);
		AAAAAAAA = code.getParamValue("AAAAAAAA");
		if (AAAAAAAA != null && TypeChange.isInt(AAAAAAAA)) AAAAAAAA = TypeChange.IntToString(TypeChange.parseInt(AAAAAAAA), 8);
		B = code.getParamValue("B");
		if (B != null && TypeChange.isInt(B)) B = TypeChange.IntToString(TypeChange.parseInt(B), 1);
		BB = code.getParamValue("BB");
		if (BB != null && TypeChange.isInt(BB)) BB = TypeChange.IntToString(TypeChange.parseInt(BB), 2);
		BBBB = code.getParamValue("BBBB");
		if (BBBB != null && TypeChange.isInt(BBBB)) BBBB = TypeChange.IntToString(TypeChange.parseInt(BBBB), 4);
		BBBBBBBB = code.getParamValue("BBBBBBBB");
		if (BBBBBBBB != null && TypeChange.isInt(BBBBBBBB)) BBBBBBBB = TypeChange.IntToString(TypeChange.parseInt(BBBBBBBB), 8);
		CC = code.getParamValue("CC");
		if (CC != null && TypeChange.isInt(CC)) CC = TypeChange.IntToString(TypeChange.parseInt(CC), 2);
		CCCC = code.getParamValue("CCCC");
		if (CCCC != null && TypeChange.isInt(CCCC)) CCCC = TypeChange.IntToString(TypeChange.parseInt(CCCC), 4);
		D = code.getParamValue("D");
		if (D != null && TypeChange.isInt(D)) D = TypeChange.IntToString(TypeChange.parseInt(D), 1);
		E = code.getParamValue("E");
		if (E != null && TypeChange.isInt(E)) E = TypeChange.IntToString(TypeChange.parseInt(E), 1);
		F = code.getParamValue("F");
		if (F != null && TypeChange.isInt(F)) F = TypeChange.IntToString(TypeChange.parseInt(F), 1);
		G = code.getParamValue("G");
		if (G != null && TypeChange.isInt(G)) G = TypeChange.IntToString(TypeChange.parseInt(G), 1);
		
		switch (code.getCodeOp()) {
			case 0x01:
				codeStr += "move v"+A+", v"+B;
				break;
			case 0x02:
				codeStr += "move/from16 v"+AA+", v"+BBBB;
				break;
			case 0x03:
				codeStr += "move/16 v"+AAAA+", v"+BBBB;
				break;
			case 0x07:
				codeStr += "move-object v"+A+", v"+B;
				break;
			case 0x08:
				codeStr += "move-object/from16 v"+AA+", v"+BBBB;
				break;
			case 0x09:
				codeStr += "move-object/16 v"+AAAA+", v"+BBBB;
				break;
			case 0x0a:
				codeStr += "move-result v"+AA;
				break;
			case 0x0c:
				codeStr += "move-result-object v"+AA;
				break;
			case 0x0e:
				codeStr += "return-void";
				break;
			case 0x0f:
				codeStr += "return v"+AA;
				break;
			case 0x11:
				codeStr += "return-object v"+AA;
				break;
			case 0x12:
				codeStr += "const/4 v"+A+", #+"+B;
				break;
			case 0x13:
				codeStr += "const/16 v"+AA+", #+"+BBBB;
				break;
			case 0x14:
				codeStr += "const v"+AA+", #+"+BBBBBBBB;
				break;
			case 0x21:
				codeStr += "array-length v"+A+", v"+B;
				break;
			case 0x22:
				codeStr += "new-instance v"+AA+", "+argu.globalTable.dexPrinter.GetTypeNameByIdx(TypeChange.parseInt(code.getParamValue("BBBB")))+" //type@"+BBBB;
				break;
			case 0x23:
				codeStr += "new-array v"+A+", v"+B+", "+argu.globalTable.dexPrinter.GetTypeNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //type@"+CCCC;
				break;
			case 0x28:
				codeStr += "goto +"+TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("AA"))*2, 2);
				break;
			case 0x29:
				codeStr += "goto/16 +"+TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("AAAA"))*2, 4);
				break;
			case 0x2a:
				codeStr += "goto/32 +"+TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("AAAAAAAA"))*2, 8);
				break;
			case 0x34:
				codeStr += "if-lt v"+A+", v"+B+", +"+TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("CCCC"))*2, 4);
				break;
			case 0x35:
				codeStr += "if-ge v"+A+", v"+B+", +"+TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("CCCC"))*2, 4);
				break;
			case 0x38:
				codeStr += "if-eqz v"+AA+", +"+TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("BBBB"))*2, 4);
				break;
			case 0x39:
				codeStr += "if-nez v"+AA+", +"+TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("BBBB"))*2, 4);
				break;
			case 0x44:
				codeStr += "aget v"+AA+", v"+BB+", v"+CC;
				break;
			case 0x4b:
				codeStr += "aput v"+AA+", v"+BB+", v"+CC;
				break;
			case 0x52:
				codeStr += "iget v"+A+", v"+B+", "+argu.globalTable.dexPrinter.GetFieldNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //field@"+CCCC;
				break;
			case 0x54:
				codeStr += "iget-object v"+A+", v"+B+", "+argu.globalTable.dexPrinter.GetFieldNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //field@"+CCCC;
				break;
			case 0x55:
				codeStr += "iget-boolean v"+A+", v"+B+", "+argu.globalTable.dexPrinter.GetFieldNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //field@"+CCCC;
				break;
			case 0x59:
				codeStr += "iput v"+A+", v"+B+", "+argu.globalTable.dexPrinter.GetFieldNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //field@"+CCCC;
				break;
			case 0x5b:
				codeStr += "iput-object v"+A+", v"+B+", "+argu.globalTable.dexPrinter.GetFieldNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //field@"+CCCC;
				break;
			case 0x5c:
				codeStr += "iput-boolean v"+A+", v"+B+", "+argu.globalTable.dexPrinter.GetFieldNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //field@"+CCCC;
				break;
			case 0x62:
				codeStr += "sget-object v"+AA+", "+argu.globalTable.dexPrinter.GetFieldNameByIdx(TypeChange.parseInt(code.getParamValue("BBBB")))+" //field@"+BBBB;
				break;
			case 0x6e:
				int tmp6e = TypeChange.parseInt(B)-1;
				codeStr += "invoke-virtual {v"+D;
				if (tmp6e > 0) codeStr += ", v"+E;
				if (tmp6e > 1) codeStr += ", v"+F;
				if (tmp6e > 2) codeStr += ", v"+G;
				if (tmp6e > 3) codeStr += ", v"+A;
				codeStr += "}, "+argu.globalTable.dexPrinter.GetMethodNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //meth@"+CCCC;
				break;
			case 0x70:
				int tmp70 = TypeChange.parseInt(B)-1;
				codeStr += "invoke-direct {v"+D;
				if (tmp70 > 0) codeStr += ", v"+E;
				if (tmp70 > 1) codeStr += ", v"+F;
				if (tmp70 > 2) codeStr += ", v"+G;
				if (tmp70 > 3) codeStr += ", v"+A;
				codeStr += "}, "+argu.globalTable.dexPrinter.GetMethodNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //meth@"+CCCC;
				break;
			case 0x71:
				int tmp71 = TypeChange.parseInt(B)-1;
				codeStr += "invoke-direct {v"+D;
				if (tmp71 > 0) codeStr += ", v"+E;
				if (tmp71 > 1) codeStr += ", v"+F;
				if (tmp71 > 2) codeStr += ", v"+G;
				if (tmp71 > 3) codeStr += ", v"+A;
				codeStr += "}, "+argu.globalTable.dexPrinter.GetMethodNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //meth@"+CCCC;
				break;
			case 0x74:
				NNNN = TypeChange.IntToString(TypeChange.parseInt(code.getParamValue("CCCC"))
					+ TypeChange.parseInt(code.getParamValue("AA"))-1, 4);
				codeStr += "invoke-virtual/range {v"+CCCC+" .. v"+NNNN+"}, "+argu.globalTable.dexPrinter.GetMethodNameByIdx(TypeChange.parseInt(code.getParamValue("BBBB")))+" //meth@"+BBBB;
				break;

			case 0x90:
				codeStr += "add-int v"+AA+", v"+BB+", v"+CC;
				break;
			case 0x91:
				codeStr += "sub-int v"+AA+", v"+BB+", v"+CC;
				break;
			case 0x92:
				codeStr += "mul-int v"+AA+", v"+BB+", v"+CC;
				break;
			case 0x95:
				codeStr += "and-int v"+AA+", v"+BB+", v"+CC;
				break;
			case 0xb0:
				codeStr += "add-int/2addr v"+A+", v"+B;
				break;
			case 0xb1:
				codeStr += "sub-int/2addr v"+A+", v"+B;
				break;
			case 0xb2:
				codeStr += "mul-int/2addr v"+A+", v"+B;
				break;
			case 0xb5:
				codeStr += "and-int/2addr v"+A+", v"+B;
				break;
			case 0xd0:
				codeStr += "add-int/lit16 v"+A+", v"+B+", #+"+CCCC;
				break;
			case 0xd1:
				codeStr += "rsub-int/lit16 v"+A+", v"+B+", #+"+CCCC;
				break;
			case 0xd2:
				codeStr += "mul-int/lit16 v"+A+", v"+B+", #+"+CCCC;
				break;
			case 0xd5:
				codeStr += "and-int/lit16 v"+A+", v"+B+", #+"+CCCC;
				break;
			case 0xd8:
				codeStr += "add-int/lit8 v"+AA+", v"+BB+", #+"+CC;
				break;
			case 0xd9:
				codeStr += "rsub-int/lit8 v"+AA+", v"+BB+", #+"+CC;
				break;
			case 0xda:
				codeStr += "mul-int/lit8 v"+AA+", v"+BB+", #+"+CC;
				break;
			case 0xdd:
				codeStr += "and-int/lit8 v"+AA+", v"+BB+", #+"+CC;
				break;
			
			case 0xff:
				int tmpff = TypeChange.parseInt(B)-1;
				codeStr += "invoke-virtual {v"+D;
				if (tmpff > 0) codeStr += ", v"+E;
				if (tmpff > 1) codeStr += ", v"+F;
				if (tmpff > 2) codeStr += ", v"+G;
				if (tmpff > 3) codeStr += ", v"+A;
				codeStr += "}, "+argu.globalTable.dexPrinter.GetMethodNameByIdx(TypeChange.parseInt(code.getParamValue("CCCC")))+" //meth@"+CCCC;
				break;
				
			default:
			case 0x00: codeStr += "nop";
		}
		return codeStr;
	}
	/**
	 * get Binary Code of 'code' <br />
	 * @param code
	 * @return String for Binary Code
	 */
	public static String getBinCode(Code code) {
		String A, AA, AAAA, AAAAAAAA, B, BB, BBBB, BBBBBBBB, CC, CCCC, D, E, F, G;
		A = code.getParamValue("A");
		if (A != null) A = TypeChange.IntToString(TypeChange.parseInt(A), 1);
		AA = code.getParamValue("AA");
		if (AA != null) AA = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(AA), 2);
		AAAA = code.getParamValue("AAAA");
		if (AAAA != null) AAAA = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(AAAA), 4);
		AAAAAAAA = code.getParamValue("AAAAAAAA");
		if (AAAAAAAA != null) AAAAAAAA = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(AAAAAAAA), 8);
		B = code.getParamValue("B");
		if (B != null) B = TypeChange.IntToString(TypeChange.parseInt(B), 1);
		BB = code.getParamValue("BB");
		if (BB != null) BB = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(BB), 2);
		BBBB = code.getParamValue("BBBB");
		if (BBBB != null) BBBB = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(BBBB), 4);
		BBBBBBBB = code.getParamValue("BBBBBBBB");
		if (BBBBBBBB != null) BBBBBBBB = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(BBBBBBBB), 8);
		CC = code.getParamValue("CC");
		if (CC != null) CC = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(CC), 2);
		CCCC = code.getParamValue("CCCC");
		if (CCCC != null) CCCC = TypeChange.IntToStringLittleEndian(TypeChange.parseInt(CCCC), 4);
		D = code.getParamValue("D");
		if (D != null) D = TypeChange.IntToString(TypeChange.parseInt(D), 1);
		E = code.getParamValue("E");
		if (E != null) E = TypeChange.IntToString(TypeChange.parseInt(E), 1);
		F = code.getParamValue("F");
		if (F != null) F = TypeChange.IntToString(TypeChange.parseInt(F), 1);
		G = code.getParamValue("G");
		if (G != null) G = TypeChange.IntToString(TypeChange.parseInt(G), 1);
		
		String codeStr;
		int op = code.getCodeOp();
		if (op == 0xff) op = 0x6e;
		String opStr = TypeChange.IntToString(op, 2);
		
		switch (op) {
			case 0x01:
				codeStr = opStr+B+A;
				break;
			case 0x02:
				codeStr = opStr+AA+BBBB;
				break;
			case 0x03:
				codeStr = opStr+"00"+AAAA+BBBB;
				break;
			case 0x07:
				codeStr = opStr+B+A;
				break;
			case 0x08:
				codeStr = opStr+AA+BBBB;
				break;
			case 0x09:
				codeStr = opStr+"00"+AAAA+BBBB;
				break;
			case 0x0a:
				codeStr = opStr+AA;
				break;
			case 0x0c:
				codeStr = opStr+AA;
				break;
			case 0x0e:
				codeStr = opStr+"00";
				break;
			case 0x0f:
				codeStr = opStr+AA;
				break;
			case 0x11:
				codeStr = opStr+AA;
				break;
			case 0x12:
				codeStr = opStr+B+A;
				break;
			case 0x13:
				codeStr = opStr+AA+BBBB;
				break;
			case 0x14:
				codeStr = opStr+AA+BBBBBBBB;
				break;
			case 0x21:
				codeStr = opStr+B+A;
				break;
			case 0x22:
				codeStr = opStr+AA+BBBB;
				break;
			case 0x23:
				codeStr = opStr+B+A+CCCC;
				break;
			case 0x28:
				codeStr = opStr+AA;
				break;
			case 0x29:
				codeStr = opStr+"00"+AAAA;
				break;
			case 0x2a:
				codeStr = opStr+"00"+AAAAAAAA;
				break;
			case 0x34:
				codeStr = opStr+B+A+CCCC;
			case 0x35:
				codeStr = opStr+B+A+CCCC;
				break;
			case 0x38:
				codeStr = opStr+AA+BBBB;
				break;
			case 0x39:
				codeStr = opStr+AA+BBBB;
				break;
			case 0x44:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0x4b:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0x52:
			case 0x54:
			case 0x55:
			case 0x59:
			case 0x5b:
			case 0x5c:
				codeStr = opStr+B+A+CCCC;
				break;
			case 0x62:
				codeStr = opStr+AA+BBBB;
				break;
			case 0x6e:
				codeStr = opStr+B+A+CCCC+E+D+G+F;
				break;
			case 0x70:
				codeStr = opStr+B+A+CCCC+E+D+G+F;
				break;
			case 0x71:
				codeStr = opStr+B+A+CCCC+E+D+G+F;
				break;
			case 0x74:
				codeStr = opStr+AA+BBBB+CCCC;
				break;
	
			case 0x90:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0x91:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0x92:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0x95:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0xb0:
				codeStr = opStr+B+A;
				break;
			case 0xb1:
				codeStr = opStr+B+A;
				break;
			case 0xb2:
				codeStr = opStr+B+A;
				break;
			case 0xb5:
				codeStr = opStr+B+A;
				break;
			case 0xd0:
				codeStr = opStr+B+A+CCCC;
				break;
			case 0xd1:
				codeStr = opStr+B+A+CCCC;
				break;
			case 0xd2:
				codeStr = opStr+B+A+CCCC;
				break;
			case 0xd5:
				codeStr = opStr+B+A+CCCC;
				break;
			case 0xd8:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0xd9:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0xda:
				codeStr = opStr+AA+BB+CC;
				break;
			case 0xdd:
				codeStr = opStr+AA+BB+CC;
				break;
			
			case 0xff:
				codeStr = "print v"+AA;
				break;
				
			default:
			case 0x00: codeStr = opStr+"00";
		}
		return codeStr;
	}
}