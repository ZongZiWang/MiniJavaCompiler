package minijava.dexstruct.items;

/**
 * ClassDefItem的数据结构
 */
public class ClassDefItem
{	
	public int classIdx;		//typetable
	public int accessFlag;
	public int superClassIdx;	//typetable
	public int interfaceOff = 0;
	public int sourceFileIdx = 0;
	public int annotationOff = 0;
	public int classDataOff = 0;
	public int staticValueOff = 0;
	public ClassDataItem class_data_item;
	
	public String classNameString;
	public String superClassNameString;
	public String sourceFileNameString;
	
	/**
	 * 构造函数，需要传入信息来进行生成
	 * @param _srcFile 所属源文件名
	 * @param _className 类名
	 * @param _superClassName 父类名
	 * @param _accessFlag 访问权限
	 */
	public ClassDefItem(String _srcFile, String _className, String _superClassName, int _accessFlag)
	{
		classNameString = new String(_className);
		superClassNameString = new String(_superClassName);
		sourceFileNameString = new String(_srcFile);
		class_data_item = new ClassDataItem();
		accessFlag = _accessFlag;
	}
	
	/**
	 * 设置回填offset信息
	 * @param _backPatchOff 回填offset
	 */
	public void SetClassDataOffBackPatchOffset(int _backPatchOff)
	{
		class_data_item.classDataOffBackPatchOffset = _backPatchOff;
	}
	
	/**
	 * 获取回填offset信息
	 * @return 回填offset
	 */
	public int GetClassDataOffBackPatchOffset()
	{
		return class_data_item.classDataOffBackPatchOffset;
	}
}