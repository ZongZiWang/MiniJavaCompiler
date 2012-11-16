package minijava.informations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import syntaxtree.Node;
import util.Mode;
import main.MiniJavaParser;
import main.ParseException;
import minijava.typecheck.symboltable.GlobalTable;

/**
 * Build information for dex file from source file
 * @author ZongZiWang
 *
 */
public class BuildInformation {
	/**
	 * Build information for dex file from source file 'filename' in the context 'GT'
	 * @param fileName
	 * @param GT
	 * @throws ParseException
	 * @throws FileNotFoundException
	 */
	public static void Build(String fileName, GlobalTable GT) throws ParseException, FileNotFoundException {
		InputStream SourceCode = new FileInputStream(fileName);
		
		if (Mode.isDebugMode()) System.out.println("---Building Information for Dex File---");
		
		//Parse again
		MiniJavaParser.ReInit(SourceCode);
		Node root = MiniJavaParser.Goal();
	
		BuildInformationVisitor biv = new BuildInformationVisitor();
		root.accept(biv, GT);
	}

}
