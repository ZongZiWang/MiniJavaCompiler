package minijava.typecheck;

import minijava.typecheck.symboltable.GlobalTable;

/**
 * First check
 * @author ZongZiWang
 *
 */
public class FirstCheck {
	/**
	 * check inheritance loop, undefined class, override error
	 * @param globalTable
	 * @return true => error
	 */
	public boolean firstCheckError(GlobalTable globalTable) {
		boolean flag = false ;
		if (globalTable == null) return true;
		if (globalTable.checkInheritanceLoop()) {
			return true;
		}
		if (globalTable.checkUndefinedClass()) {
			flag = true;
		}
		if (globalTable.checkOverrideError()) {
			flag = true;
		}
		return flag;
	}
}
