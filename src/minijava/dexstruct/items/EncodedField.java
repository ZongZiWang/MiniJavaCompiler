package minijava.dexstruct.items;

/**
 * EncodedField的数据结构
 */
public class EncodedField
{
	public int fieldIdx;
	public int accessFlag;
	
	public String modifiedField; //用来反查 fieldIdx
	
	/**
	 * 构造函数
	 * @param _class 所属类名
	 * @param _type 类型名
	 * @param _fieldname 属性名
	 * @param _accessFlag 访问权限
	 */
	public EncodedField(String _class, String _type, String _fieldname, int _accessFlag)
	{
		modifiedField = new String(_class + _type + _fieldname);
		accessFlag = _accessFlag;
	}
}