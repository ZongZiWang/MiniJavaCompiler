package minijava.dexstruct;

import java.util.*;

import util.AccessFlag;
import minijava.dexstruct.items.*;

/**
 * ClassDef段的数据存储结构
 */
public class ClassDef {
	private int size;
	private ArrayList<ClassDefItem> aClassDefItems;
	private HashSet<String> hClassNameSet;
	
	private HashMap<String, Integer> hTmpClassNameToArrayIdxMap;
	
	private HashMap<String, Integer> hClassNameToClassDefItemIdx;
	private HashMap<Integer, String> hClassDefItemIdxToClassName;
	
	private DexStringTable stringTable;
	private DexTypeTable typeTable;
	private DexFieldTable fieldTable;
	private DexMethodTable methodTable;
	
	/**
	 * 构造函数
	 * 要将相关的table的引用作为参数传入，要用到相关信息
	 * @param _StringTable
	 * @param _TypeTable
	 * @param _FieldTable
	 * @param _MethodTable
	 */
	public ClassDef(DexStringTable _StringTable, DexTypeTable _TypeTable, DexFieldTable _FieldTable, DexMethodTable _MethodTable) 
	{
		stringTable = _StringTable;
		typeTable = _TypeTable;
		fieldTable = _FieldTable;
		methodTable = _MethodTable;
		aClassDefItems = new ArrayList<ClassDefItem>();
		hClassNameSet = new HashSet<String>();
		hTmpClassNameToArrayIdxMap = new HashMap<String, Integer>();
		hClassDefItemIdxToClassName = new HashMap<Integer, String>();
		hClassNameToClassDefItemIdx = new HashMap<String, Integer>();
	}
	
	/**
	 * 注册一个类
	 * @param _className 类名称，带L那种
	 * @param _superClassName 父类名称，带L那种
	 */
	public void AddClass(String _className, String _superClassName, String _srcFileName)
	{
		if (hClassNameSet.add(_className)) 
		{
			hTmpClassNameToArrayIdxMap.put(_className, aClassDefItems.size());
			//此处改成了所有类都是 public
			aClassDefItems.add(new ClassDefItem(_srcFileName, _className, _superClassName, 1));
			typeTable.AddType(_className);
			typeTable.AddType(_superClassName);
			stringTable.AddString(_srcFileName);
		}
	}
	
	/**
	 * 注册一个类，用于GUI的支持类信息
	 * @param _className 类名称，带L那种
	 * @param _superClassName 父类名称
	 * @param _srcFileName 资源文件名称
	 * @param _accessFlag 访问权限
	 */
	public void LCAddClass(String _className, String _superClassName, String _srcFileName, int _accessFlag)
	{
		if (hClassNameSet.add(_className)) 
		{
			hTmpClassNameToArrayIdxMap.put(_className, aClassDefItems.size());
			//此处改成了所有类都是 public
			aClassDefItems.add(new ClassDefItem(_srcFileName, _className, _superClassName, _accessFlag));
			typeTable.AddType(_className);
			typeTable.AddType(_superClassName);
			stringTable.AddString(_srcFileName);
		}
	}
	
	/**
	 * 向一个类中添加一个静态成员属性
	 * @param _className 类名称，带L那种
	 * @param _fieldType 成员属性类型名称
	 * @param _fieldName 成员属性名字
	 */
	public void AddStaticFieldToClass(String _className, String _fieldType, String _fieldName)
	{
		String modField = new String(_className + _fieldType + _fieldName);
		int classIdx = hTmpClassNameToArrayIdxMap.get(_className);
		if (aClassDefItems.get(classIdx).class_data_item.hModifiedFieldSet.add(modField)) 
		{
			fieldTable.AddField(_className, _fieldType, _fieldName);
			aClassDefItems.get(classIdx).class_data_item.staticFields.add(new EncodedField(_className, _fieldType, _fieldName, AccessFlag.STATIC));
		}
	}

	/**
	 * 向一个类中添加一个静态成员属性，用于GUI支持的信息
	 * @param _className 类名称，带L那种
	 * @param _fieldType 成员属性类型名称
	 * @param _fieldName 成员属性名字
	 * @param _accessFlag 静态成员属性的访问权限
	 */
	public void LCAddStaticFieldToClass(String _className, String _fieldType, String _fieldName, int _accessFlag)
	{
		String modField = new String(_className + _fieldType + _fieldName);
		int classIdx = hTmpClassNameToArrayIdxMap.get(_className);
		if (aClassDefItems.get(classIdx).class_data_item.hModifiedFieldSet.add(modField)) 
		{
			fieldTable.AddField(_className, _fieldType, _fieldName);
			aClassDefItems.get(classIdx).class_data_item.staticFields.add(new EncodedField(_className, _fieldType, _fieldName, _accessFlag));
		}
	}
	
