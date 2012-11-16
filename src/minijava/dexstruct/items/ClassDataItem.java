package minijava.dexstruct.items;

import java.util.*;

/**
 * ClassDataItem的数据结构
 */
public class ClassDataItem
{
	public int staticFieldsSize = 0;
	public int instanceFieldsSize;
	public int directMethodsSize;
	public int virtualMethodsSize;
	
	public ArrayList<EncodedField> staticFields;
	public ArrayList<EncodedField> instanceFields;
	public ArrayList<EncodedMethod> directMethods;
	public ArrayList<EncodedMethod> virtualMethods;
	
	public HashSet<String> hModifiedFieldSet;
	public HashSet<String> hModifiedMethodSet;
	
	public int classDataOffBackPatchOffset;
	
	/**
	 * 构造函数
	 */
	public ClassDataItem()
	{
		staticFields = new ArrayList<EncodedField>();
		instanceFields = new ArrayList<EncodedField>();
		directMethods = new ArrayList<EncodedMethod>();
		virtualMethods = new ArrayList<EncodedMethod>();
		hModifiedFieldSet = new HashSet<String>();
		hModifiedMethodSet = new HashSet<String>();
	}
}