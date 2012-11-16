package minijava.dexstruct.items;

/**
 * MethodItem的数据结构
 */
public class MethodItem
{
	public short classIdx;	//to type table  the class it belongs to
	public short protoIdx;
	public int nameIdx;
	public String className;
	public String protoShorty;
	public String methodName;
	public String modifiedShorty;
	public String modifiedMethod;
	//String protoReturnTypeString;
	//int paramNum;
	//String[] paramStrings;
	/**
	 * 构造函数 需要传入信息
	 * @param _className 所属类名
	 * @param _methodName 方法名
	 * @param _protoShorty proto的shorty字段名
	 * @param _modifiedShorty 经过编码的shorty字符名
	 * @param _modifiedMethod 经过编码的方法信息
	 */
	public MethodItem(String _className, String _methodName, String _protoShorty, String _modifiedShorty, String _modifiedMethod)
	{
		className = _className;
		methodName = _methodName;
		protoShorty = _protoShorty;
		modifiedShorty = _modifiedShorty;
		modifiedMethod = _modifiedMethod;
	}
}