	/**
	 * 向一个类中添加一个非静态成员属性
	 * @param _className 类名称，带L那种
	 * @param _fieldType 成员属性类型名称
	 * @param _fieldName 成员属性名字
	 */
	public void AddInstanceFieldToClass(String _className, String _fieldType, String _fieldName)
	{
		String modField = new String(_className + _fieldType + _fieldName);
		int classIdx = hTmpClassNameToArrayIdxMap.get(_className);
		if (aClassDefItems.get(classIdx).class_data_item.hModifiedFieldSet.add(modField)) 
		{
			fieldTable.AddField(_className, _fieldType, _fieldName);
			aClassDefItems.get(classIdx).class_data_item.instanceFields.add(new EncodedField(_className, _fieldType, _fieldName, 0));
		}
	}
	
	/**
	 * 向一个类中添加一个静态方法
	 * @param _className 类名称，带L
	 * @param _methodName 方法名称
	 * @param _returnType 方法返回值类型名称
	 * @param _accessFlag 访问权限，请使用lctool中的AccessFlag中的static
	 * @param _paramNum 参数个数
	 * @param _params 参数类型列表
	 * @param _registersSize 使用的寄存器数量
	 * @param _insSize 进入的寄存器数量
	 * @param _outsSize 调用的寄存器数量
	 * @param _insnsSize 代码长度（以2Byte计）
	 * @param _code 代码
	 */
	public void AddDirectMethodToClass(String _className, String _methodName, String _returnType, int _accessFlag,
			int _paramNum, String[] _params)//, short _registersSize, short _insSize, short _outsSize, int _insnsSize, byte[] _code
	{
		String modifiedMethod = new String(_className + _methodName);
		int classIdx = hTmpClassNameToArrayIdxMap.get(_className);
		if (aClassDefItems.get(classIdx).class_data_item.hModifiedMethodSet.add(modifiedMethod)) 
		{
			methodTable.AddMethod(_className, _methodName, _returnType, _paramNum, _params);
			aClassDefItems.get(classIdx).class_data_item.directMethods.add(new EncodedMethod(_className, 
					_methodName, _accessFlag));//, _registersSize, _insSize, _outsSize, _insnsSize, _code
		}
	}
	
	/**
	 * 向一个类中添加一个静态方法
	 * @param _className 类名称，带L
	 * @param _methodName 方法名称
	 * @param _returnType 方法返回值类型名称
	 * @param _accessFlag 访问权限
	 * @param _paramNum 参数个数
	 * @param _params 参数类型列表
	 * @param _registersSize 使用的寄存器数量
	 * @param _insSize 进入的寄存器数量
	 * @param _outsSize 调用的寄存器数量
	 * @param _insnsSize 代码长度（以2Byte计）
	 * @param _code 代码数组
	 */
	public void AddVirtualMethodToClass(String _className, String _methodName, String _returnType, int _accessFlag,
			int _paramNum, String[] _params)//, short _registersSize, short _insSize, short _outsSize, int _insnsSize, byte[] _code
	{
		String modifiedMethod = new String(_className + _methodName);
		int classIdx = hTmpClassNameToArrayIdxMap.get(_className);
		if (aClassDefItems.get(classIdx).class_data_item.hModifiedMethodSet.add(modifiedMethod)) 
		{
			methodTable.AddMethod(_className, _methodName, _returnType, _paramNum, _params);
			aClassDefItems.get(classIdx).class_data_item.virtualMethods.add(new EncodedMethod(_className, 
					_methodName, _accessFlag));
		}
	}
		
