package minijava.dexstruct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import minijava.dexstruct.items.StringItem;

/**
 * StringTable段的数据存储结构
 */
public class DexStringTable {
	private int size;
	
	private HashSet<String> strings;
	private ArrayList<String> aStrings;
	
	private ArrayList<StringItem> stringItemsArrayList;
	
	/**
	 * 构造函数
	 */
	public DexStringTable()
	{
		strings = new HashSet<String>();
	}
	
	/**
	 * 添加一个字符串
	 * @param _str 添加的字符串
	 */
	public void AddString(String _str)
	{
		strings.add(_str);
	}
	
	/**
	 * 排序，不需要任何准备即可做
	 */
	public void Serilize()
	{
		stringItemsArrayList = new ArrayList<StringItem>();
		
		aStrings = new ArrayList<String>();
		Iterator<String> it = strings.iterator();
		for (int i = 0; i < strings.size(); i++) 
		{
			aStrings.add(new String(it.next()));
		}
		
		for (int i = 0; i < aStrings.size()-1; i++) 
		{
			for (int j = i; j < aStrings.size(); j++) 
			{
				if (aStrings.get(i).compareTo(aStrings.get(j)) > 0) 
				{
					String tmpString = aStrings.get(i);
					aStrings.set(i, aStrings.get(j));
					aStrings.set(j, tmpString);
				}
			}
		}
		
		//sort by ABC
		for (int i = 0; i < aStrings.size(); i++) 
		{
			stringItemsArrayList.add(new StringItem(aStrings.get(i)));		
		}
		size = aStrings.size();
	}
	
	/**
	 * 通过字符串获取对应的index（StringTable中）
	 * @param str 字符串
	 * @return 对应的index（StringTable中）
	 */ 
	public int GetStringIdx(String str)
	{
		if (strings.contains(str)) 
			return aStrings.indexOf(str);
		return -1;
	}
	
	/**
	 * 通过字符串的index获取对应的字符串
	 * @param idx 字符串的index
	 * @return 对应的字符串
	 */
	public String GetString(int idx)
	{
		if (idx < size)
			return aStrings.get(idx);
		return null;
	}
	
	/**
	 * 获取项目数量
	 * @return 项目数量
	 */
	public int GetSize()
	{
		return size;
	}
	
	/**
	 * 设置某一项目的回填offset
	 * @param idx 项目index
	 * @param off 回填offset
	 */
	public void SetBackPatchOff(int idx, int off)
	{
		stringItemsArrayList.get(idx).backPatchOff = off;
	}
	
	/**
	 * 获取某一项目的回填offset
	 * @param idx 项目index
	 * @return 回填offset
	 */
	public int GetBackPatchOff(int idx)
	{
		return stringItemsArrayList.get(idx).backPatchOff;
	}
}