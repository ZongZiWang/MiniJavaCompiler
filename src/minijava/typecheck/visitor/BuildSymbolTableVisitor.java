package minijava.typecheck.visitor;

import minijava.typecheck.symboltable.*;
import syntaxtree.*;
import visitor.*;

/**
 * Build Symbol Table
 * @author ZongZiWang
 *
 */
public class BuildSymbolTableVisitor extends GJDepthFirst<SymbolTable, SymbolTable>{
	@Override
	/**
	 * identifier get type
	 */
	public SymbolTable visit(Identifier n, SymbolTable argu) {
		SymbolTable _ret = null;
		
		if (argu instanceof TypeTable && ((TypeTable)argu).isClassType()) argu.setTypeName(n.f0.tokenImage);
		
		n.f0.accept(this, argu);
		return _ret;
	}
	@Override
	/**
	 * create new GlobalTable
	 */
	public SymbolTable visit(Goal n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		GlobalTable globalTable = new GlobalTable(n.f0.f1.f0.tokenImage);
		
		n.f0.accept(this, globalTable);
		n.f1.accept(this, globalTable);
		n.f2.accept(this, globalTable);
		
		_ret = globalTable;
		return _ret;
	}
	@Override
	/**
	 * create new ClassTable and new MethodTable
	 */
	public SymbolTable visit(MainClass n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		ClassTable classTable = new ClassTable(n.f1.f0.tokenImage, "java.lang.Object", n.f1.f0.beginLine, argu.globalTable);
		MethodTable methodTable = new MethodTable("main", n.f1.f0.tokenImage, new TypeTable(n.f1.f0.tokenImage, "void", "main", n.f1.f0.tokenImage, n.f6.beginLine, argu.globalTable), n.f6.beginLine, argu.globalTable);
		TypeTable type = new TypeTable(n.f11.f0.tokenImage, "String[]", "main", n.f1.f0.tokenImage, n.f11.f0.beginLine, argu.globalTable);
		methodTable.insertParam(n.f11.f0.tokenImage, type, n.f11.f0.beginLine);
		classTable.insertMethod("main", methodTable);
		argu.globalTable.insertClass(n.f1.f0.tokenImage, classTable);
		argu.globalTable.setMainClassName(n.f1.f0.tokenImage);
		
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, methodTable);
		n.f4.accept(this, methodTable);
		n.f5.accept(this, methodTable);
		n.f6.accept(this, methodTable);
		n.f7.accept(this, methodTable);
		n.f8.accept(this, methodTable);
		n.f9.accept(this, methodTable);
		n.f10.accept(this, methodTable);
		n.f11.accept(this, methodTable);
		n.f12.accept(this, methodTable);
		n.f13.accept(this, methodTable);
		n.f14.accept(this, methodTable);
		n.f15.accept(this, methodTable);
		n.f16.accept(this, classTable);
		
		_ret = classTable;
		return _ret;
	}
	@Override
	/**
	 * create new ClassTable
	 */
	public SymbolTable visit(ClassDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		ClassTable classTable = new ClassTable(n.f1.f0.tokenImage, "java.lang.Object", n.f1.f0.beginLine, argu.globalTable);
		
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, classTable);
		n.f4.accept(this, classTable);
		n.f5.accept(this, classTable);

		argu.globalTable.insertClass(n.f1.f0.tokenImage, classTable);

		_ret = classTable;
		return _ret;
	}
	@Override
	/**
	 * create new ClassTable
	 */
	public SymbolTable visit(ClassExtendsDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		ClassTable classTable = new ClassTable(n.f1.f0.tokenImage, n.f3.f0.tokenImage, n.f1.f0.beginLine, argu.globalTable);
		
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, classTable);
		n.f4.accept(this, classTable);
		n.f5.accept(this, classTable);
		n.f6.accept(this, classTable);
		n.f7.accept(this, classTable);
		
		argu.globalTable.insertClass(n.f1.f0.tokenImage, classTable);

		_ret = classTable;
		return _ret;
	}
	@Override
	/**
	 * create new field or local variable TypeTable
	 */
	public SymbolTable visit(VarDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		TypeTable type;
		type = new TypeTable(n.f1.f0.tokenImage, n.f0.f0.which, argu.getMethodName(), argu.getClassName(), n.f1.f0.beginLine, argu.globalTable);
		
		n.f0.accept(this, type);
		
		argu.insertVariable(n.f1.f0.tokenImage, type, n.f1.f0.beginLine);
		if (argu instanceof ClassTable) type.setKind(TypeTable.FIELD);
		if (argu instanceof MethodTable) type.setKind(TypeTable.LOCAL);
		
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);

		_ret = type;
		return _ret;
	}
	@Override
	/**
	 * create new MethodTable
	 */
	public SymbolTable visit(MethodDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;

		TypeTable type;
		type = new TypeTable(n.f2.f0.tokenImage, n.f1.f0.which, n.f2.f0.tokenImage, argu.getClassName(), n.f2.f0.beginLine, argu.globalTable);
		
		n.f0.accept(this, argu);
		n.f1.accept(this, type);
		
		MethodTable methodTable = new MethodTable(n.f2.f0.tokenImage, argu.getClassName(), type, n.f2.f0.beginLine, argu.globalTable);
			
		n.f2.accept(this, methodTable);
		n.f3.accept(this, methodTable);
		n.f4.accept(this, methodTable);
		n.f5.accept(this, methodTable);
		n.f6.accept(this, methodTable);
		n.f7.accept(this, methodTable);
		n.f8.accept(this, methodTable);
		n.f9.accept(this, methodTable);
		n.f10.accept(this, methodTable);
		n.f11.accept(this, methodTable);
		n.f12.accept(this, methodTable);
		
		((ClassTable)argu).insertMethod(n.f2.f0.tokenImage, methodTable);

		_ret = methodTable;
		return _ret;
	}
	@Override
	/**
	 * create new parameter TypeTable
	 */
	public SymbolTable visit(FormalParameter n, SymbolTable argu) {
		SymbolTable _ret=null;

		TypeTable type;
		type = new TypeTable(n.f1.f0.tokenImage, n.f0.f0.which, argu.getMethodName(), argu.getClassName(), n.f1.f0.beginLine, argu.globalTable);
		
		n.f0.accept(this, type);
		
		((MethodTable)argu).insertParam(n.f1.f0.tokenImage, type, n.f1.f0.beginLine);
		type.setKind(TypeTable.PARAM);
		
		n.f1.accept(this, argu);
		
		_ret = type;
		return _ret;
	}
}
