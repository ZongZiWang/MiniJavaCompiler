package minijava.binary;

/**
 * Model for expressions like Z := X op Y.
 * @author Xie Jiaye
 */
public class DualExp {
	/** 
	 * Key to first operating variable in corresponding 
	 * {@link minijava.intermediate.Code Code}.
	 */
	public String X;
	/**
	 * Operation code for corresponding {@link minijava.intermediate.Code Code}.
	 */
	public int op;
	/** 
	 * Key to second operating variable in corresponding 
	 * {@link minijava.intermediate.Code Code}.
	 */
	public String Y;
	/**
	 * Key to destination variable in corresponding 
	 * {@link minijava.intermediate.Code Code}.
	 */
	public String Z;
	public DualExp(String _X, int _op, String _Y, String _Z) {
		X = _X;
		op = _op;
		Y = _Y;
		Z = _Z;
	}
}
