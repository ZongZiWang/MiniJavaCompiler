package util;
/**
 * Utility for type check or change
 * @author ZongZiWang
 *
 */
public class TypeChange {
	/**
	 * convert integer 'src' to hex string of length 'len'
	 * @param src
	 * @param len
	 * @return hex string of length 'len'
	 */
	public static String IntToString(int src, int len)
	{
		String str = Integer.toHexString(src);
		int nowLen = str.length();
		for (int i = 0; i < len-nowLen; i++) {
			str = "0".concat(str);
		}
		str = str.substring(str.length()-len, str.length());
		return str;
	}
	/**
	 * convert integer 'src' to little endian hex string of length 'len'
	 * @param src
	 * @param len
	 * @return little endian hex string of length 'len'
	 */
	public static String IntToStringLittleEndian(int src, int len) {
		String BigEndianStr = IntToString(src, len);
		String str = "";
		for (int i = 0; i+1 < BigEndianStr.length(); i += 2) {
			str = BigEndianStr.substring(i, i+2).concat(str);
		}
		return str;
	}
	/**
	 * convert decimal string 'str' to integer
	 * @param str
	 * @return decimal number
	 */
	public static int parseInt(String str) {
		int i;
		for (i = 0; i < str.length(); i++) {
			if (i == 0 && str.charAt(0) == '-') continue;
			if (str.charAt(i) < '0' || str.charAt(i) > '9') break; 
		}
		if (i == str.length()) return Integer.parseInt(str);
		else {
			if (str.startsWith("^_^")) return Integer.parseInt(str.substring(3));
			else if (str.startsWith(">_<")) return Integer.parseInt(str.substring(3));
			else return 0xffff;
		}
	}
	/**
	 * check if 'str' is decimal number string
	 * @param str
	 * @return true => 'str' is decimal number <br />
	 * false => 'str' is not decimal number
	 */
	public static boolean isInt(String str) {
		int i = 0;
		if (str.startsWith("^_^")) i = 3;
		for (; i < str.length(); i++) {
			if (i == 0 && str.charAt(0) == '-') continue;
			if (str.startsWith("^_^") && i == 3 && str.charAt(3) == '-') continue;
			if (str.charAt(i) < '0' || str.charAt(i) > '9') return false; 
		}
		return true;
	}
	/**
	 * convert normal type '_type' to dex file type
	 * @param _type
	 * @return dex file type
	 */
	public static String toDexType(String _type)
	{
		String retString;
		if (_type.equals("int")) return "I";
		if (_type.equals("boolean")) return "Z";
		if (_type.equals("void")) return "V";
		if (_type.equals("int[]")) return "[I";
		if (_type.equals("String[]")) return "[Ljava/lang/String;";
 		if (_type.length() > 1)
		{
			if (_type.startsWith("[")) 
			{
				if (_type.length() > 2) 
				{
					retString = new String("[L" + _type.substring(1) + ";" );
					return retString;
				}
				retString = _type;
				return retString;
			}
			retString = new String("L" + _type + ";");
			return retString;
		}
		retString = _type;
		return retString;
	}
	/**
	 * convert string to byte
	 * @param byteString whose length must be 2
	 * @return byte of string
	 */
	public static byte StringToBytes(String byteString) {
		if (byteString.length() != 2) {
			System.err.println("The String to Bytes is formattedly Error!");
			System.exit(0);
		}
		Character first = byteString.charAt(0), second = byteString.charAt(1);
		byte ans = (byte) (Character.digit(first, 16)*16+Character.digit(second, 16));
		return ans;
	}
	
}
