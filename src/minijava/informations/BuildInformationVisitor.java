package minijava.informations;

import minijava.typecheck.symboltable.*;
import syntaxtree.*;
import util.AccessFlag;
import util.Mode;
import visitor.GJDepthFirst;
/**
 * Build information for dex file
 * @author ZongZiWang
 *
 */
public class BuildInformationVisitor extends GJDepthFirst<SymbolTable, SymbolTable>{
	/**
	 * Visit all the nodes and serialize the information
	 */
	@Override
	public SymbolTable visit(Goal n, SymbolTable argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		
		argu.globalTable.dexPrinter.SerilizeAll();
		
		return null;
	}
	/**
	 * Build the information of main class
	 */
	@Override
	public SymbolTable visit(MainClass n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		classTable.globalTable.dexPrinter.AddMainClassName(classTable.getClassName());
		classTable.globalTable.dexPrinter.AddClass(classTable.getClassName(), classTable.getParentClassName());
		MethodTable methodTable = classTable.getMethodTable("main");
		methodTable.globalTable.dexPrinter.AddDirectMethodToClass(methodTable.getClassName()
				, methodTable.getMethodName()
				, methodTable.getReturnType().getTypeName()
				, AccessFlag.PUBLIC | AccessFlag.STATIC
				, methodTable.getParamLength()
				, methodTable.getParamTypeArray());
		methodTable.updateInRegNum(methodTable.getParamLength());
		
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
	/**
	 * Build the information of class
	 */
	@Override
	public SymbolTable visit(ClassDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		classTable.globalTable.dexPrinter.AddClass(classTable.getClassName(), classTable.getParentClassName());
		
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, classTable);
		n.f4.accept(this, classTable);
		n.f5.accept(this, classTable);

		_ret = classTable;
		return _ret;
	}
	/**
	 * Build the information of class
	 */
	@Override
	public SymbolTable visit(ClassExtendsDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		classTable.globalTable.dexPrinter.AddClass(classTable.getClassName(), classTable.getParentClassName());
		
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, classTable);
		n.f4.accept(this, classTable);
		n.f5.accept(this, classTable);
		n.f6.accept(this, classTable);
		n.f7.accept(this, classTable);

		_ret = classTable;
		return _ret;
	}
	/**
	 * Build the information of type
	 */
	@Override
	public SymbolTable visit(VarDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		TypeTable type;
		type = argu.getVariableType(n.f1.f0.tokenImage);
		if (argu instanceof ClassTable) {
			argu.globalTable.dexPrinter.AddInstanceFieldToClass(argu.getClassName(), type.getTypeName(), n.f1.f0.tokenImage);
		} else if (argu instanceof MethodTable) {
			argu.globalTable.dexPrinter.AddLocalType(type.getTypeName());
		}
		
		n.f0.accept(this, type);
		n.f1.accept(this, type);
		n.f2.accept(this, type);

		_ret = type;
		return _ret;
	}

	/**
	 * Build the information of method
	 */
	@Override
	public SymbolTable visit(MethodDeclaration n, SymbolTable argu) {
		SymbolTable _ret=null;

		MethodTable methodTable = argu.globalTable.getClassTable(argu.getClassName()).getMethodTable(n.f2.f0.tokenImage);
		methodTable.globalTable.dexPrinter.AddVirtualMethodToClass(methodTable.getClassName()
				, methodTable.getMethodName()
				, methodTable.getReturnType().getTypeName()
				, AccessFlag.PUBLIC
				, methodTable.getParamLength()
				, methodTable.getParamTypeArray());
		methodTable.updateInRegNum(1/*this*/+methodTable.getParamLength());
		
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
		
		_ret = methodTable;
		return _ret;
	}
	/**
	 * Build the information of method caller
	 */
	@Override
	public SymbolTable visit(MessageSend n, SymbolTable argu) {
		SymbolTable _ret=null;
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		
		String typeName = null;
		if (n.f0.f0.choice instanceof Identifier) {
			ClassTable ct = argu.globalTable.getClassTable(argu.getClassName());
			MethodTable mt = ct.getMethodTable(argu.getMethodName());
			TypeTable tt = mt.getVariableType(((Identifier)n.f0.f0.choice).f0.tokenImage);
			if (tt == null) tt = ct.getVariableType(((Identifier)n.f0.f0.choice).f0.tokenImage);
			typeName = tt.getTypeName();
		} else if (n.f0.f0.choice instanceof AllocationExpression) {
			typeName = ((AllocationExpression)n.f0.f0.choice).f1.f0.tokenImage;
		} else {
			typeName = argu.getClassName();
		}
		ClassTable methct = argu.globalTable.getClassTable(typeName);
		MethodTable methmt = methct.getMethodTable(n.f2.f0.tokenImage);
		((MethodTable)argu).updateOutRegNum(1+methmt.getParamLength());
		((MethodTable)argu).updateInvokeVirtualRegNum(1+methmt.getParamLength());

		return _ret;
	}
	/**
	 * Build the information of method caller
	 */
	@Override
	public SymbolTable visit(PrintStatement n, SymbolTable argu) {

		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		
		((MethodTable)argu).updateOutRegNum(1+1);
		if (Mode.IsOutputApk()) ((MethodTable)argu).updateInvokeVirtualRegNum(1);
		else ((MethodTable)argu).updateInvokeVirtualRegNum(1+1);
			
		return null;
	}
}
