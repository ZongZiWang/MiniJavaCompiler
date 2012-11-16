package minijava.intermediate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import syntaxtree.*;
import util.Mode;
import main.*;
import minijava.typecheck.symboltable.GlobalTable;

/**
 * Generate Intermediate Code
 * @author ZongZiWang
 *
 */
public class IntermediateCode {
	/**
	 * MiniJava File 'filename' to Intermediate Code in the context 'GT'
	 * @param fileName
	 * @param GT
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public static String JavaToAsm(String fileName, GlobalTable GT) throws FileNotFoundException, ParseException{
		InputStream SourceCode = new FileInputStream(fileName);
	
		if (Mode.isDebugMode()) System.out.println("---Creating Intermediate Code---");
		
		/**
		 * Parse again
		 */
		MiniJavaParser.ReInit(SourceCode);
		Node root = MiniJavaParser.Goal();
		
		IntermediateCodeVisitor icv = new IntermediateCodeVisitor();
		root.accept(icv, GT);
		
		RegLabelLock.RegUnlockAll();
		TargetCode.showAllCode();
		
		return null;
	}
}
