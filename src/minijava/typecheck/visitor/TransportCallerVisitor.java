package minijava.typecheck.visitor;

import minijava.typecheck.symboltable.*;
import syntaxtree.*;
import visitor.GJDepthFirst;

/**
 * Only transport caller context to the callee
 * @author ZongZiWang
 *
 * @param <R>
 */
public class TransportCallerVisitor<R> extends GJDepthFirst<R, SymbolTable> {
	@Override
	public R visit(MainClass n, SymbolTable argu) {
		R _ret=null;
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		MethodTable methodTable = classTable.getMethodTable("main");
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
		return _ret;
	}
	@Override
	public R visit(ClassDeclaration n, SymbolTable argu) {
		R _ret=null;
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, classTable);
		n.f4.accept(this, classTable);
		n.f5.accept(this, classTable);
		return _ret;
	}
	@Override
	public R visit(ClassExtendsDeclaration n, SymbolTable argu) {
		R _ret=null;
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, classTable);
		n.f4.accept(this, classTable);
		n.f5.accept(this, classTable);
		n.f6.accept(this, classTable);
		n.f7.accept(this, classTable);
		return _ret;
	}
	@Override
	public R visit(MethodDeclaration n, SymbolTable argu) {
		R _ret=null;
		MethodTable methodTable = ((ClassTable)argu).getMethodTable(n.f2.f0.tokenImage);
		n.f0.accept(this, methodTable);
		n.f1.accept(this, methodTable);
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
		return _ret;
	}
}