	/**
	 * 向一个方法中添加二进制代码<br/>
	 * 在serilize之后做
	 * @param _className 对应类名称
	 * @param _methodName 对应函数名称
	 * @param _registersSize 使用寄存器数量
	 * @param _insSize 进入的寄存器的数量
	 * @param _outsSize 调用的寄存器数量
	 * @param _insnsSize 代码长度（以2Byte计）
	 * @param _code 代码数组
	 */
	public void AddMethodCodeToMethod(String _className, String _methodName, 
			short _registersSize, short _insSize, short _outsSize, int _insnsSize, byte[] _code)
	{
		int classidx = hClassNameToClassDefItemIdx.get(_className);
		String modMethodString = new String(_className + _methodName);
		for (int i = 0; i < aClassDefItems.get(classidx).class_data_item.directMethodsSize; i++) 
		{
			if (aClassDefItems.get(classidx).class_data_item.directMethods.get(i).modifiedMethod.equals(modMethodString)) 
			{
				aClassDefItems.get(classidx).class_data_item.directMethods.get(i).dexCode
				= new CodeItem(_registersSize, _insSize, _outsSize, _insnsSize, _code);
				return;
			}
		}
		for (int i = 0; i < aClassDefItems.get(classidx).class_data_item.virtualMethodsSize; i++) 
		{
			if (aClassDefItems.get(classidx).class_data_item.virtualMethods.get(i).modifiedMethod.equals(modMethodString)) 
			{
				aClassDefItems.get(classidx).class_data_item.virtualMethods.get(i).dexCode
				= new CodeItem(_registersSize, _insSize, _outsSize, _insnsSize, _code);
				return;
			}
		}
	}
	
