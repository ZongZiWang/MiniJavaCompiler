package minijava.binary;

import util.Mode;
import minijava.intermediate.TargetCode;
import minijava.typecheck.symboltable.GlobalTable;

/**
 * Generate Binary Code
 * @author ZongZiWang
 *
 */
public class BinaryCode {
	/**
	 * show Binary Code of source file 'filename' and insert Binary Code into Dex file
	 * @param fileName
	 * @param GT
	 * @return
	 */
	public static String AsmToBin(String fileName, GlobalTable GT) {
		
		if (Mode.isDebugMode()) System.out.println("---Creating Binary Code---");
		
		for (int i = 0; i < TargetCode.methods.size(); i++) {
			TargetCode.methods.get(i).showBinaryCode();
			TargetCode.methods.get(i).insertBinaryCodeToDex();
		}
		
		return null;
	}
}
