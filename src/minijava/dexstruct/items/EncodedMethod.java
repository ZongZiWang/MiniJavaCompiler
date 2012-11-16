package minijava.dexstruct.items;

/**
 * EncodedMethod的数据结构
 */
public class EncodedMethod
{
	public int methodIdx;
	public int accessFlag;
	public int codeOff; //之后填
	public CodeItem dexCode;
	
	public String modifiedMethod; //用来反查 methodIdx
	
	/**
	 * 构造函数
	 * @param _className 所属类名
	 * @param _methodName 方法名
	 * @param _accessFlag 访问权限
	 */
	public EncodedMethod(String _className, String _methodName, int _accessFlag)//, short _registersSize, short _insSize, short _outsSize, int _insnsSize, byte[] _code
	{
		modifiedMethod = new String(_className + _methodName);
		accessFlag = _accessFlag;
	}
}