package minijava.typecheck;

import minijava.typecheck.symboltable.GlobalTable;

/**
 * check unused error
 * @author ZongZiWang
 *
 */
public class UnusedCheck {
	/**
	 * check unused error
	 * @param globalTable
	 */
	public void unusedCheck(GlobalTable globalTable) {
		if (globalTable == null) return ;
		globalTable.checkUnusedVariables();
	}
}
