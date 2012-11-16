package util;

/**
 * 关于优化等级的全局工具类
 */
public class Optimize {
	protected static int O = 0;
	public static void setLevel(int i) {
		O = i;
		if (i < -1) O = -1;
		if (i > 3) O = 3;
	}
	public static boolean isO3() {
		return O >= 3;
	}
	public static boolean isO2() {
		return O >= 2;
	}
	public static boolean isO1() {
		return O >= 1;
	}
	public static boolean isO0() {
		return O >= 0;
	}
}
