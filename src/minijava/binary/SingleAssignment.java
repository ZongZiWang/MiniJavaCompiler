package minijava.binary;

/**
 * Model for assignments like X := Y.
 * @author Xie Jiaye
 */
public class SingleAssignment {
	
	/** 
	 * Key to destination variable in corresponding 
	 * {@link minijava.intermediate.Code Code}.
	 */
	public String X;
	/** 
	 * Key to source variable in corresponding 
	 * {@link minijava.intermediate.Code Code}.
	 */
	public String Y;
	
	SingleAssignment(String _X, String _Y) {
		X = _X;
		Y = _Y;
	}
}