package minijava.dexstruct;

import java.util.*;

import minijava.dexstruct.items.MethodItem;

/**
 * MethodTable段的数据存储结构
 */
public class DexMethodTable {
	private int size;
	private ArrayList<MethodItem> aMethodItems;
	private HashSet<String> hModifiedMethodsSet;
	private HashMap<String, Integer> hModifiedMethodStringToMethodIdx;
	private HashMap<Integer, String> hMethodIdxToModifiedMethodString;
	
	private DexStringTable stringTable;
	private DexTypeTable typeTable;
	private DexProtoTable protoTable;

	/**
	 * 构造函数，需要传入相关table的引用，这些信息会被使用到
	 * @param _stringTable
	 * @param _typeTable
	 * @param _protoTable
	 */
	public DexMethodTable(DexStringTable _stringTable, DexTypeTable _typeTable, DexProtoTable _protoTable) 
	{
		stringTable = _stringTable;
		typeTable = _typeTable;
		protoTable = _protoTable;
		aMethodItems = new ArrayList<MethodItem>();
		hModifiedMethodsSet = new HashSet<String>();
		hMethodIdxToModifiedMethodString = new HashMap<Integer, String>();
		hModifiedMethodStringToMethodIdx = new HashMap<String, Integer>();
	}
	
	/**
	 * 添加一个方法
	 * @param _className 方法所属类名
	 * @param _methodName 方法名
	 * @param _returnType 方法返回值类型名
	 * @param _paramNum 参数数量，此处是0时，_params可以为null
	 * @param _params 参数类型名列表。这里传进来的引用，我直接使用的引用而不是复制，外面的修改会对里面产生影响
	 */
	public void AddMethod(String _className, String _methodName, String _returnType, int _paramNum, String[] _params)
	{
		String modifiedMethod = new String(_className + _methodName);
		if(hModifiedMethodsSet.add(modifiedMethod))
		{
			String shorty = GenerateShorty(_returnType, _paramNum, _params);
			String modShorty = new String(shorty);
			modShorty = modShorty + _returnType;
			for (int i = 0; i < _paramNum; i++) 
			{
				modShorty = modShorty + _params[i];
			}
			typeTable.AddType(_className);
			protoTable.AddProto(shorty, _returnType, _paramNum, _params);
			stringTable.AddString(_methodName);
			aMethodItems.add(new MethodItem(_className, _methodName, shorty, modShorty, modifiedMethod));
		}
	}
	
	/**
	 * 添加一个方法，解决重载问题，GUI支持的内容中要用
	 * @param _className 方法所属类名
	 * @param _methodName 方法名
	 * @param _returnType 方法返回值类型名
	 * @param _paramNum 参数数量，此处是0时，_params可以为null
	 * @param _params 参数类型名列表。这里传进来的引用，我直接使用的引用而不是复制，外面的修改会对里面产生影响
	 */
	public void LCAddMethod(String _className, String _methodName, String _returnType, int _paramNum, String[] _params)
	{
		String modifiedMethod = new String(_className + _methodName);
		for (int i = 0; i < _paramNum; i++) 
		{
			modifiedMethod += _params[i];
		}
		if(hModifiedMethodsSet.add(modifiedMethod))
		{
			String shorty = GenerateShorty(_returnType, _paramNum, _params);
			String modShorty = new String(shorty);
			modShorty = modShorty + _returnType;
			for (int i = 0; i < _paramNum; i++) 
			{
				modShorty = modShorty + _params[i];
			}
			typeTable.AddType(_className);
			protoTable.AddProto(shorty, _returnType, _paramNum, _params);
			stringTable.AddString(_methodName);
			aMethodItems.add(new MethodItem(_className, _methodName, shorty, modShorty, modifiedMethod));
		}
	}
	
