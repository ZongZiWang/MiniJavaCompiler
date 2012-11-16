package minijava.typecheck.visitor;

import minijava.compiler.ErrorInfo;
import minijava.compiler.WarningInfo;
import minijava.typecheck.TypeCheck;
import minijava.typecheck.symboltable.*;
import syntaxtree.*;

/**
 * Check type error, no declaration error, uninitialized warning
 * @author ZongZiWang
 *
 */
public class TypeCheckVisitor extends TransportCallerVisitor<String> {
	protected boolean CloseUnknown = false;
	protected boolean EnableWarning = false;
	/**
	 * Unknown Type Switch and Warning Switch
	 * @param enableUnknown
	 * @param enableWarning
	 */
	public TypeCheckVisitor(boolean enableUnknown, boolean enableWarning) {
		CloseUnknown = enableUnknown;
		EnableWarning = enableWarning;
	}
	@Override
	public String visit(MethodDeclaration n, SymbolTable argu) {
		String _ret=null;
		
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
		
		TypeTable type = new TypeTable("UnknowExp", "UnknownType", n.f2.f0.tokenImage, argu.getClassName(), n.f9.beginLine, argu.globalTable);
		n.f10.accept(this, type);
		
		/**
		 * return type error
		 */
		if (!CloseUnknown || !type.getTypeName().equals("UnknownType"))
		if (!argu.globalTable.isTypeMatch(methodTable.getReturnType(), type)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+n.f2.f0.tokenImage+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"return (type: "+type.getTypeName()+")\" at line "+n.f9.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"return (type: "+methodTable.getReturnType().getTypeName()+")\"");
			TypeCheck.setError();
		}
		
		n.f11.accept(this, methodTable);
		n.f12.accept(this, methodTable);
		
