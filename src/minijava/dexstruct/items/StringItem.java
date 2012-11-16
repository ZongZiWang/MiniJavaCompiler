package minijava.dexstruct.items;

/**
 * StringItem的数据结构
 */
public class StringItem {
	public String str;
	public int backPatchOff;
	/**
	 * 构造函数，需要传入信息
	 * @param _str 字符串名
	 */
	public StringItem(String _str)
	{
		str = _str;
	}
}
