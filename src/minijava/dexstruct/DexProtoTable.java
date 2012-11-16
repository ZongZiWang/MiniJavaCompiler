package minijava.dexstruct;

import java.util.*;

import minijava.dexstruct.items.ProtoItem;

/**
 * ProtoTable段的数据存储结构
 */
public class DexProtoTable {
	private int size;
	private DexTypeTable typeTable;
	private DexStringTable stringTable;
	
	private HashSet<String> hModifiedProtoShortySet;
	private ArrayList<ProtoItem> aProtoItems;
	private HashMap<String, Integer> hModifiedShortyToProtoIdx;
	private HashMap<Integer, String> hProtoIdxToModifiedShorty;
	
	/**
	 * 构造函数，需要传入相关table的信息
	 * @param _typeTable
	 * @param _stringTable
	 */
	public DexProtoTable(DexTypeTable _typeTable, DexStringTable _stringTable) 
	{
		typeTable = _typeTable;
		stringTable = _stringTable;
		hModifiedProtoShortySet = new HashSet<String>();
		aProtoItems = new ArrayList<ProtoItem>();
		hProtoIdxToModifiedShorty = new HashMap<Integer, String>();
		hModifiedShortyToProtoIdx = new HashMap<String, Integer>();
	}
	
	/**
	 * 添加一个函数原型
	 * @param shorty 函数原型的短描述符，报告中有关于这个如何生成的解释
	 * @param rtype 函数原型的返回值类型名称
	 * @param _paramnum 函数原型的参数个数
	 * @param params 函数原型的参数类型名列表，这里传进来的字符串数组的引用，我直接使用的引用而不是复制内容，故之后外面修改会对里面造成影响
	 */
	public void AddProto(String shorty, String rtype, int _paramnum, String[] params)
	{
		String modShorty = new String(shorty);
		modShorty = modShorty + rtype;
		for (int i = 0; i < _paramnum; i++) 
		{
			modShorty = modShorty + params[i];
		}
		if(hModifiedProtoShortySet.add(new String(modShorty)))
		{
			stringTable.AddString(shorty);
			typeTable.AddType(rtype);
			aProtoItems.add(new ProtoItem(modShorty, shorty, rtype, _paramnum, params));
			if (_paramnum != 0) 
			{
				for (int i = 0; i < _paramnum; i++) 
				{
					typeTable.AddType(params[i]);
				}
			}
		}
	}
	
	/**
	 * 必须要在 StringTable 和 TypeTable 都做完 Serilize() 后才能做
	 */
	public void Serilize()
	{
		for (int i = 0; i < aProtoItems.size(); i++) 
		{
			aProtoItems.get(i).shortyIdx = stringTable.GetStringIdx(aProtoItems.get(i).shorty);
			aProtoItems.get(i).returnTypeIdx = typeTable.GetTypeIdx(aProtoItems.get(i).returnType);
			if (aProtoItems.get(i).paramNum != 0) 
			{
				for (int j = 0; j < aProtoItems.get(i).paramNum; j++) 
				{
					aProtoItems.get(i).typeList.typeidx[j] = (short)typeTable.GetTypeIdx(aProtoItems.get(i).paramStrings[j]); 
				}
			}
		}
		
		for (int i = 0; i < aProtoItems.size()-1; i++) 
		{
			for (int j = i+1; j < aProtoItems.size(); j++) 
			{
				if (aProtoItems.get(i).returnTypeIdx > aProtoItems.get(j).returnTypeIdx) 
				{
					ProtoItem tmpItem = aProtoItems.get(i);
					aProtoItems.set(i, aProtoItems.get(j));
					aProtoItems.set(j, tmpItem);
				}
				else if (aProtoItems.get(i).returnTypeIdx == aProtoItems.get(j).returnTypeIdx)
				{
					int parami = 0;
					while (true)
					{
						if (aProtoItems.get(i).paramNum > parami && aProtoItems.get(j).paramNum <= parami) 
						{
							//System.out.println(aProtoItems.get(i).shorty + ": j have less params:" + aProtoItems.get(j).shorty + parami);
							ProtoItem tmpItem = aProtoItems.get(i);
							aProtoItems.set(i, aProtoItems.get(j));
							aProtoItems.set(j, tmpItem);
							break;
						}
						else if (aProtoItems.get(i).paramNum <= parami && aProtoItems.get(j).paramNum > parami) 
						{
							//System.out.println(aProtoItems.get(i).shorty + ": i have less patams" + aProtoItems.get(j).shorty);
							break;
						}
						else if (aProtoItems.get(i).paramNum > parami && aProtoItems.get(j).paramNum > parami) 
						{
							int itypeidx = typeTable.GetTypeIdx(aProtoItems.get(i).paramStrings[parami]);
							int jtypeidx = typeTable.GetTypeIdx(aProtoItems.get(j).paramStrings[parami]);
							//if (typeTable.GetTypeIdx(aProtoItems.get(i).paramStrings[parami]) > typeTable.GetTypeIdx(aProtoItems.get(j).paramStrings[parami])) 
							if (itypeidx > jtypeidx)
							{
								//System.out.println(aProtoItems.get(i).paramStrings[parami] + " idx bigger than "
										//+ aProtoItems.get(j).paramStrings[parami] );
								ProtoItem tmpItem = aProtoItems.get(i);
								aProtoItems.set(i, aProtoItems.get(j));
								aProtoItems.set(j, tmpItem);
								break;
							}
							else if (itypeidx < jtypeidx)
							{
								break;
							}
						}
						else 
						{
							break;
						}
						parami++;
					}
				}
			}
		}
		
		for (int i = 0; i < aProtoItems.size(); i++) 
		{
			hModifiedShortyToProtoIdx.put(aProtoItems.get(i).modifiedShorty, i);
			hProtoIdxToModifiedShorty.put(i, aProtoItems.get(i).modifiedShorty);
		}
		
		size = aProtoItems.size();
	}
	
