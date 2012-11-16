package minijava.dexstruct.items;

/**
 * FieldItem的数据结构
 */
public class FieldItem
{
	public short classIdx;	//to type table
	public short typeIdx;  //to type table
	public int nameIdx;    //to string table
	public String nameString;
	public String classString;
	public String typeString;
	public String modifiedFieldString;
	/**
	 * 构造函数，需要传入信息
	 * @param _class 所属类名
	 * @param _type 类型名
	 * @param _name 属性名
	 * @param _modified 经过编码的属性信息
	 */
	public FieldItem(String _class, String _type, String _name, String _modified)
	{
		classString = _class;
		typeString = _type;
		nameString = _name;
		modifiedFieldString = _modified;
	}
}