	/**
	 * 必须在其他的table都做完 Serilize() 之后才能做
	 */
	public void Serilize()
	{
		for (int i = 0; i < aClassDefItems.size(); i++) 
		{
			aClassDefItems.get(i).classIdx = typeTable.GetTypeIdx(aClassDefItems.get(i).classNameString);
			aClassDefItems.get(i).superClassIdx = typeTable.GetTypeIdx(aClassDefItems.get(i).superClassNameString);
			aClassDefItems.get(i).sourceFileIdx = stringTable.GetStringIdx(aClassDefItems.get(i).sourceFileNameString);
			
			aClassDefItems.get(i).class_data_item.staticFieldsSize = aClassDefItems.get(i).class_data_item.staticFields.size();
			aClassDefItems.get(i).class_data_item.instanceFieldsSize = aClassDefItems.get(i).class_data_item.instanceFields.size();
			aClassDefItems.get(i).class_data_item.directMethodsSize = aClassDefItems.get(i).class_data_item.directMethods.size();
			aClassDefItems.get(i).class_data_item.virtualMethodsSize = aClassDefItems.get(i).class_data_item.virtualMethods.size();
			
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.staticFieldsSize; j++) 
			{
				aClassDefItems.get(i).class_data_item.staticFields.get(j).fieldIdx = 
						fieldTable.GetFieldIdxByModifiedField(aClassDefItems.get(i).class_data_item.staticFields.get(j).modifiedField);
			}
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.staticFieldsSize-1; j++) 
			{
				for (int k = j; k < aClassDefItems.get(i).class_data_item.staticFieldsSize; k++) 
				{
					if (aClassDefItems.get(i).class_data_item.staticFields.get(j).fieldIdx > aClassDefItems.get(i).class_data_item.staticFields.get(k).fieldIdx) 
					{
						EncodedField tmpEncodedField = aClassDefItems.get(i).class_data_item.staticFields.get(j);
						aClassDefItems.get(i).class_data_item.staticFields.set(j, aClassDefItems.get(i).class_data_item.staticFields.get(k));
						aClassDefItems.get(i).class_data_item.staticFields.set(k, tmpEncodedField);
					}
				}
			}
			for (int j = aClassDefItems.get(i).class_data_item.staticFieldsSize-1 ; j > 0 ; j--) 
			{
				aClassDefItems.get(i).class_data_item.staticFields.get(j).fieldIdx -= aClassDefItems.get(i).class_data_item.staticFields.get(j-1).fieldIdx;
			}
			
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.instanceFieldsSize; j++) 
			{
				aClassDefItems.get(i).class_data_item.instanceFields.get(j).fieldIdx = 
						fieldTable.GetFieldIdxByModifiedField(aClassDefItems.get(i).class_data_item.instanceFields.get(j).modifiedField);
			}
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.instanceFieldsSize-1; j++) 
			{
				for (int k = j; k < aClassDefItems.get(i).class_data_item.instanceFieldsSize; k++) 
				{
					if (aClassDefItems.get(i).class_data_item.instanceFields.get(j).fieldIdx > aClassDefItems.get(i).class_data_item.instanceFields.get(k).fieldIdx) 
					{
						EncodedField tmpEncodedField = aClassDefItems.get(i).class_data_item.instanceFields.get(j);
						aClassDefItems.get(i).class_data_item.instanceFields.set(j, aClassDefItems.get(i).class_data_item.instanceFields.get(k));
						aClassDefItems.get(i).class_data_item.instanceFields.set(k, tmpEncodedField);
					}
				}
			}
			for (int j = aClassDefItems.get(i).class_data_item.instanceFieldsSize-1 ; j > 0 ; j--) 
			{
				aClassDefItems.get(i).class_data_item.instanceFields.get(j).fieldIdx -= aClassDefItems.get(i).class_data_item.instanceFields.get(j-1).fieldIdx;
			}
			
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.directMethodsSize; j++) 
			{
				aClassDefItems.get(i).class_data_item.directMethods.get(j).methodIdx =
						methodTable.GetMethodIdxByModifiedMethod(aClassDefItems.get(i).class_data_item.directMethods.get(j).modifiedMethod);
			}
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.directMethodsSize-1; j++) 
			{
				for (int k = j; k < aClassDefItems.get(i).class_data_item.directMethodsSize; k++) 
				{
					if (aClassDefItems.get(i).class_data_item.directMethods.get(j).methodIdx > aClassDefItems.get(i).class_data_item.directMethods.get(k).methodIdx) 
					{
						EncodedMethod tmpEncodedMethod = aClassDefItems.get(i).class_data_item.directMethods.get(j);
						aClassDefItems.get(i).class_data_item.directMethods.set(j, aClassDefItems.get(i).class_data_item.directMethods.get(k));
						aClassDefItems.get(i).class_data_item.directMethods.set(k, tmpEncodedMethod);
					}
				}
			}
			for (int j = aClassDefItems.get(i).class_data_item.directMethodsSize-1 ; j > 0 ; j--) 
			{
				aClassDefItems.get(i).class_data_item.directMethods.get(j).methodIdx -= aClassDefItems.get(i).class_data_item.directMethods.get(j-1).methodIdx;
			}
			
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.virtualMethodsSize; j++) 
			{
				aClassDefItems.get(i).class_data_item.virtualMethods.get(j).methodIdx =
						methodTable.GetMethodIdxByModifiedMethod(aClassDefItems.get(i).class_data_item.virtualMethods.get(j).modifiedMethod);
			}
			for (int j = 0; j < aClassDefItems.get(i).class_data_item.virtualMethodsSize-1; j++) 
			{
				for (int k = j; k < aClassDefItems.get(i).class_data_item.virtualMethodsSize; k++) 
				{
					if (aClassDefItems.get(i).class_data_item.virtualMethods.get(j).methodIdx > aClassDefItems.get(i).class_data_item.virtualMethods.get(k).methodIdx) 
					{
						EncodedMethod tmpEncodedMethod = aClassDefItems.get(i).class_data_item.virtualMethods.get(j);
						aClassDefItems.get(i).class_data_item.virtualMethods.set(j, aClassDefItems.get(i).class_data_item.virtualMethods.get(k));
						aClassDefItems.get(i).class_data_item.virtualMethods.set(k, tmpEncodedMethod);
					}
				}
			}
			for (int j = aClassDefItems.get(i).class_data_item.virtualMethodsSize-1 ; j > 0 ; j--) 
			{
				aClassDefItems.get(i).class_data_item.virtualMethods.get(j).methodIdx -= aClassDefItems.get(i).class_data_item.virtualMethods.get(j-1).methodIdx;
			}
		}
		
		//排序  要求父类在子类之前
		ArrayList<ClassNode> classNodes = new ArrayList<ClassDef.ClassNode>();
		HashMap<Integer, Integer> classIdxToArrayIdxMap = new HashMap<Integer, Integer>();
		int objIdx = typeTable.GetTypeIdx("Ljava/lang/Object;");
		int actIdx = typeTable.GetTypeIdx("Landroid/app/Activity;");
		for (int i = 0; i < aClassDefItems.size(); i++) 
		{
			classNodes.add(new ClassNode(aClassDefItems.get(i).classIdx, aClassDefItems.get(i).superClassIdx));
			classIdxToArrayIdxMap.put(aClassDefItems.get(i).classIdx, i);
		}
		for (int i = 0; i < classNodes.size(); i++) 
		{
			if (classNodes.get(i).fatheridx != objIdx && classNodes.get(i).fatheridx != actIdx) 
			{
				classNodes.get(i).SetFatherNode(classNodes.get(classIdxToArrayIdxMap.get(classNodes.get(i).fatheridx)));			
			}
		}
		ArrayList<Integer> sortRank = new ArrayList<Integer>();
		ArrayList<ClassNode> nowFatherArrayList;
		ArrayList<ClassNode> nextFatherArrayList;
		nowFatherArrayList = new ArrayList<ClassDef.ClassNode>();
		int last = classNodes.size();
		for (int i = 0; i < classNodes.size(); i++) 
		{
			if (classNodes.get(i).fatheridx == objIdx || classNodes.get(i).fatheridx == actIdx) 
			{
				nowFatherArrayList.add(classNodes.get(i));
			}
		}
		while (true) 
		{
			nextFatherArrayList = new ArrayList<ClassDef.ClassNode>();
			ArrayList<Integer> tmpSortRank = new ArrayList<Integer>();
			for (int i = 0; i < nowFatherArrayList.size(); i++) 
			{
				tmpSortRank.add(nowFatherArrayList.get(i).classidx);
				for (int j = 0; j < nowFatherArrayList.get(i).sonNodes.size(); j++) 
				{
					nextFatherArrayList.add(nowFatherArrayList.get(i).sonNodes.get(j));
				}
				last--;
			}
			for (int i = 0; i < tmpSortRank.size()-1; i++) 
			{
				for (int j = i; j < tmpSortRank.size(); j++) 
				{
					if (tmpSortRank.get(i) > tmpSortRank.get(j)) 
					{
						int t = tmpSortRank.get(i);
						tmpSortRank.set(i, tmpSortRank.get(j));
						tmpSortRank.set(j, t);
					}
				}
			}
			for (int i = 0; i < tmpSortRank.size(); i++) 
			{
				sortRank.add(tmpSortRank.get(i));
			}
			if (last == 0) 
			{
				break;
			}
			nowFatherArrayList = nextFatherArrayList;
		}
		for (int i = 0; i < sortRank.size(); i++) 
		{
			int tmp = classIdxToArrayIdxMap.get(sortRank.get(i));
			sortRank.set(i, tmp);
		}
		ArrayList<ClassDefItem> tmpItems = new ArrayList<ClassDefItem>();
		for (int i = 0; i < aClassDefItems.size(); i++) 
		{
			tmpItems.add(aClassDefItems.get(i));
		}
		for (int i = 0; i < aClassDefItems.size(); i++) 
		{
			aClassDefItems.set(i, tmpItems.get(sortRank.get(i)));
			//System.out.println(aClassDefItems.get(i).classNameString +" father:"+ aClassDefItems.get(i).superClassNameString);
		}
				
		for (int i = 0; i < aClassDefItems.size(); i++) 
		{
			hClassDefItemIdxToClassName.put(i, aClassDefItems.get(i).classNameString);
			hClassNameToClassDefItemIdx.put(aClassDefItems.get(i).classNameString, i);
		}
		
		size = aClassDefItems.size();
	}
	
	/**
	 * 更新项目数量
	 */
	public void ReFreshSize()
	{
		size = aClassDefItems.size();
	}
	
	/**
	 * 获取项目数量
	 * @return 返回值为段中项目数量
	 */
	public int GetSize()
	{
		return size;
	}
	
	/**
	 * 通过index返回对应的classdef项目
	 * @param _classdefitemidx 要返回的index
	 * @return 返回一个classdef项目
	 */
	public ClassDefItem GetClassDefItem(int _classdefitemidx)
	{
		return aClassDefItems.get(_classdefitemidx);
	}
		
	/**
	 * 内部类
	 * 进行项目的排序时辅助使用
	 */
	private class ClassNode
	{
		int classidx;
		int fatheridx;
		ClassNode fatherNode;
		ArrayList<ClassNode> sonNodes;
		public ClassNode(int _classidx, int _fatheridx) 
		{
			classidx = _classidx;
			fatheridx = _fatheridx;
			sonNodes = new ArrayList<ClassDef.ClassNode>();
		}
		void SetFatherNode(ClassNode _fathernode)
		{
			fatherNode = _fathernode;
			fatherNode.sonNodes.add(this);
		}
	}
}