	/**
	 * 设置一个回填位置，在组织dex结构时会用到
	 * @param idx 需要回填的表项的index
	 * @param off 需要回填的dex文件中的位置
	 */
	public void SetBackPatchOff(int idx, int off)
	{
		if (aProtoItems.get(idx).paramNum != 0) 
		{
			aProtoItems.get(idx).parametersOffBackPatchOffset = off;
		}
		else 
		{
			aProtoItems.get(idx).parametersOffBackPatchOffset = 0;
		}
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
	 * 通过原型的信息获取原型的index
	 * @param shorty 函数原型的shorty描述字符串
	 * @param rtype 函数原型的返回值类型名
	 * @param _paramnum	函数原型的参数个数
	 * @param params 函数原型的参数类型名称列表
	 * @return 返回对应原型的index
	 */
	public int GetProtoIdxByShortyAndParams(String shorty, String rtype, int _paramnum, String[] params)
	{
		String modShorty = new String(shorty);
		modShorty = modShorty + rtype;
		for (int i = 0; i < _paramnum; i++) 
		{
			modShorty = modShorty + params[i];
		}
		return GetProtoIdxByModifiedShorty(modShorty);
	}
	
	/**
	 * 通过编码的函数原型信息获取函数原型对应的index
	 * @param _modifiedshorty 编码的函数原型信息
	 * @return 对应的index
	 */
	public int GetProtoIdxByModifiedShorty(String _modifiedshorty)
	{
		return hModifiedShortyToProtoIdx.get(_modifiedshorty);
	}
	
	/**
	 * 通过原型的index获取对应的原型的shorty描述符的index（StringTable中）
	 * @param _protoidx 原型的index
	 * @return 对应的原型的shorty描述符的index（StringTable中）
	 */
	public int GetProtoShortyIdx(int _protoidx)
	{
		return aProtoItems.get(_protoidx).shortyIdx;
	}
	
	/**
	 * 通过原型的index获取对应的原型的返回类型的index（TypeTable中）
	 * @param _protoidx 原型的index
	 * @return 对应的原型的返回类型的index（TypeTable中）
	 */
	public int GetProtoReturnTypeIdx(int _protoidx)
	{
		return aProtoItems.get(_protoidx).returnTypeIdx;
	}
	
	/**
	 * 通过原型的index获取对应的原型的参数个数
	 * @param _protoidx 原型的index
	 * @return 对应的原型的参数个数
	 */
	public int GetProtoParamsNum(int _protoidx)
	{
		return aProtoItems.get(_protoidx).paramNum;
	}
	
	/**
	 * 通过原型的index获取需要回填的offset位置
	 * @param _protoidx 原型的index
	 * @return 需要回填的offset位置
	 */
	public int GetProtoParamBackPatchOffset(int _protoidx)
	{
		return aProtoItems.get(_protoidx).parametersOffBackPatchOffset;
	}
	
	/**
	 * 通过原型的index和参数的index获取参数类型的index（TypeTable中）
	 * @param _protoidx 原型的index
	 * @param _paramidx 参数的index
	 * @return 参数类型的index（TypeTable中）
	 */
	public short GetProtoParamTypeIdx(int _protoidx, int _paramidx)
	{
		return aProtoItems.get(_protoidx).typeList.typeidx[_paramidx];
	}
	
}