		return _ret;
	}
	@Override
	public String visit(AssignmentStatement n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable idType = argu.getVariableType(n.f0.f0.tokenImage);
		
		/**
		 * identifier no declaration error
		 */
		boolean inited = false;
		if (idType == null) idType = argu.globalTable.getClassTable(argu.getClassName()).getVariableType(n.f0.f0.tokenImage);
		if (idType == null) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.NoDeclarationException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered no declaration of id\""+n.f0.f0.tokenImage+"\" at line "+n.f0.f0.beginLine);
			TypeCheck.setError();
		} else {
			if (idType.getInited()) inited = true;
			else idType.setInited(TypeTable.INITED);
		}
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		
		if (idType != null && !inited) idType.setInited(TypeTable.INITING);
		
		TypeTable type = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		n.f2.accept(this, type);
		
		n.f3.accept(this, argu);
		
		/**
		 * identifier assign type error
		 */
		if (!CloseUnknown || !type.getTypeName().equals("UnknownType"))
		if (idType != null && !argu.globalTable.isTypeMatch(idType, type)) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \""+n.f0.f0.tokenImage+" = (type: "+type.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \""+n.f0.f0.tokenImage+" = (type: "+idType.getTypeName()+")\"");
			TypeCheck.setError();
		}
		
		if (idType != null) idType.setInited(TypeTable.INITED);
		
		return _ret;
	}
	@Override
	public String visit(ArrayAssignmentStatement n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable idType = argu.getVariableType(n.f0.f0.tokenImage);
		
		/**
		 * identifier no declaration error
		 */
		if (idType == null) idType = argu.globalTable.getClassTable(argu.getClassName()).getVariableType(n.f0.f0.tokenImage);
		if (idType == null) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.NoDeclarationException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered no declaration of array \""+n.f0.f0.tokenImage+"\" at line "+n.f0.f0.beginLine);
			TypeCheck.setError();
		}
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f0.f0.beginLine, argu.globalTable);
		n.f0.accept(this, type0);
		
		/**
		 * id[exp]'s id is not 'int[]'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (idType != null && !type0.getTypeName().equals("int[]")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+")[]\" at line "+n.f0.f0.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: int[])[]\"");
			TypeCheck.setError();
		}
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		/**
		 * id[exp]'s exp is not 'int'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (idType != null && type0.getTypeName().equals("int[]") && !type2.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"("+n.f0.f0.tokenImage+")[type: "+type2.getTypeName()+"]\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"("+n.f0.f0.tokenImage+")[type: int]\"");
			TypeCheck.setError();
		}
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		
		TypeTable type5 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f4.beginLine, argu.globalTable);
		n.f5.accept(this, type5);
		
		/**
		 * array element assign type error
		 */
		if (!CloseUnknown || !type5.getTypeName().equals("UnknownType"))
		if (idType != null && type0.getTypeName().equals("int[]") && type2.getTypeName().equals("int") && !type5.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"("+n.f0.f0.tokenImage+")["+str2+"] = (type: "+type5.getTypeName()+")\" at line "+n.f4.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"("+n.f0.f0.tokenImage+")["+str2+"] = (type: int)\"");
			TypeCheck.setError();
		}
		
		n.f6.accept(this, argu);
		
		return _ret;
	}
	@Override
	public String visit(IfStatement n, SymbolTable argu) {
		String _ret = null;
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		n.f2.accept(this, type2);
		
		/**
		 * if (exp)'s exp is not 'boolean'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("boolean")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"if (type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"if (type: boolean)\"");
			TypeCheck.setError();
		}
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		
		return _ret;
	}
	@Override
	public String visit(WhileStatement n, SymbolTable argu) {
		String _ret = null;
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		n.f2.accept(this, type2);
		
		/**
		 * while (exp)'s exp is not 'boolean'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("boolean")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"while (type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"while (type: boolean)\"");
			TypeCheck.setError();
		}
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		
		return _ret;
	}
	@Override
	public String visit(PrintStatement n, SymbolTable argu) {
		String _ret=null;
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		
		TypeTable type = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		n.f2.accept(this, type);
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);

		/**
		 * System.out.println(exp)'s exp is not 'int'
		 */
		if (!CloseUnknown || !type.getTypeName().equals("UnknownType"))
		if (!type.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"System.out.println(type: "+type.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"System.out.println(type: int)\"");
			TypeCheck.setError();
		}
		
		return _ret;
	}
	@Override
	public String visit(AndExpression n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		/**
		 * exp1 is not 'boolean'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!type0.getTypeName().equals("boolean")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+") && "+str2+"\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: boolean) && "+str2+"\"");
			TypeCheck.setError();
		}
		
		/**
		 * exp2 is not 'boolean'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("boolean")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \""+str0+" && (type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \""+str0+" && (type: boolean)\"");
			TypeCheck.setError();
		}
		
		if (type0.getTypeName().equals("boolean") && type2.getTypeName().equals("boolean")) argu.setTypeName("boolean");

		_ret = str0+"&&"+str2;
		return _ret;
	}
	@Override
	public String visit(CompareExpression n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		/**
		 * exp1 is not 'int'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!type0.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+") < "+str2+"\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: int) < "+str2+"\"");
			TypeCheck.setError();
		}
		
		/**
		 * exp2 is not 'int'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \""+str0+" < (type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \""+str0+" < (type: int)\"");
			TypeCheck.setError();
		}
		
		if (type0.getTypeName().equals("int") && type2.getTypeName().equals("int")) argu.setTypeName("boolean");
		
		_ret = str0+"<"+str2;
		return _ret;
	}
	@Override
	public String visit(PlusExpression n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		/**
		 * exp1 is not 'int'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!type0.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+") + "+str2+"\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: int) + "+str2+"\"");
			TypeCheck.setError();
		}
		
		/**
		 * exp2 is not 'int'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \""+str0+" + (type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \""+str0+" + (type: int)\"");
			TypeCheck.setError();
		}
		
		if (type0.getTypeName().equals("int") && type2.getTypeName().equals("int")) argu.setTypeName("int");
		
		_ret = str0+"+"+str2;
		return _ret;
	}
	@Override
	public String visit(MinusExpression n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		/**
		 * exp1 is not 'int'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!type0.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+") - "+str2+"\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: int) - "+str2+"\"");
			TypeCheck.setError();
		}
		
		/**
		 * exp2 is not 'int'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \""+str0+" - (type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \""+str0+" - (type: int)\"");
			TypeCheck.setError();
		}
		
		if (type0.getTypeName().equals("int") && type2.getTypeName().equals("int")) argu.setTypeName("int");
		
		_ret = str0+"-"+str2;
		return _ret;
	}
	@Override
	public String visit(TimesExpression n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		/**
		 * exp1 is not 'int'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!type0.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+") * "+str2+"\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: int) * "+str2+"\"");
			TypeCheck.setError();
		}
		
		/**
		 * exp2 is not 'int'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \""+str0+" * (type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \""+str0+" * (type: int)\"");
			TypeCheck.setError();
		}
		
		if (type0.getTypeName().equals("int") && type2.getTypeName().equals("int")) argu.setTypeName("int");
		
		_ret = str0+"*"+str2;
		return _ret;
	}
	@Override
	public String visit(ArrayLookup n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		n.f3.accept(this, argu);
		
		/**
		 * exp1 is not 'int'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!type0.getTypeName().equals("int[]")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+")["+str2+"]\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: int[])["+str2+"]\"");
			TypeCheck.setError();
		}
		
		/**
		 * exp2 is not 'int'
		 */
		if (!CloseUnknown || !type2.getTypeName().equals("UnknownType"))
		if (!type2.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \""+str0+"[type: "+type2.getTypeName()+")\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \""+str0+"[type: int]\"");
			TypeCheck.setError();
		}
		
		if (type0.getTypeName().equals("int[]") && type2.getTypeName().equals("int")) argu.setTypeName("int");
		
		_ret = str0+"["+str2+"]";
		return _ret;
	}
	@Override
	public String visit(ArrayLength n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		
		/**
		 * exp.length's exp is not 'int'
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!type0.getTypeName().equals("int[]")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+").length\" at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: int).length\"");
			TypeCheck.setError();
		}
		
		if (type0.getTypeName().equals("int[]")) argu.setTypeName("int");
		
		_ret = str0+".length";
		return _ret;
	}
	@Override
	public String visit(MessageSend n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.beginLine, argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		MethodTable methodTable = null;
		
		/**
		 * exp.method()'s exp is not class
		 */
		if (!CloseUnknown || !type0.getTypeName().equals("UnknownType"))
		if (!(type0.isClassType())) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"(type: "+type0.getTypeName()+")."+n.f2.f0.tokenImage+"()\" of at line "+n.f1.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"(type: class)."+n.f2.f0.tokenImage+"()\"");
			TypeCheck.setError();
		} else {
		
			ClassTable classTable = argu.globalTable.getClassTable(type0.getTypeName());
			
			/**
			 * exp.method()'s exp's type no declaration
			 */
			if (classTable == null) {
				ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.NoDeclarationException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
				ErrorInfo.addlnInfo("\tEncountered no declaration of class \""+str0+"\" of at line "+n.f2.f0.beginLine);
				TypeCheck.setError();
			} else {
			
				methodTable = classTable.getMethodTable(n.f2.f0.tokenImage);
				
				/**
				 * exp.method()'s method no declaration
				 */
				if (methodTable == null) {
					ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.NoDeclarationException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
					ErrorInfo.addlnInfo("\tEncountered no declaration of method \""+n.f2.f0.tokenImage+"\" of at line "+n.f2.f0.beginLine);
					TypeCheck.setError();
				}
			}
		}
		
		n.f1.accept(this, argu);
		
		TypeTable type2 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f2.f0.beginLine, argu.globalTable);
		String str2 = n.f2.accept(this, type2);
		
		n.f3.accept(this, argu);
		
		ParamTable paramList = new ParamTable(argu.getMethodName(), argu.getClassName(), n.f3.beginLine, argu.globalTable);
		
		String str4 = n.f4.accept(this, paramList);
		
		if (methodTable != null) {
			/**
			 * exp.method()'s parameters' type error
			 */
			if (!methodTable.isParamListCompatible(paramList)) {
				ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
				ErrorInfo.addInfo("\tEncountered \""+str0+"."+str2+"(");
				for (int i = 0; i < paramList.getLength(); i++) ErrorInfo.addInfo("type: "+paramList.getParamType(i).getTypeName()+" ");
				ErrorInfo.addlnInfo(")\" at line "+n.f1.beginLine);
				ErrorInfo.addInfo("\tWas expecting: \""+str0+"."+str2+"(");
				for (int i = 0; i < methodTable.getParamLength(); i++) ErrorInfo.addInfo("type: "+methodTable.getParamType(i).getTypeName()+" ");
				ErrorInfo.addlnInfo(")\"");
				TypeCheck.setError();
			} else {
				argu.setTypeName(methodTable.getReturnType().getTypeName());
			}
		}
		
		n.f5.accept(this, argu);
		
		_ret = str0+"."+str2+"("+str4+")";
		return _ret;
	}
	@Override
	public String visit(ExpressionList n, SymbolTable argu) {
		String _ret = null;
		
		String str0 = n.f0.accept(this, argu);
		String str1 = n.f1.accept(this, argu);
		
		_ret = str0+str1;
		return _ret;
	}
	@Override
	public String visit(Expression n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), argu.getLineNumber(), argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		if (argu instanceof ParamTable) ((ParamTable)argu).insertParam(str0, type0);
		if (argu instanceof TypeTable) argu.setTypeName(type0.getTypeName());
		
		_ret = str0;
		return _ret;
	}
	@Override
	public String visit(ExpressionRest n, SymbolTable argu) {
		String _ret = null;
		
		n.f0.accept(this, argu);
		
		TypeTable type1 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), argu.getLineNumber(), argu.globalTable);
		String str1 = n.f1.accept(this, type1);
		
		if (argu instanceof ParamTable) ((ParamTable)argu).insertParam(str1, type1);
		if (argu instanceof TypeTable) argu.setTypeName(type1.getTypeName());
		
		_ret = ","+str1;
		return _ret;
	}
	@Override
	public String visit(AllocationExpression n, SymbolTable argu) {
		String _ret = null;
		
		n.f0.accept(this, argu);
		
		TypeTable type1 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f1.f0.beginLine, argu.globalTable);
		String str1 = n.f1.accept(this, type1);
		
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		
		/**
		 * new id()'s id no declaration
		 */
		if (classTable == null) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.NoDeclarationException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered no declaration of class\""+n.f1.f0.tokenImage+"\" at line "+n.f1.f0.beginLine);
			TypeCheck.setError();
		} else {
			argu.setTypeName(classTable.getClassName());
		}
		
		_ret = "new"+str1+"()";
		return _ret;
	}
	@Override
	public String visit(ArrayAllocationExpression n, SymbolTable argu) {
		String _ret = null;
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		
		TypeTable type3 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f2.beginLine, argu.globalTable);
		String str3 = n.f3.accept(this, type3);
		
		n.f4.accept(this, argu);
		
		/**
		 * new int[exp]'s exp is not 'int'
		 */
		if (!CloseUnknown || !type3.getTypeName().equals("UnknownType"))
		if (!type3.getTypeName().equals("int")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"new int[type: "+type3.getTypeName()+"]\" at line "+n.f2.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"new int[type: int]\"");
			TypeCheck.setError();
		}
		
		if (type3.getTypeName().equals("int")) argu.setTypeName("int[]");
		
		_ret = "new int["+str3+"]";
		return _ret;
	}
	@Override
	public String visit(PrimaryExpression n, SymbolTable argu) {
		String _ret = null;
		
		TypeTable type0 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), argu.getLineNumber(), argu.globalTable);
		String str0 = n.f0.accept(this, type0);
		
		if (n.f0.which == 3 && type0.isClassType()) {

			TypeTable idType = null;

			/**
			 * identifier no declaration
			 */
			if (argu.getMethodName() != null) idType = argu.globalTable.getClassTable(argu.getClassName()).getMethodTable(argu.getMethodName()).getVariableType(str0);
			if (idType == null) idType = argu.globalTable.getClassTable(argu.getClassName()).getVariableType(str0);
			if (idType == null) {
				ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.NoDeclarationException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
				ErrorInfo.addlnInfo("\tEncountered no declaration of id\""+str0+"\" at line "+argu.getLineNumber());
				TypeCheck.setError();
			}
		}
		
		argu.setTypeName(type0.getTypeName());
		
		_ret = str0;
		return _ret;
	}
	@Override
	public String visit(NotExpression n, SymbolTable argu) {
		String _ret = null;
		
		n.f0.accept(this, argu);
		
		TypeTable type1 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f0.beginLine, argu.globalTable);
		String str1 = n.f1.accept(this, type1);
		
		/**
		 * !exp's exp is not 'boolean'
		 */
		if (!CloseUnknown || !type1.getTypeName().equals("UnknownType"))
		if (!type1.getTypeName().equals("boolean")) {
			ErrorInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.TypeException: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			ErrorInfo.addlnInfo("\tEncountered \"!(type: "+type1.getTypeName()+")\" at line "+n.f0.beginLine);
			ErrorInfo.addlnInfo("\tWas expecting: \"!(type: boolean)\"");
			TypeCheck.setError();
		}
		
		if (type1.getTypeName().equals("boolean")) argu.setTypeName("boolean");
		
		_ret = "!"+str1;
		return _ret;
	}
	@Override
	public String visit(Identifier n, SymbolTable argu) {
		String _ret = n.f0.tokenImage;
		
		n.f0.accept(this, argu);
		
		TypeTable idType = null;
		
		if (argu.getMethodName() != null) idType = argu.globalTable.getClassTable(argu.getClassName()).getMethodTable(argu.getMethodName()).getVariableType(n.f0.tokenImage);
		if (idType == null) idType = argu.globalTable.getClassTable(argu.getClassName()).getVariableType(n.f0.tokenImage);
		if (idType != null) argu.setTypeName(idType.getTypeName());
		
		/**
		 * identifier uninitialized warning
		 */
		if (idType != null) idType.setUsed(true);
		if (EnableWarning && argu.getMethodName() != null && idType != null && !idType.getInited()) {
			WarningInfo.addlnInfo("Exception in thread \"main\" minijava.typecheck.UninitializedWarning: in Method \""+argu.getMethodName()+"\" (of Class "+argu.getClassName()+")");
			WarningInfo.addlnInfo("\tEncountered uninitialized Identifier \""+n.f0.tokenImage+"\" at line "+n.f0.beginLine);
		}
		
		return _ret;
	}
	@Override
	public String visit(IntegerLiteral n, SymbolTable argu) {
		String _ret = n.f0.tokenImage;
		
		n.f0.accept(this, argu);
		
		argu.setTypeName("int");
		
		return _ret;
	}
	@Override
	public String visit(TrueLiteral n, SymbolTable argu) {
		String _ret = n.f0.tokenImage;
		
		n.f0.accept(this, argu);
		
		argu.setTypeName("boolean");
		
		return _ret;
	}
	@Override
	public String visit(FalseLiteral n, SymbolTable argu) {
		String _ret = n.f0.tokenImage;
		
		n.f0.accept(this, argu);
		
		argu.setTypeName("boolean");
		
		return _ret;
	}
	@Override
	public String visit(ThisExpression n, SymbolTable argu) {
		String _ret = n.f0.tokenImage;
		
		n.f0.accept(this, argu);
		
		argu.setTypeName(argu.getClassName());
		
		return _ret;
	}
	@Override
	public String visit(BracketExpression n, SymbolTable argu) {
		String _ret = null;
		
		n.f0.accept(this, argu);
		
		TypeTable type1 = new TypeTable("UnknowExp", "UnknownType", argu.getMethodName(), argu.getClassName(), n.f0.beginLine, argu.globalTable);
		String str1 = n.f1.accept(this, type1);
		
		n.f2.accept(this, argu);
		
		argu.setTypeName(type1.getTypeName());
		
		_ret = "("+str1+")";
		return _ret;
	}
	@Override
	public String visit(VarDeclaration n, SymbolTable argu) {
		String _ret = null;
		n.f0.accept(this, argu);
		if (argu.getMethodName() != null) {
			MethodTable methodTable = argu.globalTable.getClassTable(argu.getClassName()).getMethodTable(argu.getMethodName());
			if (methodTable != null) methodTable.getVariableType(n.f1.f0.tokenImage).setInited(TypeTable.INITED);
			n.f1.accept(this, argu);
			if (methodTable != null) methodTable.getVariableType(n.f1.f0.tokenImage).setInited(TypeTable.UNINITED);
			methodTable.getVariableType(n.f1.f0.tokenImage).setUsed(false);
		} else n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}
}
