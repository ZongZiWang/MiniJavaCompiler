package minijava.dexstruct;

import java.util.*;

/**
 * TypeTable段的数据存储结构
 */
public class DexTypeTable {
	private int size;
	private HashSet<String> hTypeStrings;
	private ArrayList<Integer> aTypeStringIdxs;
	private HashMap<String, Integer> hTypeStringToStringIdx;
	private HashMap<Integer, String> hStringIdxToTypeString;
	
	private DexStringTable stringTable;
	
	/**
	 * 构造函数，需要传入table信息
	 * @param _StringTable
	 */
	public DexTypeTable(DexStringTable _StringTable) 
	{
		stringTable = _StringTable;
		hTypeStrings = new HashSet<String>();
		aTypeStringIdxs = new ArrayList<Integer>();
		hTypeStringToStringIdx = new HashMap<String, Integer>();
		hStringIdxToTypeString = new HashMap<Integer, String>();
	}

	/**
	 * 添加一个类型
	 * @param _str 类型名字符串
	 */
	public void AddType(String _str)
	{
		hTypeStrings.add(_str);
		stringTable.AddString(_str);
	}
	
	/**
	 * 获取项目数
	 * @return 项目数
	 */
	public int GetSize()
	{
		return size;
	}
	
	/**
	 * 要先调用了stringTable的serilize()后才能调用此函数
	 */
	public void Serilize()
	{
		Iterator<String> it = hTypeStrings.iterator();
		for (int i = 0; i < hTypeStrings.size(); i++) 
		{
			String tmpString = new String(it.next());
			hTypeStringToStringIdx.put(tmpString, stringTable.GetStringIdx(tmpString));
			hStringIdxToTypeString.put(stringTable.GetStringIdx(tmpString), tmpString);
			aTypeStringIdxs.add(stringTable.GetStringIdx(tmpString));
		}
		
		for (int i = 0; i < aTypeStringIdxs.size()-1; i++) 
		{
			for (int j = i; j < aTypeStringIdxs.size(); j++) 
			{
				if (aTypeStringIdxs.get(i) > (aTypeStringIdxs.get(j))) 
				{
					Integer tmpInteger = aTypeStringIdxs.get(i);
					aTypeStringIdxs.set(i, aTypeStringIdxs.get(j));
					aTypeStringIdxs.set(j, tmpInteger);
				}
			}
		}
		size = aTypeStringIdxs.size();
	}
	
	/**
	 * 获得type表中第idx的在stringtable中的索引值
	 * @param idx
	 * @return
	 */
	public int GetTypeStringIdx(int idx)
	{
		return aTypeStringIdxs.get(idx);
	}
	
	/**
	 * 获得type表中第idx的在stringtable中的String
	 * @param idx
	 * @return
	 */
	public String GetTypeString(int idx)
	{
		return stringTable.GetString(aTypeStringIdxs.get(idx));
	}
	
	/**
	 * 获得名称为str的type在typetable中的索引值
	 * @param str
	 * @return
	 */
	public int GetTypeIdx(String str)
	{
		Integer stridx = hTypeStringToStringIdx.get(str);
		return aTypeStringIdxs.indexOf(stridx);
	}
}
