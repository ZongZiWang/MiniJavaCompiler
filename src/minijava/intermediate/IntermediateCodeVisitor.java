package minijava.intermediate;

import java.util.Enumeration;

import minijava.binary.ISA;
import minijava.typecheck.symboltable.*;
import syntaxtree.*;
import util.Mode;
import util.Optimize;
import util.TypeChange;
import visitor.GJDepthFirst;
/**
 * Generate Intermediate Code by Visitor
 * @author ZongZiWang
 *
 */
public class IntermediateCodeVisitor extends GJDepthFirst<CodeStructure, SymbolTable>{
	/**
	 * write all dirty fields back to memory
	 * @param argu
	 * @param cs
	 */
	private void writeBackAllDirtyFields(SymbolTable argu, CodeStructure cs) {
		for (String memberVar: argu.globalTable.getClassTable(argu.getClassName()).getMemberVariables().keySet()) {
			TypeTable tt = argu.globalTable.getClassTable(argu.getClassName()).getMemberVariables().get(memberVar);
			if (tt.getState() == TypeTable.DIRTY) {
				if (tt.getTypeName().contains("int") && !tt.getTypeName().contains("[]")) TargetCode.AddCode(0x59, cs);
				else if (tt.getTypeName().contains("boolean")) TargetCode.AddCode(0x5c, cs);
				else TargetCode.AddCode(0x5b, cs);
				// 0x59 22c iput vA, vB, field@CCCC; B.@CCCC = A
				// 0x5b 22c iput-object vA, vB, field@CCCC; B.@CCCC = A
				// 0x5c 22c iput-boolean vA, vB, field@CCCC; B.@CCCC = A
				TargetCode.AddParam("A", tt.getReg());
				TargetCode.AddParam("B", "this");
				TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetFieldIdx(argu.getClassName(), tt.getTypeName(), memberVar));
			}
			tt.setState(TypeTable.FREE);
		}
	}
	
	@Override
	public CodeStructure visit(NodeList n, SymbolTable argu) {
		CodeStructure _ret = null;
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
			if (_ret == null) _ret = e.nextElement().accept(this,argu);
			else e.nextElement().accept(this,argu);
		}
		return _ret;
	}
	
	@Override
	public CodeStructure visit(NodeListOptional n, SymbolTable argu) {
		if ( n.present() ) {
			CodeStructure _ret = null;
			for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
				if (_ret == null) _ret = e.nextElement().accept(this,argu);
				else e.nextElement().accept(this,argu);
			}
			return _ret;
		} else return null;
	}
	
	@Override
	public CodeStructure visit(MainClass n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("declare");
		
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		MethodTable methodTable = classTable.getMethodTable("main");
		TargetCode.NewMethod(methodTable);
		n.f3.accept(this, methodTable);
		n.f4.accept(this, methodTable);
		n.f5.accept(this, methodTable);
		n.f6.accept(this, methodTable);
		n.f7.accept(this, methodTable);
		n.f8.accept(this, methodTable);
		n.f9.accept(this, methodTable);
		n.f10.accept(this, methodTable);
		//n.f11.accept(this, methodTable);
		n.f12.accept(this, methodTable);
		n.f13.accept(this, methodTable);
		CodeStructure cs14 = n.f14.accept(this, methodTable);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs14.getCodeIdx());
		n.f15.accept(this, methodTable);
		n.f16.accept(this, classTable);
		
		TargetCode.AddCode(0x0e, cs);
		//0e 10x return-void
		
		methodTable.updateRegNum(ApplyResource.getMaxUsedRegNum()+1);
		methodTable.setThisRegisterNumber(-1);
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(ClassDeclaration n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("declare");
		
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		/*
		 * Must not add this code!!!
		 * n.f3.accept(this, classTable);
		 */
		CodeStructure cs4 = n.f4.accept(this, classTable);
		if (cs4 != null) if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs4.getCodeIdx());
		
		n.f5.accept(this, classTable);
		return cs;
	}
	
	@Override
	public CodeStructure visit(ClassExtendsDeclaration n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("declare");
		
		ClassTable classTable = argu.globalTable.getClassTable(n.f1.f0.tokenImage);
		n.f0.accept(this, classTable);
		n.f1.accept(this, classTable);
		n.f2.accept(this, classTable);
		n.f3.accept(this, classTable);
		n.f4.accept(this, classTable);
		/*
		 * Must not add this code!!!
		 * n.f5.accept(this, classTable);
		 */
		CodeStructure cs6 = n.f6.accept(this, classTable);
		if (cs6 != null) if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs6.getCodeIdx());
		
		n.f7.accept(this, classTable);
		return cs;
	}
	
	@Override
	public CodeStructure visit(MethodDeclaration n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("declare");
		
		MethodTable methodTable = ((ClassTable)argu).getMethodTable(n.f2.f0.tokenImage);
		TargetCode.NewMethod(methodTable);
		n.f0.accept(this, methodTable);
		n.f1.accept(this, methodTable);
		n.f2.accept(this, methodTable);
		n.f3.accept(this, methodTable);
		n.f4.accept(this, methodTable);
		n.f5.accept(this, methodTable);
		n.f6.accept(this, methodTable);
		//n.f7.accept(this, methodTable);
		CodeStructure cs8 = n.f8.accept(this, methodTable);
		n.f9.accept(this, methodTable);
		CodeStructure cs10 = n.f10.accept(this, methodTable);
		n.f11.accept(this, methodTable);
		n.f12.accept(this, methodTable);
		
		if (cs8 != null) if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs8.getCodeIdx());
		else if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs10.getCodeIdx());

		writeBackAllDirtyFields(argu, cs);
		
		if (cs10.getType().equals("^_^boolean")) {
			cs10.setType("boolean");
			cs10.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x12, cs);
			// 12 11n const/4 vA, #+B
			TargetCode.AddParam("A", cs10.getReg());
			TargetCode.AddParam("B", "^_^"+cs10.getLiteralVal());
		} else if (cs10.getType().equals("^_^int")) {
			cs10.setType("int");
			cs10.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x13, cs);
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs10.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs10.getLiteralVal());
		}
		
		if (cs10.getType().equals("int") || cs10.getType().equals("boolean") || cs10.getType().endsWith("@id")) {
			TargetCode.AddCode(0x0f, cs);
			//0f 11x return vAA
			TargetCode.AddParam("AA", cs10.getReg());
		} else {
			TargetCode.AddCode(0x11, cs);
			//11 11x return-object vAA
			TargetCode.AddParam("AA", cs10.getReg());
		}

		methodTable.updateRegNum(ApplyResource.getMaxUsedRegNum()+1);
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(Statement n, SymbolTable argu) {
		CodeStructure cs = n.f0.accept(this, argu);
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(Block n, SymbolTable argu) {
		n.f0.accept(this, argu);
		CodeStructure cs = n.f1.accept(this, argu);
		n.f2.accept(this, argu);

		return cs;
	}
	
	@Override
	public CodeStructure visit(AssignmentStatement n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("state");
		
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		
		if (cs0.getIsField()) {
			
			if (cs2.getType().equals("^_^boolean")) {
				argu.globalTable.getClassTable(argu.getClassName()).getVariableType(cs0.getId()).setValue(""+cs2.getLiteralVal());
				cs.setCodeIdx(TargetCode.AddCode(0x12, cs));
				// 12 11n const/4 vA, #+B
				TargetCode.AddParam("A", cs0.getReg());
				TargetCode.AddParam("B", "^_^"+cs2.getLiteralVal());
			} else if (cs2.getType().equals("^_^int")) {
				argu.globalTable.getClassTable(argu.getClassName()).getVariableType(cs0.getId()).setValue(""+cs2.getLiteralVal());
				cs.setCodeIdx(TargetCode.AddCode(0x13, cs));
				// 13 21s const/16 vAA, #+BBBB
				TargetCode.AddParam("AA", cs0.getReg());
				TargetCode.AddParam("BBBB", "^_^"+cs2.getLiteralVal());
			} else if (cs2.getType().equals("int") || cs2.getType().equals("boolean") || cs2.getType().endsWith("@id")) {
				if (TargetCode.nowMethodTargetCode.codeSize() > 0
						&& !TargetCode.nowMethodTargetCode.isLastMoveOrConstToVar()
						&& ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1)).length > 0
						&& cs2.getReg().equals(TargetCode.nowMethodTargetCode.getCodeParamValue(
							TargetCode.nowMethodTargetCode.codeSize()-1, 
							ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1))[0])
						)
					) {
					TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1).replace(cs2.getReg(), cs0.getReg());
				} else {
					TargetCode.AddCode(0x01, cs);
					//01 12x move vA, vB; vA = vB;
					TargetCode.AddParam("A", cs0.getReg());
					TargetCode.AddParam("B", cs2.getReg());
				}
				ApplyResource.recycleReg(cs2.getReg());
			} else {
				if (TargetCode.nowMethodTargetCode.codeSize() > 0
						&& !TargetCode.nowMethodTargetCode.isLastMoveOrConstToVar()
						&& ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1)).length > 0
						&& cs2.getReg().equals(TargetCode.nowMethodTargetCode.getCodeParamValue(
							TargetCode.nowMethodTargetCode.codeSize()-1, 
							ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1))[0])
						)
					) {
					TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1).replace(cs2.getReg(), cs0.getReg());
				} else {
					TargetCode.AddCode(0x07, cs);
					//07 12x move-object vA, vB; vA = vB;
					TargetCode.AddParam("A", cs0.getReg());
					TargetCode.AddParam("B", cs2.getReg());
				}
				ApplyResource.recycleReg(cs2.getReg());
			}

			argu.globalTable.getClassTable(argu.getClassName()).getVariableType(cs0.getId()).setState(TypeTable.DIRTY);
			if (!Optimize.isO1()) {
				TypeTable tt = argu.globalTable.getClassTable(argu.getClassName()).getVariableType(cs0.getId());
				if (tt.getTypeName().contains("int") && !tt.getTypeName().contains("[]")) TargetCode.AddCode(0x59, cs);
				else if (tt.getTypeName().contains("boolean")) TargetCode.AddCode(0x5c, cs);
				else TargetCode.AddCode(0x5b, cs);
				// 0x59 22c iput vA, vB, field@CCCC; B.@CCCC = A
				// 0x5b 22c iput-object vA, vB, field@CCCC; B.@CCCC = A
				// 0x5c 22c iput-boolean vA, vB, field@CCCC; B.@CCCC = A
				TargetCode.AddParam("A", tt.getReg());
				TargetCode.AddParam("B", "this");
				TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetFieldIdx(argu.getClassName(), tt.getTypeName(), tt.getVarName()));
				tt.setState(TypeTable.FREE);
			}
			
		} else {
			
			if (cs2.getType().equals("^_^boolean")) {
				argu.globalTable.getClassTable(argu.getClassName()).getMethodTable(argu.getMethodName()).getVariableType(cs0.getId()).setValue(""+cs2.getLiteralVal());
				cs.setCodeIdx(TargetCode.AddCode(0x12, cs));
				// 12 11n const/4 vA, #+B
				TargetCode.AddParam("A", cs0.getReg());
				TargetCode.AddParam("B", "^_^"+cs2.getLiteralVal());
			} else if (cs2.getType().equals("^_^int")) {
				argu.globalTable.getClassTable(argu.getClassName()).getMethodTable(argu.getMethodName()).getVariableType(cs0.getId()).setValue(""+cs2.getLiteralVal());
				cs.setCodeIdx(TargetCode.AddCode(0x13, cs));
				// 13 21s const/16 vAA, #+BBBB
				TargetCode.AddParam("AA", cs0.getReg());
				TargetCode.AddParam("BBBB", "^_^"+cs2.getLiteralVal());
			} else if (cs2.getType().equals("int") || cs2.getType().equals("boolean") || cs2.getType().contains("@id")) {
				if (TargetCode.nowMethodTargetCode.codeSize() > 0
						&& !TargetCode.nowMethodTargetCode.isLastMoveOrConstToVar()
						&& ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1)).length > 0
						&& cs2.getReg().equals(TargetCode.nowMethodTargetCode.getCodeParamValue(
							TargetCode.nowMethodTargetCode.codeSize()-1, 
							ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1))[0])
						)
					) {
					TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1).replace(cs2.getReg(), cs0.getReg());
				} else {
					TargetCode.AddCode(0x01, cs);
					//01 12x move vA, vB; vA = vB;
					TargetCode.AddParam("A", cs0.getReg());
					TargetCode.AddParam("B", cs2.getReg());
				}
				ApplyResource.recycleReg(cs2.getReg());
			} else {
				if (TargetCode.nowMethodTargetCode.codeSize() > 0
						&& !TargetCode.nowMethodTargetCode.isLastMoveOrConstToVar()
						&& ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1)).length > 0
						&& cs2.getReg().equals(TargetCode.nowMethodTargetCode.getCodeParamValue(
							TargetCode.nowMethodTargetCode.codeSize()-1, 
							ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1))[0])
						)
					) {
					TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1).replace(cs2.getReg(), cs0.getReg());
				} else {
					TargetCode.AddCode(0x07, cs);
					//07 12x move-object vA, vB; vA = vB;
					TargetCode.AddParam("A", cs0.getReg());
					TargetCode.AddParam("B", cs2.getReg());
				}
				ApplyResource.recycleReg(cs2.getReg());
			}
		}
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(ArrayAssignmentStatement n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("state");
		
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		
		if (cs2.getType().equals("^_^int")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x13, cs);
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs2.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs2.getLiteralVal());
		}
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		CodeStructure cs5 = n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		
		if (cs5.getType().equals("^_^int")) {
			cs5.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x13, cs);
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs5.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs5.getLiteralVal());
		}

		CodeStructure cs0 = n.f0.accept(this, argu);

		if (cs0.getType().equals("int[]")) {
			if (cs0.getIsField()) argu.globalTable.getClassTable(argu.getClassName()).getVariableType(cs0.getId()).setState(TypeTable.DIRTY);
			TargetCode.AddCode(0x4b, cs);
			//4b 23x aput vAA, vBB. vCC; BB[CC] = AA;
			TargetCode.AddParam("BB", cs0.getReg());
			TargetCode.AddParam("CC", cs2.getReg());
			TargetCode.AddParam("AA", cs5.getReg());
		}
		
		ApplyResource.recycleReg(cs2.getReg());
		ApplyResource.recycleReg(cs5.getReg());
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(IfStatement n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("state");
		
		if (n.f2.f0.which == 1) {
			CompareExpression ce = (CompareExpression) n.f2.f0.choice;
				
			CodeStructure cecs0 = ce.f0.accept(this, argu);
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cecs0.getCodeIdx());
				
			CodeStructure cecs2 = ce.f2.accept(this, argu);
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cecs2.getCodeIdx());
				
			if ((!ApplyResource.getWhileMode() && cecs0.getType().startsWith("^_^int") && cecs2.getType().startsWith("^_^int")) || (cecs0.getType().equals("^_^int") && cecs2.getType().equals("^_^int"))) {
				if (cecs0.getLiteralVal() < cecs2.getLiteralVal()) {
					cs = n.f4.accept(this, argu);
				} else {
					cs = n.f6.accept(this, argu);
				}
				return cs;
			}
				
			if (cecs0.getType().equals("^_^int")) {
				if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cecs2.getCodeIdx());
				cecs0.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
				TargetCode.AddCode(0x13, cs);
				// 13 21s const/16 vAA, #+BBBB
				TargetCode.AddParam("AA", cecs0.getReg());
				TargetCode.AddParam("BBBB", "^_^"+cecs0.getLiteralVal());
			}
			
			if (cecs2.getType().equals("^_^int")) {
				cecs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
				TargetCode.AddCode(0x13, cs);
				// 13 21s const/16 vAA, #+BBBB
				TargetCode.AddParam("AA", cecs2.getReg());
				TargetCode.AddParam("BBBB", "^_^"+cecs2.getLiteralVal());
			}
			
			writeBackAllDirtyFields(argu, cs);
			
			CodeStructure csIfNot = new CodeStructure("state");
			csIfNot.setCodeIdx(TargetCode.AddCode(0x35, cs));
			// 35 22t if-ge vA, vB, +CCCC
			TargetCode.AddParam("A", cecs0.getReg());
			TargetCode.AddParam("B", cecs2.getReg());
			
			n.f4.accept(this, argu);
			
			writeBackAllDirtyFields(argu, cs);
			
			// csExit:		goto/16 cs
			CodeStructure csExit = new CodeStructure("state");
			csExit.setCodeIdx(TargetCode.AddCode(0x29, cs));
			// 29 20t goto/16 +AAAA 
			
			CodeStructure cs6 = n.f6.accept(this, argu);

			if (cs6 != null) TargetCode.AddParam(csIfNot.getCodeIdx(), "CCCC", (TargetCode.CS2LineNumber(cs6)-TargetCode.CS2LineNumber(csIfNot))/2+"");
			else TargetCode.AddParam(csIfNot.getCodeIdx(), "CCCC", (TargetCode.CS2LineNumber(csExit)+TargetCode.CS2CodeLength(csExit)-TargetCode.CS2LineNumber(csIfNot))/2+"");
			
			if (cs6 != null) TargetCode.AddParam(csExit.getCodeIdx(), "AAAA", (TargetCode.nowMethodTargetCode.getLineNumber()-TargetCode.CS2LineNumber(csExit))/2+"");
			else TargetCode.AddParam(csExit.getCodeIdx(), "AAAA", TargetCode.CS2CodeLength(csExit)/2+"");
			
			ApplyResource.recycleReg(cecs0.getReg());
			ApplyResource.recycleReg(cecs2.getReg());
			return cs;
		}
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		n.f3.accept(this, argu);
		
		if ((!ApplyResource.getWhileMode() && cs2.getType().startsWith("^_^boolean")) || cs2.getType().equals("^_^boolean")) {
			if (cs2.getLiteralVal() == 1) {
				cs = n.f4.accept(this, argu);
			} else {
				cs = n.f6.accept(this, argu);
			}
			return cs;
		}
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure csIfNot = new CodeStructure("state");
		csIfNot.setCodeIdx(TargetCode.AddCode(0x38, cs));
		//38 21t if-eqz vAA, +BBBB;
		
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure csExit = new CodeStructure("state");
		csExit.setCodeIdx(TargetCode.AddCode(0x29, cs));
		//29 20t goto +AAAA;
		
		CodeStructure cs6 = n.f6.accept(this, argu);

		TargetCode.AddParam(csIfNot.getCodeIdx(), "AA", cs2.getReg());
		if (cs6 != null) TargetCode.AddParam(csIfNot.getCodeIdx(), "BBBB", (TargetCode.CS2LineNumber(cs6)-TargetCode.CS2LineNumber(csIfNot))/2+"");
		else TargetCode.AddParam(csIfNot.getCodeIdx(), "BBBB", (TargetCode.CS2LineNumber(csExit)+TargetCode.CS2CodeLength(csExit)-TargetCode.CS2LineNumber(csIfNot))/2+"");
		
		if (cs6 != null) TargetCode.AddParam(csExit.getCodeIdx(), "AAAA", (TargetCode.nowMethodTargetCode.getLineNumber()-TargetCode.CS2LineNumber(csExit))/2+"");
		else TargetCode.AddParam(csExit.getCodeIdx(), "AAAA", TargetCode.CS2CodeLength(csExit)/2+"");
		
		ApplyResource.recycleReg(cs2.getReg());
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(WhileStatement n, SymbolTable argu) {
		ApplyResource.setWhileMode();
		CodeStructure cs = new CodeStructure("state");
		
		if (n.f2.f0.which == 1) {
			CompareExpression ce = (CompareExpression) n.f2.f0.choice;
				
			CodeStructure cecs0 = ce.f0.accept(this, argu);
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cecs0.getCodeIdx());
				
			CodeStructure cecs2 = ce.f2.accept(this, argu);
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cecs2.getCodeIdx());
				
			if ((cecs0.getType().equals("^_^int") && cecs2.getType().equals("^_^int"))) {
				if (cecs0.getLiteralVal() < cecs2.getLiteralVal()) {
					System.err.println("Infinitive Loop!");
				} else return cs;
			}
				
			if (cecs0.getType().equals("^_^int")) {
				if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cecs2.getCodeIdx());
				cecs0.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
				TargetCode.AddCode(0x13, cs);
				// 13 21s const/16 vAA, #+BBBB
				TargetCode.AddParam("AA", cecs0.getReg());
				TargetCode.AddParam("BBBB", "^_^"+cecs0.getLiteralVal());
			}
			
			if (cecs2.getType().equals("^_^int")) {
				cecs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
				TargetCode.AddCode(0x13, cs);
				// 13 21s const/16 vAA, #+BBBB
				TargetCode.AddParam("AA", cecs2.getReg());
				TargetCode.AddParam("BBBB", "^_^"+cecs2.getLiteralVal());
			}
			
			writeBackAllDirtyFields(argu, cs);
			
			CodeStructure csWhileNot = new CodeStructure("state");
			csWhileNot.setCodeIdx(TargetCode.AddCode(0x35, cs));
			// 35 22t if-ge vA, vB, +CCCC
			TargetCode.AddParam("A", cecs0.getReg());
			TargetCode.AddParam("B", cecs2.getReg());
			
			n.f4.accept(this, argu);
			
			writeBackAllDirtyFields(argu, cs);
			
			CodeStructure csLoop = new CodeStructure("state");
			csLoop.setCodeIdx(TargetCode.AddCode(0x29, cs));
			//29 20t goto +AAAA;
			TargetCode.AddParam("AAAA", (TargetCode.CS2LineNumber(cs)-TargetCode.CS2LineNumber(csLoop))/2+"");
			
			TargetCode.AddParam(csWhileNot.getCodeIdx(), "CCCC", (TargetCode.CS2LineNumber(csLoop)+TargetCode.CS2CodeLength(csLoop)-TargetCode.CS2LineNumber(csWhileNot))/2+"");

			ApplyResource.recycleReg(cecs0.getReg());
			ApplyResource.recycleReg(cecs2.getReg());
			
			ApplyResource.exitWhileMode();
			return cs;
		}
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		
		n.f3.accept(this, argu);
		
		if (((!ApplyResource.getWhileMode() && cs2.getType().startsWith("^_^boolean")) || cs2.getType().equals("^_^boolean")) && cs2.getLiteralVal() == 0) return cs;
		if (cs2.getType().equals("^_^boolean") && cs2.getLiteralVal() == 1) {
			System.err.println("Infinitive Loop!");
		}
		
		if (cs2.getType().equals("^_^boolean")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x12, cs);
			// 12 11n const/4 vA, #+B
			TargetCode.AddParam("A", cs2.getReg());
			TargetCode.AddParam("B", "^_^"+cs2.getLiteralVal());
		}
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure csWhileNot = new CodeStructure("state");
		csWhileNot.setCodeIdx(TargetCode.AddCode(0x38, cs));
		//38 21t if-eqz vAA, +BBBB;
		
		n.f4.accept(this, argu);
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure csLoop = new CodeStructure("state");
		csLoop.setCodeIdx(TargetCode.AddCode(0x29, cs));
		//29 20t goto +AAAA;
		TargetCode.AddParam("AAAA", (TargetCode.CS2LineNumber(cs)-TargetCode.CS2LineNumber(csLoop))/2+"");
		
		TargetCode.AddParam(csWhileNot.getCodeIdx(), "AA", cs2.getReg());
		TargetCode.AddParam(csWhileNot.getCodeIdx(), "BBBB", (TargetCode.CS2LineNumber(csLoop)+TargetCode.CS2CodeLength(csLoop)-TargetCode.CS2LineNumber(csWhileNot))/2+"");

		ApplyResource.recycleReg(cs2.getReg());
		
		ApplyResource.exitWhileMode();
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(PrintStatement n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("state");
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);

		if (cs2.getType().equals("^_^boolean")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x12, cs);
			// 12 11n const/4 vA, #+B
			TargetCode.AddParam("A", cs2.getReg());
			TargetCode.AddParam("B", ""+cs2.getLiteralVal());
		} else if (cs2.getType().equals("^_^int")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x13, cs);
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs2.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs2.getLiteralVal());
		}
		
		if (!Mode.IsOutputApk()) 
		{
			cs.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x62, cs);
			//62 21c sget-object vAA, field@BBBB;
			TargetCode.AddParam("AA", cs.getReg());
			TargetCode.AddParam("BBBB", ""+argu.globalTable.dexPrinter.GetFieldIdx("java/lang/System", "java/io/PrintStream", "out"));
			
			TargetCode.AddCode(0xff, cs);
			// ff print vA; System.out.println(vA);
			// 6e 35c invoke-virtual {vD, vE, vF, vG, vA}, meth@CCCC
			TargetCode.AddParam("B", String.valueOf(1+1));
			TargetCode.AddParam("D", cs.getReg());
			TargetCode.AddParam("E", cs2.getReg());
			TargetCode.AddParam("F", String.valueOf(0));
			TargetCode.AddParam("G", String.valueOf(0));
			TargetCode.AddParam("A", String.valueOf(0));
			TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetMethodIdx("java/io/PrintStream", "println"));
			
			ApplyResource.recycleReg(cs.getReg());
			ApplyResource.recycleReg(cs2.getReg());
		}
		else 
		{
			TargetCode.AddCode(0x71, cs);
			// 71 35c invoke-static {vD, vE, vF, vG, vA}, meth@CCCC
			TargetCode.AddParam("B", String.valueOf(1));
			TargetCode.AddParam("D", cs2.getReg());
			TargetCode.AddParam("E", String.valueOf(0));
			TargetCode.AddParam("F", String.valueOf(0));
			TargetCode.AddParam("G", String.valueOf(0));
			TargetCode.AddParam("A", String.valueOf(0));
			TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetMethodIdx("minijava/output/MiniJavaOutput", "addAnswer"));
			
			ApplyResource.recycleReg(cs2.getReg());
		}
		
	
		return cs;
	}
	
	@Override
	public CodeStructure visit(Expression n, SymbolTable argu) {
		CodeStructure cs = n.f0.accept(this, argu);
		return cs;
	}

	@Override
	public CodeStructure visit(AndExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("boolean");
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		
		if (((!ApplyResource.getWhileMode() && cs0.getType().startsWith("^_^boolean")) || cs0.getType().equals("^_^boolean")) && cs0.getLiteralVal() == 0) {
			cs.setType("^_^boolean");
			cs.setLiteralVal(0);
			return cs;
		}
		
		if (cs0.getType().equals("^_^boolean")) {
			cs0.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			cs.setCodeIdx(TargetCode.AddCode(0x12, cs));
			// 12 11n const/4 vA, #+B
			TargetCode.AddParam("A", cs0.getReg());
			TargetCode.AddParam("B", "^_^"+cs0.getLiteralVal());
		}
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure csIfFalse = new CodeStructure("state");
		csIfFalse.setCodeIdx(TargetCode.AddCode(0x38, cs));
		//38 21t if-eqz vAA, +BBBB;
		
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		
		if (cs2.getType().equals("^_^boolean")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x12, cs);
			// 12 11n const/4 vA, #+B
			TargetCode.AddParam("A", cs2.getReg());
			TargetCode.AddParam("B", "^_^"+cs2.getLiteralVal());
		}
		
		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		TargetCode.AddCode(0x95, cs); 
		// 95 23x and-int vAA, vBB, vCC; vAA = vBB && vCC
		TargetCode.AddParam("AA", dest);
		TargetCode.AddParam("BB", cs0.getReg());
		TargetCode.AddParam("CC", cs2.getReg());
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure csEnd = new CodeStructure("state");
		csEnd.setCodeIdx(TargetCode.AddCode(0x28, cs));
		//28 10t goto +AA;
		
		CodeStructure csSetFalse = new CodeStructure("state");
		csSetFalse.setCodeIdx(TargetCode.AddCode(0x12, cs));
		// 12 11n const/4 vA, #+B
		TargetCode.AddParam("A", dest);
		TargetCode.AddParam("B", "^_^0");
		
		TargetCode.AddParam(csIfFalse.getCodeIdx(), "AA", cs0.getReg());
		TargetCode.AddParam(csIfFalse.getCodeIdx(), "BBBB", (TargetCode.CS2LineNumber(csSetFalse)-TargetCode.CS2LineNumber(csIfFalse))/2+"");
		
		TargetCode.AddParam(csEnd.getCodeIdx(), "AA", (TargetCode.CS2LineNumber(csSetFalse)+TargetCode.CS2CodeLength(csSetFalse)-TargetCode.CS2LineNumber(csEnd))/2+"");
		
		ApplyResource.recycleReg(cs0.getReg());
		ApplyResource.recycleReg(cs2.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(CompareExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("boolean");
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		
		if ((!ApplyResource.getWhileMode() && cs0.getType().startsWith("^_^int") && cs2.getType().startsWith("^_^int")) || (cs0.getType().equals("^_^int") && cs2.getType().equals("^_^int"))) {
			cs.setType("^_^boolean");
			if (cs0.getLiteralVal() < cs2.getLiteralVal()) cs.setLiteralVal(1);
			else cs.setLiteralVal(0);
			return cs;
		}
		
		if (cs0.getType().equals("^_^int")) {
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
			cs0.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x13, cs);
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs0.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs0.getLiteralVal());
		}
		
		if (cs2.getType().equals("^_^int")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x13, cs);
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs2.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs2.getLiteralVal());
		}
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure csIf = new CodeStructure("state");
		// csIf:		if-lt cs0.getReg(), cs1.getReg(), csIfLt
		csIf.setCodeIdx(TargetCode.AddCode(0x34, cs));
		// 34 22t if-lt vA, vB, +CCCC
		TargetCode.AddParam("A", cs0.getReg());
		TargetCode.AddParam("B", cs2.getReg());
		
		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		// csIfNotLt:	const/4 dest, 0
		CodeStructure csIfNotLt = new CodeStructure("express");
		csIfNotLt.setCodeIdx(TargetCode.AddCode(0x12, cs));
		// 12 11n const/4 vA, #+B 
		TargetCode.AddParam("A", dest);
		TargetCode.AddParam("B", "^_^0");
		
		writeBackAllDirtyFields(argu, cs);

		// csEnd:		goto cs
		CodeStructure csEnd = new CodeStructure("state");
		csEnd.setCodeIdx(TargetCode.AddCode(0x28, cs));
		// 28 10t goto +AA 
		
		// csIfLt:		const/4 dest, 1
		CodeStructure csIfLt = new CodeStructure("state");
		csIfLt.setCodeIdx(TargetCode.AddCode(0x12, cs));
		// 12 11n const/4 vA, #+B 
		TargetCode.AddParam("A", dest);
		TargetCode.AddParam("B", "^_^1");

		TargetCode.AddParam(csIf.getCodeIdx(), "CCCC", (TargetCode.CS2LineNumber(csIfLt)-TargetCode.CS2LineNumber(csIf))/2+"");
		TargetCode.AddParam(csEnd.getCodeIdx(), "AA", (TargetCode.CS2LineNumber(csIfLt)+TargetCode.CS2CodeLength(csIfLt)-TargetCode.CS2LineNumber(csEnd))/2+"");
		
		ApplyResource.recycleReg(cs0.getReg());
		ApplyResource.recycleReg(cs2.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(PlusExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("int");
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());

		if ((!ApplyResource.getWhileMode() && cs0.getType().startsWith("^_^int") && cs2.getType().startsWith("^_^int")) || (cs0.getType().equals("^_^int") && cs2.getType().equals("^_^int"))) {
			cs.setType("^_^int");
			cs.setLiteralVal(cs0.getLiteralVal()+cs2.getLiteralVal());
			return cs;
		}

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		if (cs0.getType().equals("^_^int")) {
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
			if (TypeChange.parseInt(dest) > 15 || (TypeChange.isInt(cs2.getReg()) && TypeChange.parseInt(cs2.getReg()) > 15)) {
				TargetCode.AddCode(0xd8, cs);
				// d8 22b add-int/lit8 vAA, vBB, #+CC;
				TargetCode.AddParam("AA", dest);
				TargetCode.AddParam("BB", cs2.getReg());
				TargetCode.AddParam("CC", "^_^"+cs0.getLiteralVal());
			} else {
				TargetCode.AddCode(0xd0, cs);
				// d0 22s add-int/lit16 vA, vB, #+CCCC;
				TargetCode.AddParam("A", dest);
				TargetCode.AddParam("B", cs2.getReg());
				TargetCode.AddParam("CCCC", "^_^"+cs0.getLiteralVal());
			}
			return cs;
		}
		
		if (cs2.getType().equals("^_^int")) {
			if (TypeChange.parseInt(dest) > 15 || (TypeChange.isInt(cs0.getReg()) && TypeChange.parseInt(cs0.getReg()) > 15)) {
				TargetCode.AddCode(0xd8, cs);
				// d8 22b add-int/lit8 vAA, vBB, #+CC;
				TargetCode.AddParam("AA", dest);
				TargetCode.AddParam("BB", cs0.getReg());
				TargetCode.AddParam("CC", "^_^"+cs2.getLiteralVal());
			} else {
				TargetCode.AddCode(0xd0, cs);
				// d0 22s add-int/lit16 vA, vB, #+CCCC;
				TargetCode.AddParam("A", dest);
				TargetCode.AddParam("B", cs0.getReg());
				TargetCode.AddParam("CCCC", "^_^"+cs2.getLiteralVal());
			}
			return cs;
		}
		
		TargetCode.AddCode(0x90, cs);
		// 90 23x add-int vAA, vBB, vCC; vAA = vBB + vCC
		TargetCode.AddParam("AA", dest);
		TargetCode.AddParam("BB", cs0.getReg());
		TargetCode.AddParam("CC", cs2.getReg());

		ApplyResource.recycleReg(cs0.getReg());
		ApplyResource.recycleReg(cs2.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(MinusExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("int");
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		
		if ((!ApplyResource.getWhileMode() && cs0.getType().startsWith("^_^int") && cs2.getType().startsWith("^_^int")) || (cs0.getType().equals("^_^int") && cs2.getType().equals("^_^int"))) {
			cs.setType("^_^int");
			cs.setLiteralVal(cs0.getLiteralVal()-cs2.getLiteralVal());
			return cs;
		}

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		if (cs0.getType().equals("^_^int")) {
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
			if (TypeChange.parseInt(dest) > 15 || (TypeChange.isInt(cs2.getReg()) && TypeChange.parseInt(cs2.getReg()) > 15)) {
				TargetCode.AddCode(0xd9, cs);
				// d9 22b rsub-int/lit8 vAA, vBB, #+CC;
				TargetCode.AddParam("AA", dest);
				TargetCode.AddParam("BB", cs2.getReg());
				TargetCode.AddParam("CC", "^_^"+cs0.getLiteralVal());
			} else {
				TargetCode.AddCode(0xd1, cs);
				// d1 22s rsub-int/lit16 vA, vB, #+CCCC;
				TargetCode.AddParam("A", dest);
				TargetCode.AddParam("B", cs2.getReg());
				TargetCode.AddParam("CCCC", "^_^"+cs0.getLiteralVal());
			}
			return cs;
		}
		
		if (cs2.getType().equals("^_^int")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			TargetCode.AddCode(0x13, cs);
			// 13 21s const vAA, #+BBBB; vAA = #+BBBB
			TargetCode.AddParam("AA", cs2.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs2.getLiteralVal());
		}
		
		TargetCode.AddCode(0x91, cs);
		// 91 23x sub-int vAA, vBB, vCC; vAA = vBB - vCC
		TargetCode.AddParam("AA", dest);
		TargetCode.AddParam("BB", cs0.getReg());
		TargetCode.AddParam("CC", cs2.getReg());

		ApplyResource.recycleReg(cs0.getReg());
		ApplyResource.recycleReg(cs2.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(TimesExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("int");
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		
		if ((!ApplyResource.getWhileMode() && cs0.getType().startsWith("^_^int") && cs2.getType().startsWith("^_^int")) || (cs0.getType().equals("^_^int") && cs2.getType().equals("^_^int"))) {
			cs.setType("^_^int");
			cs.setLiteralVal(cs0.getLiteralVal()*cs2.getLiteralVal());
			return cs;
		}

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		if (cs0.getType().equals("^_^int")) {
			if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
			if (TypeChange.parseInt(dest) > 15 || (TypeChange.isInt(cs2.getReg()) && TypeChange.parseInt(cs2.getReg()) > 15)) {
				TargetCode.AddCode(0xda, cs);
				// da 22b mul-int/lit8 vAA, vBB, #+CC;
				TargetCode.AddParam("AA", dest);
				TargetCode.AddParam("BB", cs2.getReg());
				TargetCode.AddParam("CC", "^_^"+cs0.getLiteralVal());
			} else {
				TargetCode.AddCode(0xd2, cs);
				// d2 22s mul-int/lit16 vA, vB, #+CCCC;
				TargetCode.AddParam("A", dest);
				TargetCode.AddParam("B", cs2.getReg());
				TargetCode.AddParam("CCCC", "^_^"+cs0.getLiteralVal());
			}
			return cs;
		}
		
		if (cs2.getType().equals("^_^int")) {
			if (TypeChange.parseInt(dest) > 15 || (TypeChange.isInt(cs0.getReg()) && TypeChange.parseInt(cs0.getReg()) > 15)) {
				TargetCode.AddCode(0xda, cs);
				// da 22b mul-int/lit8 vAA, vBB, #+CC;
				TargetCode.AddParam("AA", dest);
				TargetCode.AddParam("BB", cs0.getReg());
				TargetCode.AddParam("CC", "^_^"+cs2.getLiteralVal());
			} else {
				TargetCode.AddCode(0xd2, cs);
				// d2 22s mul-int/lit16 vA, vB, #+CCCC;
				TargetCode.AddParam("A", dest);
				TargetCode.AddParam("B", cs0.getReg());
				TargetCode.AddParam("CCCC", "^_^"+cs2.getLiteralVal());
			}
			return cs;
		}
		
		TargetCode.AddCode(0x92, cs);
		// 92 23x mul-int vAA, vBB, vCC; vAA = vBB + vCC
		TargetCode.AddParam("AA", dest);
		TargetCode.AddParam("BB", cs0.getReg());
		TargetCode.AddParam("CC", cs2.getReg());

		ApplyResource.recycleReg(cs0.getReg());
		ApplyResource.recycleReg(cs2.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(ArrayLookup n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("int");

		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs2.getCodeIdx());
		n.f3.accept(this, argu);
		
		if (cs2.getType().equals("^_^int")) {
			cs2.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			cs2.setCodeIdx(TargetCode.AddCode(0x13, cs));
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs2.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs2.getLiteralVal());
		}

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		TargetCode.AddCode(0x44, cs);
		// 44 23x aget vAA, vBB, vCC; vAA = vBB[vCC]
		TargetCode.AddParam("AA", dest);
		TargetCode.AddParam("BB", cs0.getReg());
		TargetCode.AddParam("CC", cs2.getReg());

		ApplyResource.recycleReg(cs0.getReg());
		ApplyResource.recycleReg(cs2.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(ArrayLength n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("int");
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		TargetCode.AddCode(0x21, cs);
		// 21 12x array-length vA, vB; vA = vB.length
		TargetCode.AddParam("A", dest);
		TargetCode.AddParam("B", cs0.getReg());

		ApplyResource.recycleReg(cs0.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(MessageSend n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		
		writeBackAllDirtyFields(argu, cs);
		
		CodeStructure cs0 = n.f0.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs0.getCodeIdx());
		
		n.f1.accept(this, argu);
		CodeStructure cs2 = n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		
		ClassTable ct = argu.globalTable.getClassTable(cs0.getType());
		MethodTable mt = ct.getMethodTable(cs2.getId());
		int paramNum = mt.getParamLength();
		
		if (paramNum < 5) {
			ApplyResource.setInvokeMode(paramNum);
		} else {
			ApplyResource.setInvokeMode(paramNum);
			if (cs0.getReg() != null && (!TypeChange.isInt(cs0.getReg()) || TypeChange.parseInt(cs0.getReg()) != ApplyResource.getNowInvokeReg())) {
				if (TargetCode.nowMethodTargetCode.codeSize() > 0
						&& !TargetCode.nowMethodTargetCode.isLastMoveOrConstToVar()
						&& ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1)).length > 0
						&& cs0.getReg().equals(TargetCode.nowMethodTargetCode.getCodeParamValue(
							TargetCode.nowMethodTargetCode.codeSize()-1, 
							ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1))[0])
						)
					) {
					TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1).replace(cs0.getReg(), ""+ApplyResource.getNowInvokeReg());
				} else {
					TargetCode.AddCode(0x07, cs);
					//07 12x move-object vA, vB; vA = vB;
					TargetCode.AddParam("A", ""+ApplyResource.getNowInvokeReg());
					TargetCode.AddParam("B", cs0.getReg());
				}
				ApplyResource.recycleReg(cs0.getReg());
				cs0.setReg(""+ApplyResource.getNowInvokeReg());
			}
		}
		ApplyResource.addNowOutReg(cs0.getReg(), cs0.getType());
		
		CodeStructure cs4 = n.f4.accept(this, argu);
		if (cs4 != null && cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs4.getCodeIdx());
		
		n.f5.accept(this, argu);
		
		if (paramNum < 5) {
			TargetCode.AddCode(0x6e, cs);
			// 6e 35c invoke-virtual {vD, vE, vF, vG, vA}, meth@CCCC
			// B: argument word count (4 bits) 
			// C: method index (16 bits) 
			// D..G, A: argument registers 
			// (4 bits each) 
			TargetCode.AddParam("B", String.valueOf(paramNum+1));
			TargetCode.AddParam("D", ""+ApplyResource.getNowOutReg(0));
			TargetCode.AddType("D", ApplyResource.getNowOutType(0));
			ApplyResource.recycleReg(""+ApplyResource.getNowOutReg(0));
			if (paramNum > 0) {
				TargetCode.AddParam("E", ""+ApplyResource.getNowOutReg(1));
				TargetCode.AddType("E", ApplyResource.getNowOutType(1));
				ApplyResource.recycleReg(""+ApplyResource.getNowOutReg(1));
			} else TargetCode.AddParam("E", String.valueOf(0));
			if (paramNum > 1) {
				TargetCode.AddParam("F", ""+ApplyResource.getNowOutReg(2));
				TargetCode.AddType("F", ApplyResource.getNowOutType(2));
				ApplyResource.recycleReg(""+ApplyResource.getNowOutReg(2));
			} else TargetCode.AddParam("F", String.valueOf(0));
			if (paramNum > 2) {
				TargetCode.AddParam("G", ""+ApplyResource.getNowOutReg(3));
				TargetCode.AddType("G", ApplyResource.getNowOutType(3));
				ApplyResource.recycleReg(""+ApplyResource.getNowOutReg(3));
			} else TargetCode.AddParam("G", String.valueOf(0));
			if (paramNum > 3) {
				TargetCode.AddParam("A", ""+ApplyResource.getNowOutReg(4));
				TargetCode.AddType("A", ApplyResource.getNowOutType(4));
				ApplyResource.recycleReg(""+ApplyResource.getNowOutReg(4));
			} else TargetCode.AddParam("A", String.valueOf(0));
			TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetMethodIdx(mt.getClassName(), mt.getMethodName()));
		} else {
			TargetCode.AddCode(0x74, cs);
			// 74 3rc invoke-virtual/range {vCCCC .. vNNNN}, meth@BBBB
			TargetCode.AddParam("AA", String.valueOf(paramNum+1));
			TargetCode.AddParam("CCCC", ""+ApplyResource.getNowOutReg(0));
			for (int i = 0; i < paramNum+1; i++) {
				ApplyResource.recycleReg(""+ApplyResource.getNowOutReg(i));
			}
			TargetCode.AddParam("BBBB", ""+argu.globalTable.dexPrinter.GetMethodIdx(mt.getClassName(), mt.getMethodName()));
		}

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		if (mt.getReturnType().getTypeName().equals("int") || mt.getReturnType().getTypeName().equals("boolean")) {
			TargetCode.AddCode(0x0a, cs);
			//0a 11x move-result vAA
			TargetCode.AddParam("AA", dest);
		} else {
			TargetCode.AddCode(0x0c, cs);
			//0c 11x move-result-object vAA
			TargetCode.AddParam("AA", dest);
		}
		
		ApplyResource.exitInvokeMode();
		
		cs.setType(mt.getReturnType().getTypeName());
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(ExpressionList n, SymbolTable argu) {
		CodeStructure cs = n.f0.accept(this, argu);
		if (cs.getType().equals("^_^boolean")) {
			cs.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			cs.setCodeIdx(TargetCode.AddCode(0x12, cs));
			// 12 11n const/4 vA, #+B
			TargetCode.AddParam("A", cs.getReg());
			TargetCode.AddParam("B", "^_^"+cs.getLiteralVal());
		} else if (cs.getType().equals("^_^int")) {
			cs.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			cs.setCodeIdx(TargetCode.AddCode(0x13, cs));
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs.getLiteralVal());
		}
		
		if (ApplyResource.isInvokeRange() && cs.getReg() != null && (!TypeChange.isInt(cs.getReg()) || (TypeChange.parseInt(cs.getReg()) != ApplyResource.getNowInvokeReg()))) {
			if (TargetCode.nowMethodTargetCode.codeSize() > 0
					&& !TargetCode.nowMethodTargetCode.isLastMoveOrConstToVar()
					&& ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1)).length > 0
					&& cs.getReg().equals(TargetCode.nowMethodTargetCode.getCodeParamValue(
						TargetCode.nowMethodTargetCode.codeSize()-1, 
						ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1))[0])
					)
				) {
				TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1).replace(cs.getReg(), ""+ApplyResource.getNowInvokeReg());
			} else {
				if (cs.getType().startsWith("^_^") || cs.getType().equals("int") || cs.getType().equals("boolean") || cs.getType().endsWith("@id")) {
					TargetCode.AddCode(0x01, cs);
					//01 12x move vA, vB; vA = vB;
				} else {
					TargetCode.AddCode(0x07, cs);
					//07 12x move-object vA, vB; vA = vB;
				}
				TargetCode.AddParam("A", ""+ApplyResource.getNowInvokeReg());
				TargetCode.AddParam("B", cs.getReg());
			}
			ApplyResource.recycleReg(cs.getReg());
			cs.setReg(""+ApplyResource.getNowInvokeReg());
		}
			
		ApplyResource.addNowOutReg(cs.getReg(), cs.getType());
		
		n.f1.accept(this, argu);
		return cs;
	}
	
	@Override
	public CodeStructure visit(ExpressionRest n, SymbolTable argu) {
		n.f0.accept(this, argu);
		CodeStructure cs = n.f1.accept(this, argu);
		if (cs.getType().equals("^_^boolean")) {
			cs.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			cs.setCodeIdx(TargetCode.AddCode(0x12, cs));
			// 12 11n const/4 vA, #+B
			TargetCode.AddParam("A", cs.getReg());
			TargetCode.AddParam("B", ""+cs.getLiteralVal());
		} else if (cs.getType().equals("^_^int")) {
			cs.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			cs.setCodeIdx(TargetCode.AddCode(0x13, cs));
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs.getLiteralVal());
		}
		
		if (ApplyResource.isInvokeRange() && cs.getReg() != null && (!TypeChange.isInt(cs.getReg()) || (TypeChange.parseInt(cs.getReg()) != ApplyResource.getNowInvokeReg()))) {
			if (TargetCode.nowMethodTargetCode.codeSize() > 0
					&& !TargetCode.nowMethodTargetCode.isLastMoveOrConstToVar()
					&& ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1)).length > 0
					&& cs.getReg().equals(TargetCode.nowMethodTargetCode.getCodeParamValue(
						TargetCode.nowMethodTargetCode.codeSize()-1, 
						ISA.getDst(TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1))[0])
					)
				) {
				TargetCode.nowMethodTargetCode.getCodeByIndex(TargetCode.nowMethodTargetCode.codeSize()-1).replace(cs.getReg(), ""+ApplyResource.getNowInvokeReg());
			} else {
				if (cs.getType().startsWith("^_^") || cs.getType().equals("int") || cs.getType().equals("boolean") || cs.getType().endsWith("@id")) {
					TargetCode.AddCode(0x01, cs);
					//01 12x move vA, vB; vA = vB;
				} else {
					TargetCode.AddCode(0x07, cs);
					//07 12x move-object vA, vB; vA = vB;
				}
				TargetCode.AddParam("A", ""+ApplyResource.getNowInvokeReg());
				TargetCode.AddParam("B", cs.getReg());
			}
			ApplyResource.recycleReg(cs.getReg());
			cs.setReg(""+ApplyResource.getNowInvokeReg());
		}
		
		ApplyResource.addNowOutReg(cs.getReg(), cs.getType());
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(PrimaryExpression n, SymbolTable argu) {
		CodeStructure cs = null;
		CodeStructure cs0 = n.f0.accept(this, argu);
		
		if (n.f0.which == 3 && cs0.getIsField() && argu.globalTable.getClassTable(argu.getClassName()).getVariableType(cs0.getId()).getState() == TypeTable.FREE) {
			argu.globalTable.getClassTable(argu.getClassName()).getVariableType(cs0.getId()).setState(TypeTable.LOADED);
			cs = new CodeStructure("express");
			cs.setType(cs0.getType());
			cs.setId(cs0.getId());
			cs.setIsField();
			cs.setReg(cs0.getReg());
			String idName =  cs0.getId();
			
			if (cs.getType().contains("int") && !cs.getType().contains("[]")) cs.setCodeIdx(TargetCode.AddCode(0x52, cs));
			else if (cs.getType().contains("boolean")) cs.setCodeIdx(TargetCode.AddCode(0x55, cs));
			else cs.setCodeIdx(TargetCode.AddCode(0x54, cs));
			// 0x52 22c iget vA, vB, field@CCCC; A = B.@CCCC
			// 0x54 22c iget-object vA, vB, field@CCCC; A = B.@CCCC
			// 0x55 22c iget-boolean vA, vB, field@CCCC; A = B.@CCCC
			
			TargetCode.AddParam("A", cs.getReg());
			TargetCode.AddParam("B", "this");
			String fieldType = cs0.getType();
			if (fieldType.startsWith("^_^")) fieldType = fieldType.substring(3, fieldType.lastIndexOf("@"));
			TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetFieldIdx(argu.getClassName(), fieldType, idName));
		} else cs = cs0;
		return cs;
	}
	
	@Override
	public CodeStructure visit(IntegerLiteral n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		
		if (Optimize.isO1()) {
			//Literal
			cs.setType("^_^int");
			cs.setLiteralVal(TypeChange.parseInt(n.f0.tokenImage));
			return cs;
		} else {
			//Expand Literal
			String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
			cs.setReg(dest);
			cs.setType("int");
			n.f0.accept(this, argu);
			
			TargetCode.AddCode(0x13, cs);
			// 13 21s const/16 vAA, #+BBBB; vAA = #+BBBB
			TargetCode.AddParam("AA", dest);
			TargetCode.AddParam("BBBB", n.f0.tokenImage); 
			
			return cs;
		}
		
	}
	
	@Override
	public CodeStructure visit(TrueLiteral n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");

		if (Optimize.isO1()) {
			//Literal
			cs.setType("^_^boolean");
			cs.setLiteralVal(1);
			return cs;
		} else {
			//Expand Literal
			String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
			cs.setReg(dest);
			cs.setType("boolean");
			n.f0.accept(this, argu);
			
			TargetCode.AddCode(0x12, cs);
			// 12 11n const/4 vA, #+B; vA = #+B
			TargetCode.AddParam("A", dest);
			TargetCode.AddParam("B", "1");
			
			return cs;
		}
	}
	
	@Override
	public CodeStructure visit(FalseLiteral n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		
		if (Optimize.isO1()) {
			//Literal
			cs.setType("^_^boolean");
			cs.setLiteralVal(0);
			return cs;
		} else {
			//Expand Literal
			String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
			cs.setReg(dest);
			cs.setType("boolean");n.f0.accept(this, argu);
			
			TargetCode.AddCode(0x12, cs);
			// 12 11n const/4 vA, #+B; vA = #+B
			TargetCode.AddParam("A", dest);
			TargetCode.AddParam("B", "0");
			
			return cs;
		}
	}
	
	@Override
	public CodeStructure visit(Identifier n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("identify");
		cs.setId(n.f0.tokenImage);
		
		if (argu.globalTable.getClassTable(n.f0.tokenImage) != null) {
			cs.setType(n.f0.tokenImage);
		} else {
			ClassTable ct = argu.globalTable.getClassTable(argu.getClassName());
			if (ct.getMethodTable(n.f0.tokenImage) != null) {
				cs.setType(".Method");
			} else {
				MethodTable mt = null;
				if (argu.getMethodName() != null) mt = ct.getMethodTable(argu.getMethodName());
				TypeTable tt = null;
				if (mt != null) tt = mt.getVariableType(n.f0.tokenImage);
				if (tt == null) {
					tt = ct.getVariableType(n.f0.tokenImage);
					if (tt != null) cs.setIsField();
				}
				if (tt != null) {
					cs.setReg(tt.getReg());
					cs.setType(tt.getTypeName());
					if (Optimize.isO1() && !ApplyResource.getWhileMode() && tt.getValue() != null && (cs.getType() == "int" || cs.getType() == "boolean")) {
						cs.setType("^_^"+cs.getType()+"@id");
						cs.setLiteralVal(TypeChange.parseInt(tt.getValue()));
					}
				} else cs.setType(".UnkownType");
			}
		}
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(ThisExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType(argu.getClassName());
		cs.setReg("this");
		
		return cs;
	}
	
	@Override
	public CodeStructure visit(ArrayAllocationExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		cs.setType("int[]");
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		CodeStructure cs3 = n.f3.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs3.getCodeIdx());
		n.f4.accept(this, argu);
		
		if (cs3.getType().equals("^_^int")) {
			cs3.setReg(""+ApplyResource.getReg(ApplyResource.TEMP));
			cs.setCodeIdx(TargetCode.AddCode(0x13, cs));
			// 13 21s const/16 vAA, #+BBBB
			TargetCode.AddParam("AA", cs3.getReg());
			TargetCode.AddParam("BBBB", "^_^"+cs3.getLiteralVal());
		}

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		TargetCode.AddCode(0x23, cs);
		// 23 22c new-array vA, vB, type@CCCC
		TargetCode.AddParam("A", dest);
		TargetCode.AddParam("B", cs3.getReg());
		TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetTypeIdx("int[]"));

		ApplyResource.recycleReg(cs3.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(AllocationExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		
		n.f0.accept(this, argu);
		CodeStructure csId = n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		cs.setCodeIdx(TargetCode.AddCode(0x22, cs));
		// 22 21c new-instance vAA, type@BBBB
		TargetCode.AddParam("AA", dest);
		TargetCode.AddParam("BBBB", ""+argu.globalTable.dexPrinter.GetTypeIdx(n.f1.f0.tokenImage));
		
		TargetCode.AddCode(0x70, cs);
		// 70 35c invoke-direct {vD, vE, vF, vG, vA}, meth@CCCC
		// B: argument word count (4 bits) 
		// C: method index (16 bits) 
		// D..G, A: argument registers 
		// (4 bits each) 
		TargetCode.AddParam("B", String.valueOf(1));
		TargetCode.AddParam("D", dest);
		TargetCode.AddParam("E", String.valueOf(0));
		TargetCode.AddParam("F", String.valueOf(0));
		TargetCode.AddParam("G", String.valueOf(0));
		TargetCode.AddParam("A", String.valueOf(0));
		TargetCode.AddParam("CCCC", ""+argu.globalTable.dexPrinter.GetMethodIdx(n.f1.f0.tokenImage, "<init>"));
		
		cs.setType(csId.getType());

		return cs;
	}
	
	@Override
	public CodeStructure visit(NotExpression n, SymbolTable argu) {
		CodeStructure cs = new CodeStructure("express");
		
		n.f0.accept(this, argu);
		CodeStructure cs1 = n.f1.accept(this, argu);
		if (cs.getCodeIdx() == 0xffff) cs.setCodeIdx(cs1.getCodeIdx());
		
		if ((!ApplyResource.getWhileMode() && cs1.getType().startsWith("^_^boolean")) || cs1.getType().equals("^_^boolean")) {
			cs.setType("^_^boolean");
			cs.setLiteralVal(1-cs1.getLiteralVal());
			return cs;
		}

		String dest = ""+ApplyResource.getReg(ApplyResource.TEMP);
		cs.setReg(dest);
		cs.setType("boolean");
		TargetCode.AddCode(0xd9, cs);
		// d9 22b rsub-int/lit8 vAA, vBB, #+CC; vAA = CC - vBB
		TargetCode.AddParam("AA", dest);
		TargetCode.AddParam("BB", cs1.getReg());
		TargetCode.AddParam("CC", "^_^1");

		ApplyResource.recycleReg(cs1.getReg());
		return cs;
	}
	
	@Override
	public CodeStructure visit(BracketExpression n, SymbolTable argu) {
		CodeStructure cs = n.f1.accept(this, argu);
		return cs;
	}
}
