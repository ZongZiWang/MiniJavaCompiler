package minijava.compiler;

/**
 * record warning information
 * @author ZongZiWang
 *
 */
public class WarningInfo {
	static String info = "";
	public static void init() {
		info = "";
	}
	public static String getInfo() {
		return info;
	}
	/**
	 * just like System.out.print();
	 * @param x
	 * @return
	 */
	public static Boolean addInfo(String x) {
		info = info + x;
		return true;
	}
	/**
	 * just like System.out.println();
	 * @param x
	 * @return
	 */
	public static Boolean addlnInfo(String x) {
		info = info + x + '\n';
		return true;
	}
}