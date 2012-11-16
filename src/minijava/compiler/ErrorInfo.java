package minijava.compiler;

/**
 * record error information
 * @author ZongZiWang
 *
 */
public class ErrorInfo{
	static String info = "";
	public static void init() {
		info = "";
	}
	public static String getInfo() {
		return info;
	}
	/**
	 * just like System.err.print();
	 * @param x
	 * @return
	 */
	public static Boolean addInfo(String x) {
		info = info + x;
		return true;
	}
	/**
	 * just like System.err.println();
	 * @param x
	 * @return
	 */
	public static Boolean addlnInfo(String x) {
		info = info + x + '\n';
		return true;
	}
}