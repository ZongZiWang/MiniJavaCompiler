package minijava.typecheck;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import main.MiniJavaParser;
import main.ParseException;
import minijava.typecheck.symboltable.*;
import minijava.typecheck.visitor.*;
import syntaxtree.Node;
import util.Mode;

/**
 * Type check
 * @author ZongZiWang
 *
 */
public class TypeCheck {
	/**
	 * set no error
	 */
	public static void init() {
		error = false;
	}
	static Boolean first = true;
	static Boolean error = false;
	/**
	 * set error
	 */
	public static void setError() {
		error = true;
	}
	/**
	 * type check the source file 'fileName'
	 * @param fileName
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static GlobalTable typeCheck(String fileName) throws ParseException, IOException{
		InputStream SourceCode = new FileInputStream(fileName);
	
		if (Mode.isDebugMode()) System.out.println("---Creating Symbol Table---");
		
		if (first) {
			new MiniJavaParser(SourceCode);
			first = false;
		} else MiniJavaParser.ReInit(SourceCode);
		Node root = MiniJavaParser.Goal();
	
		GlobalTable globalTable = null;
		
		BuildSymbolTableVisitor buildSymbolTable = new BuildSymbolTableVisitor();
		globalTable = (GlobalTable)root.accept(buildSymbolTable, null);
		
		if (Mode.isDebugMode()) System.out.println("---Creation Succeeded!---");
		
		if (Mode.isDebugMode()) System.out.println("---First Check Start---");
	
		if ((new FirstCheck()).firstCheckError(globalTable)) return null;
		
		if (Mode.isDebugMode()) System.out.println("---First Check End---");
		
		if (Mode.isDebugMode()) System.out.println("---Type Check Start---");
		
		TypeCheckVisitor typeCheck = new TypeCheckVisitor(true, true);
		root.accept(typeCheck, globalTable);
		
		if (Mode.isDebugMode()) System.out.println("---Type Check End---");

		if (Mode.isDebugMode()) System.out.println("---Unused Check Start---");
		
		(new UnusedCheck()).unusedCheck(globalTable);

		if (Mode.isDebugMode()) System.out.println("---Unused Check End---");

		SourceCode.close();
		
		if (error) return null;
		
		return globalTable;
	}
}
