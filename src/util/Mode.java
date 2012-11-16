package util;

/**
 * 设置模式的全局工具类
 */
public class Mode {
	protected static boolean isOutputApk = true;
	protected static boolean isDebugMode = false;
	public static void SetOutputApkMode()
	{
		isOutputApk = true;
	}
	public static void SetOutputDexMode()
	{
		isOutputApk = false;
	}
	public static boolean IsOutputApk()
	{
		return isOutputApk;
	}
	public static void SetDebugMode()
	{
		isDebugMode = true;
	}
	public static boolean isDebugMode()
	{
		return isDebugMode;
	}
}
