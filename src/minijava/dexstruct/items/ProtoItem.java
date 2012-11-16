package minijava.dexstruct.items;

/**
 * ProtoItem的数据结构
 */
public class ProtoItem{
	public int shortyIdx;   //to string table
	public int returnTypeIdx;  // to type table
	public int parametersOffBackPatchOffset;

	public int paramNum;
	public String shorty;
	public String returnType;
	public String[] paramStrings;
	public String modifiedShorty;
	public TypeList typeList;
	/**
	 * 构造函数，需要传入信息
	 * @param _modifiedShorty 经过编码的shorty字符信息
	 * @param _shorty shorty字符名
	 * @param _returnType 返回类型名
	 * @param _paramNum 参数个数
	 * @param _paramStrings 参数类型名列表
	 */
	public ProtoItem(String _modifiedShorty, String _shorty, String _returnType, int _paramNum, String[] _paramStrings)
	{
		modifiedShorty = _modifiedShorty;
		shorty = _shorty;
		returnType = _returnType;
		paramNum = _paramNum;
		if (paramNum != 0) 
		{
			typeList = new TypeList();
			typeList.size = paramNum;
			typeList.typeidx = new short[paramNum];
			paramStrings = _paramStrings;
		}
	}
}