	/**
	 * 必须要在 StringTable 和 TypeTable 和 ProtoTable 都做完 Serilize() 后才能做
	 */
	public void Serilize()
	{
		for (int i = 0; i < aMethodItems.size(); i++) 
		{
			aMethodItems.get(i).classIdx = (short)typeTable.GetTypeIdx(aMethodItems.get(i).className);
			aMethodItems.get(i).protoIdx = (short)protoTable.GetProtoIdxByModifiedShorty(aMethodItems.get(i).modifiedShorty);
			aMethodItems.get(i).nameIdx = stringTable.GetStringIdx(aMethodItems.get(i).methodName);
		}
		
		for (int i = 0; i < aMethodItems.size()-1; i++) 
		{
			for (int j = i; j < aMethodItems.size(); j++) 
			{
				if (aMethodItems.get(i).classIdx > aMethodItems.get(j).classIdx) 
				{
					MethodItem tmp = aMethodItems.get(i);
					aMethodItems.set(i, aMethodItems.get(j));
					aMethodItems.set(j, tmp);
				}
				else if (aMethodItems.get(i).classIdx == aMethodItems.get(j).classIdx) 
				{
					if (aMethodItems.get(i).nameIdx > aMethodItems.get(j).nameIdx) 
					{
						MethodItem tmp = aMethodItems.get(i);
						aMethodItems.set(i, aMethodItems.get(j));
						aMethodItems.set(j, tmp);
					}
					else if (aMethodItems.get(i).nameIdx == aMethodItems.get(j).nameIdx) 
					{
						if (aMethodItems.get(i).protoIdx > aMethodItems.get(j).protoIdx) 
						{
							MethodItem tmp = aMethodItems.get(i);
							aMethodItems.set(i, aMethodItems.get(j));
							aMethodItems.set(j, tmp);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < aMethodItems.size(); i++) 
		{
			hMethodIdxToModifiedMethodString.put(i, aMethodItems.get(i).modifiedMethod);
			hModifiedMethodStringToMethodIdx.put(aMethodItems.get(i).modifiedMethod, i);
		}
		
		size = aMethodItems.size();
	}
	
	/**
	 * 获取这一段中项目数
	 * @return 这一段中的项目数
	 */
	public int GetSize()
	{
		return size;
	}
	
	/**
	 * 通过方法的信息来获取方法对应的index
	 * @param _className 方法所属类名
	 * @param _methodName 方法名
	 * @return 方法对应的index
	 */
	public int GetMethodIdxByInformation(String _className, String _methodName)
	{
		String modifiedMethod = new String(_className + _methodName);
		return GetMethodIdxByModifiedMethod(modifiedMethod);
	}
	
	/**
	 * 通过编码的方法名来获取方法对应的index
	 * @param _modifiedMethod 经过编码的方法名
	 * @return 方法对应的index
	 */
	public int GetMethodIdxByModifiedMethod(String _modifiedMethod)
	{
		return hModifiedMethodStringToMethodIdx.get(_modifiedMethod);
	}
	
	/**
	 * 通过方法的index获取对应方法的所属类的index（TypeTable中）
	 * @param idx 方法index
	 * @return 对应方法的所属类的index（TypeTable中）
	 */
	public short GetMethodClassIdx(int idx)
	{
		return aMethodItems.get(idx).classIdx;
	}
	
	/**
	 * 通过方法的index获取对应方法的原型的index（ProtoTable中）
	 * @param idx 方法index
	 * @return 对应方法的原型的index（ProtoTable中）
	 */
	public short GetMethodProtoIdx(int idx)
	{
		return aMethodItems.get(idx).protoIdx;
	}
	
	/**
	 * 通过方法的index获取对应方法的名称的index（StringTable中）
	 * @param idx 方法index
	 * @return 对应方法的名称的index（StringTable中）
	 */
	public int GetMethodNameIdx(int idx)
	{
		return aMethodItems.get(idx).nameIdx;
	}

	/**
	 * 通过方法的信息，生成对应proto的shorty
	 * @param _returnType 方法的返回类型名称
	 * @param _paramNum 方法参数个数
	 * @param _params 方法参数类型名称列表
	 * @return 对应proto的shorty字符串
	 */
	private String GenerateShorty(String _returnType, int _paramNum, String[] _params)
	{
		String retString = new String("");
		if(_returnType.startsWith("[") || _returnType.startsWith("L"))
			retString = retString + "L";
		else
			retString = retString + _returnType;
		
		for (int i = 0; i < _paramNum; i++) 
		{
			if(_params[i].startsWith("[") || _params[i].startsWith("L"))
				retString = retString + "L";
			else
				retString = retString + _params[i];
		}
		
		return retString;
	}
}