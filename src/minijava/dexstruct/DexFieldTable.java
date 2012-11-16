package minijava.dexstruct;

import java.util.*;

import minijava.dexstruct.items.FieldItem;

/**
 * FieldTable段的数据存储结构
 */
public class DexFieldTable {
	private int size;
	private ArrayList<FieldItem> aFieldItems;
	private HashSet<String> hModifiedFieldItems;
	private HashMap<String, Integer> hModifiedFieldToFieldIdx;
	private HashMap<Integer, String> hFieldIdxToModifiedField;
	private DexTypeTable typeTable;
	private DexStringTable stringTable;

	/**
	 * 构造函数，需要传入相关table的引用，这些信息会被使用到
	 * @param _typeTable
	 * @param _stringTable
	 */
	public DexFieldTable(DexTypeTable _typeTable, DexStringTable _stringTable) 
	{
		typeTable = _typeTable;
		stringTable = _stringTable;
		aFieldItems = new ArrayList<FieldItem>();
		hModifiedFieldItems = new HashSet<String>();
		hModifiedFieldToFieldIdx = new HashMap<String, Integer>();
		hFieldIdxToModifiedField = new HashMap<Integer, String>();
	}
	
	/**
	 * 添加一个成员属性
	 * @param _class 类的名字
	 * @param _type 类型的名字
	 * @param _name 变量的名字
	 */
	public void AddField(String _class, String _type, String _name)
	{
		String modFieldString = new String(_class + _type + _name);
		if (hModifiedFieldItems.add(modFieldString)) 
		{
			typeTable.AddType(_class);
			typeTable.AddType(_type);
			stringTable.AddString(_name);
			aFieldItems.add(new FieldItem(_class, _type, _name, modFieldString));
		}
	}

	/**
	 * 必须要在 StringTable 和 TypeTable 都做完 Serilize() 后才能做
	 */
	public void Serilize()
	{
		for (int i = 0; i < aFieldItems.size(); i++) 
		{
			aFieldItems.get(i).classIdx = (short)typeTable.GetTypeIdx(aFieldItems.get(i).classString);
			aFieldItems.get(i).typeIdx = (short)typeTable.GetTypeIdx(aFieldItems.get(i).typeString);
			aFieldItems.get(i).nameIdx = stringTable.GetStringIdx(aFieldItems.get(i).nameString);
		}
		
		for (int i = 0; i < aFieldItems.size()-1; i++) 
		{
			for (int j = i; j < aFieldItems.size(); j++) 
			{
				if (aFieldItems.get(i).classIdx > aFieldItems.get(j).classIdx) 
				{
					FieldItem tmpFieldItem = aFieldItems.get(i);
					aFieldItems.set(i, aFieldItems.get(j));
					aFieldItems.set(j, tmpFieldItem);					
				}
				else if (aFieldItems.get(i).classIdx == aFieldItems.get(j).classIdx) 
				{
					if (aFieldItems.get(i).nameIdx > aFieldItems.get(j).nameIdx) 
					{
						FieldItem tmpFieldItem = aFieldItems.get(i);
						aFieldItems.set(i, aFieldItems.get(j));
						aFieldItems.set(j, tmpFieldItem);					
					}
					else if (aFieldItems.get(i).nameIdx == aFieldItems.get(j).nameIdx)
					{					
						if (aFieldItems.get(i).typeIdx > aFieldItems.get(j).typeIdx) 
						{
							FieldItem tmpFieldItem = aFieldItems.get(i);
							aFieldItems.set(i, aFieldItems.get(j));
							aFieldItems.set(j, tmpFieldItem);					
						}
					}
				}
			}
		}
		
		for (int i = 0; i < aFieldItems.size(); i++) 
		{
			hModifiedFieldToFieldIdx.put(aFieldItems.get(i).modifiedFieldString, i);
			hFieldIdxToModifiedField.put(i, aFieldItems.get(i).modifiedFieldString);
		}
		
		size = aFieldItems.size();
	}
	
	/**
	 * 获取项目数
	 * @return 段中的项目数
	 */
	public int GetSize()
	{
		return size;
	}
	
	/**
	 * 通过成员属性的信息来获取对应的index
	 * @param _class 所属类名称
	 * @param _type 成员属性的类型名称
	 * @param _name 成员属性的名称
	 * @return 返回对应的index
	 */
	public int GetFieldIdxByClassAndTypeAndName(String _class, String _type, String _name)
	{
		String modFieldString = new String(_class + _type + _name);
		return GetFieldIdxByModifiedField(modFieldString);
	}
	
	/**
	 * 通过编码了的成员属性信息获取对应的index
	 * @param _modfield 经过编码的成员属性信息
	 * @return 返回对应的index
	 */
	public int GetFieldIdxByModifiedField(String _modfield)
	{
		return hModifiedFieldToFieldIdx.get(_modfield);
	}
	
	/**
	 * 通过field中的index获取对应成员属性对应的类的index（TypeTable中的）
	 * @param _fieldidx field中的index
	 * @return 对应的类的index（TypeTable中的）
	 */
	public short GetFieldClassIdx(int _fieldidx)
	{
		return aFieldItems.get(_fieldidx).classIdx;
	}
	
	/**
	 * 通过field中的index获取对应成员属性的类别的index（TypeTable中的）
	 * @param _fieldidx field中的index
	 * @return 对应成员属性的类别的index（TypeTable中的）
	 */
	public short GetFieldTypeIdx(int _fieldidx)
	{
		return aFieldItems.get(_fieldidx).typeIdx;
	}
	
	/**
	 * 通过field中的index获取对应成员属性的名称的index（StringTable中的）
	 * @param _fieldidx field中的index
	 * @return 对应成员属性的名称的index（StringTable中的）
	 */
	public int GetFieldNameIdx(int _fieldidx)
	{
		return aFieldItems.get(_fieldidx).nameIdx;
	}
}
