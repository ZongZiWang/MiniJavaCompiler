package minijava.dexbuilder;

import java.io.*;
import java.util.*;

import util.*;

import minijava.dexstruct.*;

/**
 * 提交准备好的类的信息和编译好的二进制代码，<br/>
 * 生成最终的dex文件的类
 *
 */
public class DexPrinter {
	
	FileOutputStream target = null;
	
	//7个信息列表
	Header dexHeader;
	DexStringTable dexStringTable;
	DexTypeTable dexTypeTable;
	DexProtoTable dexProtoTable;
	DexFieldTable dexFieldTable;
	DexMethodTable dexMethodTable;
	ClassDef dexClassDef;
	
	//验证码生成器
	ReVerify dexReVerifier;
	
	//信息计数器
	int codeItemCount;
	int typeListCount;
	int dataBegin;
	String mainClassNameString;
	
	//代码缓冲区
	ArrayList<Byte> outBuffer;
	int pos;
	
	/**
	 * 构造函数
	 * @param file 参数为输出文件名 一般为“classes.dex”
	 */
	public DexPrinter(String file) 
	{
		target = SdcardManager.getFileOutputStream(file);
		dexHeader = new Header();
		dexStringTable = new DexStringTable();
		dexTypeTable = new DexTypeTable(dexStringTable);
		dexProtoTable = new DexProtoTable(dexTypeTable, dexStringTable);
		dexFieldTable = new DexFieldTable(dexTypeTable, dexStringTable);
		dexMethodTable = new DexMethodTable(dexStringTable, dexTypeTable, dexProtoTable);
		dexClassDef = new ClassDef(dexStringTable, dexTypeTable, dexFieldTable, dexMethodTable);
		outBuffer = new ArrayList<Byte>();
		pos = 0;
		codeItemCount = 0;
		typeListCount = 0;
		dexReVerifier = new ReVerify();
	}
	
	/**
	 * 添加源代码中含有main函数的类的名称
	 * @param _mainClassNameString 含有main函数的类的名称，不要加任何修饰
	 */
	public void AddMainClassName(String _mainClassNameString)
	{
		mainClassNameString = _mainClassNameString;
	}
	
	/**
	 * 添加类信息：注册一个类
	 * @param _className 类名称，不要加 L 和 ；
	 * @param _superClassName 这个类的父类名称，不要加 L 和 ；
	 */
	public void AddClass(String _className, String _superClassName)
	{
		//TODO
		_className = FixPoint(_className);
		_superClassName = FixPoint(_superClassName);
		
		_className = FixClassType(_className);
		_superClassName = FixClassType(_superClassName);
		String srcFileNameString = new String(mainClassNameString + ".java");
		dexClassDef.AddClass(_className, _superClassName, srcFileNameString);
	}
	
	/**
	 * 添加类信息：注册一个类<br/>
	 * 内部调用，可以指定类所属的源文件名以及类的访问权限，用于添加GUI支持类信息
	 * @param _className 类名称，不要加 L 和 ；
	 * @param _superClassName 这个类的父类名称，不要加 L 和 ；
	 * @param _srcFileName 这个类所属的源文件名称
	 * @param _accessFlag 这个类的访问权限
	 */
	private void LCAddClass(String _className, String _superClassName, String _srcFileName, int _accessFlag)
	{
		//TODO
		_className = FixPoint(_className);
		_superClassName = FixPoint(_superClassName);
		
		_className = FixClassType(_className);
		_superClassName = FixClassType(_superClassName);
		dexClassDef.LCAddClass(_className, _superClassName, _srcFileName, _accessFlag);
	}
	
	/**
	 * 添加类信息：向一个类添加一个静态成员属性，类必须注册过
	 * @param _className 对应类的名称，不要加 L 和 ；
	 * @param _fieldType 对应成员属性类型，不要加 L 和 ；
	 * @param _fieldName 对应成员属性名字，不要加 L 和 ；
	 */
	public void AddStaticFieldToClass(String _className, String _fieldType, String _fieldName)
	{
		//TODO
		_className = FixPoint(_className);
		_fieldType = FixPoint(_fieldType);
		_fieldName = FixPoint(_fieldName);
		
		_className = FixClassType(_className);
		_fieldType = FixClassType(_fieldType);
		dexClassDef.AddStaticFieldToClass(_className, _fieldType, _fieldName);
	}

	/**
	 * 添加类信息：向一个类添加一个静态成员属性，类必须注册过<br/>
	 * 内部调用，可以指定静态成员属性的访问权限，用于添加GUI支持类信息
	 * @param _className 对应类的名称，不要加 L 和 ；
	 * @param _fieldType 对应成员属性类型，不要加 L 和 ；
	 * @param _fieldName 对应成员属性名字，不要加 L 和 ；
	 * @param _accessFlag 对应成员属性访问权限
	 */
	private void LCAddStaticFieldToClass(String _className, String _fieldType, String _fieldName, int _accessFlag)
	{
		//TODO
		_className = FixPoint(_className);
		_fieldType = FixPoint(_fieldType);
		_fieldName = FixPoint(_fieldName);
		
		_className = FixClassType(_className);
		_fieldType = FixClassType(_fieldType);
		dexClassDef.LCAddStaticFieldToClass(_className, _fieldType, _fieldName, _accessFlag);
	}
	
	/**
	 * 添加类信息：向一个类添加一个非静态成员属性，类必须注册过
	 * @param _className 对应类的名称，不要加 L 和 ；
	 * @param _fieldType 对应成员属性类型，不要加 L 和 ；
	 * @param _fieldName 对应成员属性名字，不要加 L 和 ；
	 */
	public void AddInstanceFieldToClass(String _className, String _fieldType, String _fieldName)
	{
		//TODO
		_className = FixPoint(_className);
		_fieldType = FixPoint(_fieldType);
		_fieldName = FixPoint(_fieldName);

		_className = FixClassType(_className);
		_fieldType = FixClassType(_fieldType);
		dexClassDef.AddInstanceFieldToClass(_className, _fieldType, _fieldName);
	}
	
	/**
	 * 添加类信息：向一个类添加一个静态方法，类必须注册过
	 * @param _className 对应类名字，不要加 L 和 ；
	 * @param _methodName 对应方法名字
	 * @param _returnType 对应方法返回值类型，不要加 L 和 ；
	 * @param _accessFlag 对应方法的访问权限
	 * @param _paramNum 对应方法的参数个数
	 * @param _params 对应方法所有参数类型，按照顺序，不要加 L 和 ；
	 */
	public void AddDirectMethodToClass(String _className, String _methodName, String _returnType, int _accessFlag,
			int _paramNum, String[] _params)
	{
		//TODO
		_className = FixPoint(_className);
		_methodName = FixPoint(_methodName);
		_returnType = FixPoint(_returnType);
		
		_className = FixClassType(_className);
		_returnType = FixClassType(_returnType);
		for (int i = 0; i < _paramNum; i++) 
		{
			//TODO
			_params[i] = FixPoint(_params[i]);
			
			_params[i] = FixClassType(_params[i]); 
		}
		dexClassDef.AddDirectMethodToClass(_className, _methodName, _returnType, _accessFlag, _paramNum, _params);
	}
	
	/**
	 * 添加类信息：向一个类添加一个非静态方法，类必须注册过
	 * @param _className 对应类名字，不要加 L 和 ；
	 * @param _methodName 对应方法名字
	 * @param _returnType 对应方法返回值类型，不要加 L 和 ；
	 * @param _accessFlag 对应方法的访问权限
	 * @param _paramNum 对应方法的参数个数
	 * @param _params 对应方法所有参数类型，按照顺序，不要加 L 和 ；
	 */
	public void AddVirtualMethodToClass(String _className, String _methodName, String _returnType, int _accessFlag,
			int _paramNum, String[] _params)
	{
		//TODO
		_className = FixPoint(_className);
		_methodName = FixPoint(_methodName);
		_returnType = FixPoint(_returnType);
		
		_className = FixClassType(_className);
		_returnType = FixClassType(_returnType);
		for (int i = 0; i < _paramNum; i++) 
		{
			//TODO
			_params[i] = FixPoint(_params[i]);
			
			_params[i] = FixClassType(_params[i]); 
		}
		dexClassDef.AddVirtualMethodToClass(_className, _methodName, _returnType, _accessFlag, _paramNum, _params);
	}
	
	/**
	 * 添加编译好的二进制代码：向一个方法添加编译好的代码
	 * @param _className 方法所属的类名称，不要加 L 和 ；
	 * @param _methodName 方法名字
	 * @param _registersSize 使用的寄存器数量
	 * @param _insSize 传入寄存器数量
	 * @param _outsSize 内部调用使用的寄存器数量
	 * @param _insnsSize 二进制代码长度（以2Byte计的个数）
	 * @param _code 二进制代码数组
	 */
	public void AddMethodCodeToMethod(String _className, String _methodName, 
			short _registersSize, short _insSize, short _outsSize, int _insnsSize, byte[] _code)
	{
		//TODO
		_className = FixPoint(_className);
		_methodName = FixPoint(_methodName);
		
		_className = FixClassType(_className);
		dexClassDef.AddMethodCodeToMethod(_className, _methodName, _registersSize, _insSize, _outsSize, _insnsSize, _code);
	}
	
	/**
	 * 添加类信息：添加一个类型<br/>
	 * 这个函数根本没被用到
	 * @param _localType 原始状态的字符串
	 */
	public void AddLocalType(String _localType)
	{
		_localType = FixPoint(_localType);
		_localType = FixClassType(_localType);
		dexTypeTable.AddType(_localType);
	}
	
	/**
	 * 通过一个字符串，获取这个字符串在StringTable中的index
	 * @param _string 要查询index的字符串
	 * @return 返回对应的index
	 */
	public int GetStringIdx(String _string)
	{
		//TODO
		_string = FixPoint(_string);
		
		return dexStringTable.GetStringIdx(_string);
	}
	
	/**
	 * 通过一个类型名称字符串，获取这个字符串在TypeTable中的index
	 * @param _type 要查询的类型名称字符串，不要加 L 和 ；
	 * @return 返回对应的index
	 */
	public int GetTypeIdx(String _type)
	{
		//TODO
		_type = FixPoint(_type);
		
		_type = FixClassType(_type);
		return dexTypeTable.GetTypeIdx(_type);
	}
	
	/**
	 * 通过一个成员属性的信息，获取这个成员属性在FieldTable中的index
	 * @param _class 对应成员属性所属的类的名称，不要加 L 和 ；
	 * @param _type 对应成员属性的类型名称，不要加 L 和 ；
	 * @param _name 对应成员属性的名称
	 * @return 返回对应的index
	 */
	public int GetFieldIdx(String _class, String _type, String _name)
	{
		//TODO
		_class = FixPoint(_class);
		_type = FixPoint(_type);
		_name = FixPoint(_name);

		_class = FixClassType(_class);
		_type = FixClassType(_type);
		return dexFieldTable.GetFieldIdxByClassAndTypeAndName(_class, _type, _name);
	}
	
	/**
	 * 通过一个成员方法的信息，获取这个成员方法在MethodTable中的index
	 * @param _className 对应成员方法所属类的名称，不要加 L 和 ；
	 * @param _methodName 对应成员方法的名称
	 * @return 返回对应的index
	 */
	public int GetMethodIdx(String _className, String _methodName)
	{
		//TODO
		_className = FixPoint(_className);
		_methodName = FixPoint(_methodName);
		
		_className = FixClassType(_className);
		return dexMethodTable.GetMethodIdxByInformation(_className, _methodName);
	}

	/**
	 * 通过一个成员方法的信息，获取这个成员方法在MethodTable中的index<br />
	 * 内部调用，拥有更高信息权限，因为要处理一个重载过的提供GUI支持的系统方法
	 * @param _className 对应成员方法所属类的名称，不要加 L 和 ；
	 * @param _methodName 对应成员方法的名称
	 * @param _paramNum 对应成员方法的参数数量
	 * @param _params 对应成员方法各个参数的类型数组
	 * @return 返回对应的index
	 */
	private int LCGetMethodIdx(String _className, String _methodName, int _paramNum, String[] _params)
	{
		//TODO
		_className = FixPoint(_className);
		_methodName = FixPoint(_methodName);
		
		_className = FixClassType(_className);
		String modifiedMethod = new String(_className + _methodName);
		for (int i = 0; i < _paramNum; i++) 
		{
			modifiedMethod += _params[i];
		}
		return dexMethodTable.GetMethodIdxByModifiedMethod(modifiedMethod);
	}
	
	/**
	 * 通过index获取一个成员属性的名称
	 * @param _fieldidx 要查询的index
	 * @return 返回一个成员属性的名称
	 */
	public String GetFieldNameByIdx(int _fieldidx)
	{
		return dexStringTable.GetString(dexFieldTable.GetFieldNameIdx(_fieldidx));
	}
	
	/**
	 * 通过index获取一个类型的名称
	 * @param _typeidx 要查询的index
	 * @return 返回结果会带L和；严禁将这个String再用于Get
	 */
	public String GetTypeNameByIdx(int _typeidx)
	{
		return dexTypeTable.GetTypeString(_typeidx);
	}
	
	/**
	 * 通过index获取一个方法的名称
	 * @param _methodidx 要查询的index
	 * @return 返回对应的方法名称
	 */
	public String GetMethodNameByIdx(int _methodidx) {
		return dexStringTable.GetString(dexMethodTable.GetMethodNameIdx(_methodidx));
	}
	
	/**
	 * 进行排序前的预处理项目<br/>
	 * 在这里要加入对GUI支持的系统函数<br/>
	 * 并为每一个类加一个默认的构造函数
	 */
	private void Pretreatment()
	{
		String[] tmpempty = new String[0];
		dexMethodTable.AddMethod("Ljava/lang/Object;", "<init>", "V", 0, tmpempty);

		if (!Mode.IsOutputApk()) 
		{
			dexFieldTable.AddField("Ljava/lang/System;", "Ljava/io/PrintStream;", "out");
			String[] tmp = new String[1];
			tmp[0] = "I";
			dexMethodTable.AddMethod("Ljava/io/PrintStream;", "println", "V", 1, tmp);
		}
		else 
		{
			AddApkNotDefinedThings();
			AddApkClasses();
		}

		dexClassDef.ReFreshSize();
		
		for (int i = 0; i < dexClassDef.GetSize(); i++) 
		{
			String[] params = new String[0];
			String initNameString = DeFixClassType(dexClassDef.GetClassDefItem(i).classNameString);
			AddDirectMethodToClass(initNameString, "<init>", "V", 65537, 0, params);
		}
		
	}
	
	/**
	 * 添加GUI支持的一部分<br/>
	 * 在这里加入的是GUI支持中调用的一些库函数库变量等等的<br/>
	 * 不出现在类信息中的信息
	 */
	private void AddApkNotDefinedThings() 
	{
		String[] params;
		
		dexTypeTable.AddType("Ljava/lang/StringBuilder;");
		params = new String[1];
		params[0] = new String("Ljava/lang/Object;");
		dexMethodTable.AddMethod("Ljava/lang/String;", "valueOf", "Ljava/lang/String;", 1, params);
		params = new String[1];
		params[0] = new String("Ljava/lang/String;");
		dexMethodTable.AddMethod("Ljava/lang/StringBuilder;", "<init>", "V", 1, params);
		params = new String[1];
		params[0] = new String("I");
		dexMethodTable.LCAddMethod("Ljava/lang/StringBuilder;", "append", "Ljava/lang/StringBuilder;", 1, params);
		params = new String[1];
		params[0] = new String("C");
		dexMethodTable.LCAddMethod("Ljava/lang/StringBuilder;", "append", "Ljava/lang/StringBuilder;", 1, params);
		params = new String[0];
		dexMethodTable.AddMethod("Ljava/lang/StringBuilder;", "toString", "Ljava/lang/String;", 0, params);
		params = new String[0];
		dexMethodTable.AddMethod("Landroid/app/Activity;", "<init>", "V", 0, params);
		params = new String[1];
		params[0] = new String("Landroid/os/Bundle;");
		dexMethodTable.AddMethod("Landroid/app/Activity;", "onCreate", "V", 1, params);
		params = new String[1];
		params[0] = new String("I");
		dexMethodTable.AddMethod("Lminijava/output/MiniJavaOutputActivity;", "setContentView", "V", 1, params);
		params = new String[1];
		params[0] = new String("I");
		dexMethodTable.AddMethod("Lminijava/output/MiniJavaOutputActivity;", "findViewById", "Landroid/view/View;", 1, params);
		params = new String[1];
		params[0] = new String("Ljava/lang/CharSequence;");
		dexMethodTable.AddMethod("Landroid/widget/TextView;", "setText", "V", 1, params);
		
		dexStringTable.AddString("");
		dexStringTable.AddString("Designed By ZongZiWang, LIuCHi, LynnXie 2011-2036 All Rights Reserved ^_^\n");
	}
	
	/**
	 * 添加GUI支持的一部分<br/>
	 * 在这里加入的是提供GUI支持的类信息
	 */
	private void AddApkClasses() 
	{
		String[] params;

		//class #0
		LCAddClass("minijava/output/MiniJavaOutput", "java/lang/Object", "MiniJavaOutput.java", 1);
		LCAddStaticFieldToClass("minijava/output/MiniJavaOutput", "java/lang/String", "answer", 12);
		params = new String[0];
		AddDirectMethodToClass("minijava/output/MiniJavaOutput", "<clinit>", "void", 65544, 0, params);
		params = new String[1];
		params[0] = new String("int");
		AddDirectMethodToClass("minijava/output/MiniJavaOutput", "addAnswer", "boolean", 9, 1, params);
		params = new String[0];
		AddDirectMethodToClass("minijava/output/MiniJavaOutput", "getAnswer", "java/lang/String", 9, 0, params);
		params = new String[0];
		AddDirectMethodToClass("minijava/output/MiniJavaOutput", "init", "void", 9, 0, params);
		
		//class #1
		LCAddClass("minijava/output/MiniJavaOutputActivity", "android/app/Activity", "MiniJavaOutputActivity.java", 1);
		params = new String[1];
		params[0] = new String("android/os/Bundle");
		AddVirtualMethodToClass("minijava/output/MiniJavaOutputActivity", "onCreate", "void", 1, 1, params);
		params = new String[0];
		AddVirtualMethodToClass("minijava/output/MiniJavaOutputActivity", "test", "void", 1, 0, params);
		
		//class #2
		LCAddClass("minijava/output/R$attr", "java/lang/Object", "R.java", 17);
		
		//class #3
		LCAddClass("minijava/output/R$drawable", "java/lang/Object", "R.java", 17);
		LCAddStaticFieldToClass("minijava/output/R$drawable", "int", "ic_launcher", 25);
		
		//class #4
		LCAddClass("minijava/output/R$id", "java/lang/Object", "R.java", 17);
		LCAddStaticFieldToClass("minijava/output/R$id", "int", "sv1", 25);
		LCAddStaticFieldToClass("minijava/output/R$id", "int", "tv1", 25);
		
		//class #5
		LCAddClass("minijava/output/R$layout", "java/lang/Object", "R.java", 17);
		LCAddStaticFieldToClass("minijava/output/R$layout", "int", "main", 25);
		
		//class #6
		LCAddClass("minijava/output/R$string", "java/lang/Object", "R.java", 17);
		LCAddStaticFieldToClass("minijava/output/R$string", "int", "app_name", 25);
		
		//class #7
		LCAddClass("minijava/output/R", "java/lang/Object", "R.java", 17);
	}
	
	/**
	 * 添加GUI支持的一部分<br/>
	 * 向GUI支持的类的函数中添加代码
	 */
	private void AddCodeToApkClasses() 
	{
		String className;
		byte[] code;
		int codeLength;
		int stringIdx;
		int fieldIdx;
		int typeIdx;
		int methodIdx;
		String[] params;
		
		//#0
		className = new String("minijava/output/MiniJavaOutput");
		
		codeLength = 5;
		code = new byte[codeLength*2];
		code[0] = (byte)26;
		code[1] = (byte)0;
		stringIdx = dexStringTable.GetStringIdx("");
		code[2] = (byte)stringIdx;
		stringIdx = stringIdx >>> 8;
		code[3] = (byte)stringIdx;
		code[4] = (byte)105;
		code[5] = (byte)0;
		fieldIdx = GetFieldIdx("minijava/output/MiniJavaOutput", "java/lang/String", "answer");
		code[6] = (byte)fieldIdx;
		fieldIdx = fieldIdx >>> 8;
		code[7] = (byte)fieldIdx;
		code[8] = (byte)14;
		code[9] = (byte)0;
		AddMethodCodeToMethod(className, "<clinit>", (short)1, (short)0, (short)0, codeLength, code);
		
		codeLength = 29;
		code = new byte[codeLength*2];
		code[0] = (byte)34;
		code[1] = (byte)0;
		typeIdx = GetTypeIdx("java/lang/StringBuilder");
		code[2] = (byte)typeIdx;
		typeIdx = typeIdx >>> 8;
		code[3] = (byte)typeIdx;
		code[4] = (byte)98;
		code[5] = (byte)1;
		fieldIdx = GetFieldIdx("minijava/output/MiniJavaOutput", "java/lang/String", "answer");
		code[6] = (byte)fieldIdx;
		fieldIdx = fieldIdx >>> 8;
		code[7] = (byte)fieldIdx;
		code[8] = (byte)113;
		code[9] = (byte)16;
		methodIdx = GetMethodIdx("java/lang/String", "valueOf");
		code[10] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[11] = (byte)methodIdx;
		code[12] = (byte)1;
		code[13] = (byte)0;
		code[14] = (byte)12;
		code[15] = (byte)1;
		code[16] = (byte)112;
		code[17] = (byte)32;
		methodIdx = GetMethodIdx("java/lang/StringBuilder", "<init>");
		code[18] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[19] = (byte)methodIdx;
		code[20] = (byte)16;
		code[21] = (byte)0;
		code[22] = (byte)110;
		code[23] = (byte)32;
		params = new String[1];
		params[0] = new String("I");
		methodIdx = LCGetMethodIdx("java/lang/StringBuilder", "append", 1, params);
		code[24] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[25] = (byte)methodIdx;
		code[26] = (byte)32;
		code[27] = (byte)0;
		code[28] = (byte)12;
		code[29] = (byte)0;
		code[30] = (byte)19;
		code[31] = (byte)1;
		code[32] = (byte)10;
		code[33] = (byte)0;
		code[34] = (byte)110;
		code[35] = (byte)32;
		params = new String[1];
		params[0] = new String("C");
		methodIdx = LCGetMethodIdx("java/lang/StringBuilder", "append", 1, params);
		code[36] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[37] = (byte)methodIdx;
		code[38] = (byte)16;
		code[39] = (byte)0;
		code[40] = (byte)12;
		code[41] = (byte)0;
		code[42] = (byte)110;
		code[43] = (byte)16;
		methodIdx = GetMethodIdx("java/lang/StringBuilder", "toString");
		code[44] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[45] = (byte)methodIdx;
		code[46] = (byte)0;
		code[47] = (byte)0;
		code[48] = (byte)12;
		code[49] = (byte)0;
		code[50] = (byte)105;
		code[51] = (byte)0;
		fieldIdx = GetFieldIdx("minijava/output/MiniJavaOutput", "java/lang/String", "answer");
		code[52] = (byte)fieldIdx;
		fieldIdx = fieldIdx >>> 8;
		code[53] = (byte)fieldIdx;
		code[54] = (byte)18;
		code[55] = (byte)16;
		code[56] = (byte)15;
		code[57] = (byte)0;
		AddMethodCodeToMethod(className, "addAnswer", (short)3, (short)1, (short)2, codeLength, code);
		
		codeLength = 3;
		code = new byte[codeLength*2];
		code[0] = (byte)98;
		code[1] = (byte)0;
		fieldIdx = GetFieldIdx("minijava/output/MiniJavaOutput", "java/lang/String", "answer");
		code[2] = (byte)fieldIdx;
		fieldIdx = fieldIdx >>> 8;
		code[3] = (byte)fieldIdx;
		code[4] = (byte)17;
		code[5] = (byte)0;
		AddMethodCodeToMethod(className, "getAnswer", (short)1, (short)0, (short)0, codeLength, code);
		
		codeLength = 5;
		code = new byte[codeLength*2];
		code[0] = (byte)26;
		code[1] = (byte)0;
		stringIdx = GetStringIdx("Designed By ZongZiWang, LIuCHi, LynnXie 2011-2036 All Rights Reserved ^_^\n");
		code[2] = (byte)stringIdx;
		stringIdx = stringIdx >>> 8;
		code[3] = (byte)stringIdx;
		code[4] = (byte)105;
		code[5] = (byte)0;
		fieldIdx = GetFieldIdx("minijava/output/MiniJavaOutput", "java/lang/String", "answer");
		code[6] = (byte)fieldIdx;
		fieldIdx = fieldIdx >>> 8;
		code[7] = (byte)fieldIdx;
		code[8] = (byte)14;
		code[9] = (byte)0;
		AddMethodCodeToMethod(className, "init", (short)1, (short)0, (short)0, codeLength, code);
		
		//#1
		className = new String("minijava/output/MiniJavaOutputActivity");
		
		codeLength = 31;
		code = new byte[codeLength*2];
		code[0] = (byte)111;
		code[1] = (byte)32;
		methodIdx = GetMethodIdx("android/app/Activity", "onCreate");
		code[2] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[3] = (byte)methodIdx;
		code[4] = (byte)50;
		code[5] = (byte)0;
		code[6] = (byte)21;
		code[7] = (byte)1;
		code[8] = (byte)3;
		code[9] = (byte)127;
		code[10] = (byte)110;
		code[11] = (byte)32;
		methodIdx = GetMethodIdx("minijava/output/MiniJavaOutputActivity", "setContentView");
		code[12] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[13] = (byte)methodIdx;
		code[14] = (byte)18;
		code[15] = (byte)0;
		code[16] = (byte)113;
		code[17] = (byte)0;
		methodIdx = GetMethodIdx("minijava/output/MiniJavaOutput", "init");
		code[18] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[19] = (byte)methodIdx;
		code[20] = (byte)0;
		code[21] = (byte)0;
		code[22] = (byte)110;
		code[23] = (byte)16;
		methodIdx = GetMethodIdx("minijava/output/MiniJavaOutputActivity", "test");
		code[24] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[25] = (byte)methodIdx;
		code[26] = (byte)2;
		code[27] = (byte)0;
		code[28] = (byte)20;
		code[29] = (byte)1;
		code[30] = (byte)1;
		code[31] = (byte)0;
		code[32] = (byte)5;
		code[33] = (byte)127;
		code[34] = (byte)110;
		code[35] = (byte)32;
		methodIdx = GetMethodIdx("minijava/output/MiniJavaOutputActivity", "findViewById");
		code[36] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[37] = (byte)methodIdx;
		code[38] = (byte)18;
		code[39] = (byte)0;
		code[40] = (byte)12;
		code[41] = (byte)0;
		code[42] = (byte)31;
		code[43] = (byte)0;
		typeIdx = GetTypeIdx("android/widget/TextView");
		code[44] = (byte)typeIdx;
		typeIdx = typeIdx >>> 8;
		code[45] = (byte)typeIdx;
		code[46] = (byte)113;
		code[47] = (byte)0;
		methodIdx = GetMethodIdx("minijava/output/MiniJavaOutput", "getAnswer");
		code[48] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[49] = (byte)methodIdx;
		code[50] = (byte)0;
		code[51] = (byte)0;
		code[52] = (byte)12;
		code[53] = (byte)1;
		code[54] = (byte)110;
		code[55] = (byte)32;
		methodIdx = GetMethodIdx("android/widget/TextView", "setText");
		code[56] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[57] = (byte)methodIdx;
		code[58] = (byte)16;
		code[59] = (byte)0;
		code[60] = (byte)14;
		code[61] = (byte)0;
		AddMethodCodeToMethod(className, "onCreate", (short)4, (short)2, (short)2, codeLength, code);
		
		codeLength = 5;//TODO :this need change!
		code = new byte[codeLength*2];
		methodIdx = GetMethodIdx(mainClassNameString, "main");
		code[0] = (byte)18;
		code[1] = (byte)0;
		code[2] = (byte)113;
		code[3] = (byte)16;
		code[4] = (byte)methodIdx;
		methodIdx = methodIdx >>> 8;
		code[5] = (byte)methodIdx;
		code[6] = (byte)0;
		code[7] = (byte)0;
		code[8] = (byte)14;
		code[9] = (byte)0;
		AddMethodCodeToMethod(className, "test", (short)2, (short)1, (short)1, codeLength, code);
	}
	
	/**
	 * 给每一个注册类的默认构造函数添加对应的代码<br/>
	 * 在排序后自动调用
	 */
	private void AddInitToAllClass()
	{
		if (Mode.isDebugMode()) for (int i = 0; i < dexMethodTable.GetSize(); i++) 
		{
			System.out.print(dexTypeTable.GetTypeString(dexMethodTable.GetMethodClassIdx(i))+" ");
			System.out.print(dexStringTable.GetString(dexProtoTable.GetProtoShortyIdx(dexMethodTable.GetMethodProtoIdx(i)))+" ");
			System.out.println(dexStringTable.GetString(dexMethodTable.GetMethodNameIdx(i)));
		}

		for (int i = 0; i < dexClassDef.GetSize(); i++) 
		{
			String nameString = dexClassDef.GetClassDefItem(i).classNameString;
			if (Mode.isDebugMode()) System.out.println("LCLCLCLCLCLCLCLCLC!!!!!!!!!!");
			byte[] code = new byte[8];
			code[0] = 112;
			code[1] = 16;
			String superNameString = dexClassDef.GetClassDefItem(i).superClassNameString;
			if (Mode.isDebugMode()) System.out.println(nameString);
			if (Mode.isDebugMode()) System.out.println(superNameString);
			int methodidx = dexMethodTable.GetMethodIdxByInformation(superNameString , "<init>");
			code[2] = (byte)methodidx;
			methodidx = methodidx >>> 8;
			code[3] = (byte)methodidx;
			code[4] = 0;
			code[5] = 0;
			code[6] = 14;
			code[7] = 0;
			nameString = DeFixClassType(nameString);
			AddMethodCodeToMethod(nameString, "<init>", (short)1, (short)1, (short)1, 4, code);
		}
	}
	
	/**
	 * 对所有类信息进行排序<br/>
	 * 由外部决定调用时机，在添加完所有类信息后调用
	 */
	public void SerilizeAll()
	{
		Pretreatment();
		
		//每一段进行排序
		dexStringTable.Serilize();
		dexTypeTable.Serilize();
		dexProtoTable.Serilize();
		dexFieldTable.Serilize();
		dexMethodTable.Serilize();
		dexClassDef.Serilize();
		
		AddInitToAllClass();
		
		if (Mode.IsOutputApk()) 
		{
			AddCodeToApkClasses();
		}
	}
	
	/**
	 * 输出整个dex文件<br/>
	 * 由外部决定调用时机，在将所有方法代码加入方法之后调用
	 */
	public void PrintDex()
	{
		pos = 0;
		PrintHeader();
		PrintStringTable();
		PrintTypeTable();
		PrintProtoTable();
		PrintFieldTable();
		PrintMethodTable();
		PrintClassDef();
		PrintAnnotationSetItems();
		PrintCodeItems();
		PrintAnnotationsDirectoryItems();
		PrintProtoItemTypeLists();
		PrintStringImages();
		PrintDebugInfo();
		PrintAnnotationOffItems();
		PrintSecrectCode();
		PrintStaticValueItems();
		PrintClassDataItem();
		PrintAlien();
		PrintMap();
		ReVerify();
		FlushBuffer();
	}
	
	/**
	 * 对产生的存储在缓存中的dex文件，生成对应的checksum和SHA-1验证<br/>
	 * 以生成正确的dex文件
	 */
	private void ReVerify()
	{
		dexReVerifier.DoReVerify(outBuffer);
	}
	
	/**
	 * 输出Header部分到输出缓冲
	 */
	private void PrintHeader()
	{
		//Print MagicCode
		for (int i = 0; i < 8; i++) 
		{
			BytePrinter((byte) dexHeader.magicCode[i]);
		}
		
		//Print CheckSum
		//Need Backpatch
		for (int i = 0; i < 4; i++) 
		{
			BytePrinter((byte)0);
		}
		
		//Print SHA-1
		//Need Backpatch
		for (int i = 0; i < 20; i++) 
		{
			BytePrinter((byte)0);
		}
		
		//Print FileLength
		//Need Backpatch
		IntPrinter(0);
		
		//Print HeaderLength
		IntPrinter(112);
		
		//Print EndianTag
		for (int i = 0; i < 4; i++) 
		{
			BytePrinter(dexHeader.endianTag[i]);
		}
		
		//Print LinkSize = 0
		IntPrinter(0);
		
		//Print linkOff = 0
		IntPrinter(0);
		
		//Print MapOff
		//Need Backpatch
		IntPrinter(0);
		
		//Print StringSize
		IntPrinter(dexStringTable.GetSize());
		
		//Print StringOffset
		//Need Backpatch
		IntPrinter(0);
		
		//Print TypeSize
		IntPrinter(dexTypeTable.GetSize());
		
		//PrintTypeOffset
		//Need Backpatch
		IntPrinter(0);
		
		//PrintProtoSize
		IntPrinter(dexProtoTable.GetSize());
		
		//PrintProtoOffset
		//Need Backpatch
		IntPrinter(0);
		
		//PrintFieldSize
		IntPrinter(dexFieldTable.GetSize());
		
		//PrintFieldOffset
		//Need Backpatch
		IntPrinter(0);
		
		//PrintMethodSize
		IntPrinter(dexMethodTable.GetSize());
		
		//PrintMethodOffset
		//Need Backpatch
		IntPrinter(0);
		
		//Print ClassDefSize
		IntPrinter(dexClassDef.GetSize());

		//Print ClassDefOffset
		//Need Backpatch
		IntPrinter(0);
		
		//PrintDataSize
		//Need Backpatch
		IntPrinter(0);
		
		//PrintDataOffset
		//Need Backpatch
		IntPrinter(0);
	}
	
	/**
	 * 输出StringTable部分到输出缓冲
	 */
	private void PrintStringTable()
	{
		dexHeader.stringIDsOffset = pos;
		ChangeInt(60, pos);
		for (int i = 0; i < dexStringTable.GetSize(); i++) 
		{
			dexStringTable.SetBackPatchOff(i, pos);
			IntPrinter(0);
		}
	}
	
	/**
	 * 输出TypeTable部分到输出缓冲
	 */
	private void PrintTypeTable() 
	{
		dexHeader.typeIDsOffset = pos;
		ChangeInt(68, pos);
		for (int i = 0; i < dexTypeTable.GetSize(); i++) 
		{
			IntPrinter(dexTypeTable.GetTypeStringIdx(i));
		}
	}

	/**
	 * 输出ProtoTable部分到输出缓冲
	 */
	private void PrintProtoTable() 
	{
		dexHeader.protoIDsOffset = pos;
		ChangeInt(76, pos);
		for (int i = 0; i < dexProtoTable.GetSize(); i++) 
		{
			IntPrinter(dexProtoTable.GetProtoShortyIdx(i));
			IntPrinter(dexProtoTable.GetProtoReturnTypeIdx(i));
			dexProtoTable.SetBackPatchOff(i, pos);
			IntPrinter(0);
		}
	}

	/**
	 * 输出FieldTable部分到输出缓冲
	 */
	private void PrintFieldTable() 
	{
		dexHeader.fieldIDsOffset = pos;
		ChangeInt(84, pos);
		for (int i = 0; i < dexFieldTable.GetSize(); i++) 
		{
			ShortPrinter(dexFieldTable.GetFieldClassIdx(i));
			ShortPrinter(dexFieldTable.GetFieldTypeIdx(i));
			IntPrinter(dexFieldTable.GetFieldNameIdx(i));
		}
	}

	/**
	 * 输出MethodTable部分到输出缓冲
	 */
	private void PrintMethodTable() 
	{
		dexHeader.methodIDsOffset = pos;
		ChangeInt(92, pos);
		for (int i = 0; i < dexMethodTable.GetSize(); i++) 
		{
			ShortPrinter(dexMethodTable.GetMethodClassIdx(i));
			ShortPrinter(dexMethodTable.GetMethodProtoIdx(i));
			IntPrinter(dexMethodTable.GetMethodNameIdx(i));
		}
	}

	/**
	 * 输出ClassDef部分到输出缓冲
	 */
	private void PrintClassDef() 
	{
		dexHeader.classDefOffset = pos;
		ChangeInt(100, pos);
		for (int i = 0; i < dexClassDef.GetSize(); i++) 
		{
			IntPrinter(dexClassDef.GetClassDefItem(i).classIdx);
			IntPrinter(dexClassDef.GetClassDefItem(i).accessFlag);
			IntPrinter(dexClassDef.GetClassDefItem(i).superClassIdx);
			IntPrinter(dexClassDef.GetClassDefItem(i).interfaceOff);
			IntPrinter(dexClassDef.GetClassDefItem(i).sourceFileIdx);
			IntPrinter(dexClassDef.GetClassDefItem(i).annotationOff);
			dexClassDef.GetClassDefItem(i).SetClassDataOffBackPatchOffset(pos);
			IntPrinter(dexClassDef.GetClassDefItem(i).classDataOff);
			IntPrinter(dexClassDef.GetClassDefItem(i).staticValueOff);
		}
	}

	/**
	 * 输出AnnotationSetItems部分到输出缓冲
	 */
	private void PrintAnnotationSetItems() 
	{
		dataBegin = pos;
		ChangeInt(108, pos);
		//nano版本中此处为空
	}

	/**
	 * 输出CodeItems部分到输出缓冲
	 */
	private void PrintCodeItems() 
	{
		while (pos%4 != 0) 
		{
			BytePrinter((byte)0);
		}
		dexHeader.codeItemOffset = pos;
		
		boolean isfirst = true;
		
		for (int i = 0; i < dexClassDef.GetSize(); i++) 
		{
			for (int j = 0; j < dexClassDef.GetClassDefItem(i).class_data_item.directMethodsSize; j++) 
			{
				if (isfirst) 
				{
					isfirst = false;
				}
				else 
				{
					while (pos%4 != 0) 
					{
						BytePrinter((byte)0);
					}
				}
				codeItemCount++;
				dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).codeOff = pos;
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.registersSize);
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.insSize);
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.outsSize);
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.triedSize);
				IntPrinter(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.debugInfoOff);
				IntPrinter(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.insnsSize);
				for (int k = 0; k < dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.insnsSize*2; k++) 
				{
					BytePrinter(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).dexCode.code[k]);
				}
			}
			for (int j = 0; j < dexClassDef.GetClassDefItem(i).class_data_item.virtualMethodsSize; j++) 
			{
				if (isfirst) 
				{
					isfirst = false;
				}
				else 
				{
					while (pos%4 != 0) 
					{
						BytePrinter((byte)0);
					}
				}
				codeItemCount++;
				dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).codeOff = pos;
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.registersSize);
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.insSize);
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.outsSize);
				ShortPrinter(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.triedSize);
				IntPrinter(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.debugInfoOff);
				IntPrinter(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.insnsSize);
				for (int k = 0; k < dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.insnsSize*2; k++) 
				{
					BytePrinter(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).dexCode.code[k]);
				}
			}
		}
		
	}

	/**
	 * 输出AnnotationsDirectoryItems部分到输出缓冲
	 */
	private void PrintAnnotationsDirectoryItems() 
	{
		//nano版本中此处为空
	}

	/**
	 * 输出ProtoItemTypeLists部分到输出缓冲
	 */
	private void PrintProtoItemTypeLists() 
	{
		while (pos%4 != 0) 
		{
			BytePrinter((byte)0);
		}
		dexHeader.typeListOffset = pos;
		int last = 0;
		for (int i = 0; i < dexProtoTable.GetSize(); i++) 
		{
			if(dexProtoTable.GetProtoParamsNum(i) != 0)
			{
				if (last == 1) 
				{
					ShortPrinter((short)0);
				}
				ChangeInt(dexProtoTable.GetProtoParamBackPatchOffset(i), pos);
				IntPrinter(dexProtoTable.GetProtoParamsNum(i));
				for (int j = 0; j < dexProtoTable.GetProtoParamsNum(i); j++) 
				{
					ShortPrinter(dexProtoTable.GetProtoParamTypeIdx(i, j));
				}
				if (dexProtoTable.GetProtoParamsNum(i) % 2 == 1) 
				{
					last = 1;
				}
				else 
				{
					last = 0;
				}
				typeListCount++;
			}
		}
	}

	/**
	 * 输出StringImages部分到输出缓冲
	 */
	private void PrintStringImages() 
	{
		dexHeader.stringItemOffset = pos;
		for (int i = 0; i < dexStringTable.GetSize(); i++) 
		{
			ChangeInt(dexStringTable.GetBackPatchOff(i), pos);
			StringPrinter(dexStringTable.GetString(i));
		}
	}

	/**
	 * 输出DebugInfo部分到输出缓冲
	 */
	private void PrintDebugInfo() 
	{
		//nano版本中此处为空
	}

	/**
	 * 输出AnnotationOffItems部分到输出缓冲
	 */
	private void PrintAnnotationOffItems() 
	{
		//nano版本中此处为空
	}

	/**
	 * 输出一段神秘代码
	 */
	private void PrintSecrectCode() 
	{
		//while (pos%4 != 0) 
		//{
		//	BytePrinter((byte)0);
		//}
	}
	
	/**
	 * 输出StaticValueItems部分到输出缓冲
	 */
	private void PrintStaticValueItems() 
	{
		//nano版本中此处为空
	}
	
	/**
	 * 输出ClassDataItem部分到输出缓冲
	 */
	private void PrintClassDataItem() 
	{
		dexHeader.classdataItemOffset = pos;
		for (int i = 0; i < dexClassDef.GetSize(); i++) 
		{
			ChangeInt(dexClassDef.GetClassDefItem(i).GetClassDataOffBackPatchOffset(), pos);
			ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.staticFieldsSize);
			ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.instanceFieldsSize);
			ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.directMethodsSize);
			ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethodsSize);
			
			for (int j = 0; j < dexClassDef.GetClassDefItem(i).class_data_item.staticFieldsSize; j++) 
			{
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.staticFields.get(j).fieldIdx);
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.staticFields.get(j).accessFlag);
			}
			for (int j = 0; j < dexClassDef.GetClassDefItem(i).class_data_item.instanceFieldsSize; j++) 
			{
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.instanceFields.get(j).fieldIdx);
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.instanceFields.get(j).accessFlag);
			}
			for (int j = 0; j < dexClassDef.GetClassDefItem(i).class_data_item.directMethodsSize; j++) 
			{
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).methodIdx);
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).accessFlag);
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.directMethods.get(j).codeOff);
			}
			for (int j = 0; j < dexClassDef.GetClassDefItem(i).class_data_item.virtualMethodsSize; j++) 
			{
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).methodIdx);
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).accessFlag);
				ULEB128Printer(dexClassDef.GetClassDefItem(i).class_data_item.virtualMethods.get(j).codeOff);
			}
		}
	}
	
	/**
	 * 输出一段外星人般的代码
	 */
	private void PrintAlien() 
	{
		//while (pos%4 != 0) 
		//{
		//	BytePrinter((byte)0);
		//}
	}
	
	/**
	 * 输出map信息
	 */
	private void PrintMap() 
	{
		while (pos%4 != 0) 
		{
			BytePrinter((byte)0);
		}
		dexHeader.mapOffset = pos;
		ChangeInt(52, pos);
		
		IntPrinter(12);
		
		ShortPrinter((short)0);
		ShortPrinter((short)0);
		IntPrinter(1);
		IntPrinter(0);
		
		ShortPrinter((short)1);
		ShortPrinter((short)0);
		IntPrinter(dexStringTable.GetSize());
		IntPrinter(dexHeader.stringIDsOffset);
		
		ShortPrinter((short)2);
		ShortPrinter((short)0);
		IntPrinter(dexTypeTable.GetSize());
		IntPrinter(dexHeader.typeIDsOffset);
		
		ShortPrinter((short)3);
		ShortPrinter((short)0);
		IntPrinter(dexProtoTable.GetSize());
		IntPrinter(dexHeader.protoIDsOffset);
		
		ShortPrinter((short)4);
		ShortPrinter((short)0);
		IntPrinter(dexFieldTable.GetSize());
		IntPrinter(dexHeader.fieldIDsOffset);
		
		ShortPrinter((short)5);
		ShortPrinter((short)0);
		IntPrinter(dexMethodTable.GetSize());
		IntPrinter(dexHeader.methodIDsOffset);
		
		ShortPrinter((short)6);
		ShortPrinter((short)0);
		IntPrinter(dexClassDef.GetSize());
		IntPrinter(dexHeader.classDefOffset);
		
		ShortPrinter((short)8193);
		ShortPrinter((short)0);
		IntPrinter(codeItemCount);
		IntPrinter(dexHeader.codeItemOffset);
		
		ShortPrinter((short)4097);
		ShortPrinter((short)0);
		IntPrinter(typeListCount);
		IntPrinter(dexHeader.typeListOffset);
		
		ShortPrinter((short)8194);
		ShortPrinter((short)0);
		IntPrinter(dexStringTable.GetSize());
		IntPrinter(dexHeader.stringItemOffset);
		
		ShortPrinter((short)8192);
		ShortPrinter((short)0);
		IntPrinter(dexClassDef.GetSize());
		IntPrinter(dexHeader.classdataItemOffset);
		
		ShortPrinter((short)4096);
		ShortPrinter((short)0);
		IntPrinter(1);
		IntPrinter(dexHeader.mapOffset);
		
		ChangeInt(32, pos);
		ChangeInt(104, pos - dataBegin);
	}
	
	/**
	 * 将缓冲里的信息输出到文件<br/>
	 * 必须手动调用，内部调用
	 */
	private void FlushBuffer() 
	{
		if (Mode.isDebugMode()) System.out.println("---Generated Dex Code---");
		byte[] tmp = new byte[1];
		for (int i = 0; i < outBuffer.size(); i++) 
		{
			tmp[0] = outBuffer.get(i);
			int ans = 255 & outBuffer.get(i);
			if (Mode.isDebugMode()) System.out.print(Integer.toHexString(ans) + " ");
			if (Mode.isDebugMode()) if ((i+1)%8 == 0) {
				System.out.println();
			}
			try {
				target.write(tmp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 工具函数<br/>
	 * 将一个byte输出到文件缓冲区当前位置
	 * @param src 要输出的byte
	 */
	private void BytePrinter(byte src)
	{
		byte[] tmp = new byte[1];
		tmp[0] = src;
		for (int i = 0; i < tmp.length; i++) 
		{
			outBuffer.add(tmp[i]);
			pos += 1;
		}
	}
	
	/**
	 * 工具函数<br/>
	 * 将一个short输出到文件缓冲区当前位置
	 * @param src 要输出的short
	 */
	private void ShortPrinter(short src)
	{
		byte[] tmp = new byte[2];
		tmp[0] = (byte)src;
		tmp[1] = (byte)(src>>>8);
		for (int i = 0; i < tmp.length; i++) 
		{
			outBuffer.add(tmp[i]);
			pos += 1;
		}
	}
	
	/**
	 * 工具函数<br/>
	 * 将一个int输出到文件缓冲区当前位置
	 * @param src 要输出的int
	 */
	private void IntPrinter(int src)
	{
		byte[] tmp = new byte[4];
		tmp[0] = (byte)src;
		tmp[1] = (byte)(src>>>8);
		tmp[2] = (byte)(src>>>16);
		tmp[3] = (byte)(src>>>24);
		for (int i = 0; i < tmp.length; i++) 
		{
			outBuffer.add(tmp[i]);
			pos += 1;
		}
	}
	
	/**
	 * 工具函数<br/>
	 * 修改dex缓冲中，位于offset位置的一个int数值
	 * @param offset 要修改的数的位置
	 * @param src 要修改成的数值
	 */
	private void ChangeInt(int offset, int src)
	{
		byte[] tmp = new byte[4];
		tmp[0] = (byte)src;
		tmp[1] = (byte)(src>>>8);
		tmp[2] = (byte)(src>>>16);
		tmp[3] = (byte)(src>>>24);
		for (int i = 0; i < tmp.length; i++) 
		{
			outBuffer.set(offset+i, tmp[i]);
		}
	}
	
	/**
	 * 工具函数<br/>
	 * 将一个数字以ULEB128的形式输出到文件缓冲当前位置
	 * @param src 要输出的数字值
	 */
	private void ULEB128Printer(int src)
	{
		byte[] tmp = new byte[5];
		int high1one = -128;
		int low7one = 127;
		int tmpsrc = src;
		int size = 0;
		int srclow7 = tmpsrc & low7one;
		
		tmp[0] = (byte)srclow7;
		size++;
		tmpsrc = tmpsrc >>> 7;
		
		while (tmpsrc != 0) 
		{
			tmp[size-1] = (byte) (tmp[size -1] | high1one);
			srclow7 = tmpsrc & low7one;
			tmp[size] = (byte)srclow7;
			size++;
			tmpsrc = tmpsrc >>> 7;
		}
		
		for (int i = 0; i < size; i++) 
		{
			outBuffer.add(tmp[i]);
			pos += 1;
		}
	}
	
	/**
	 * 工具函数<br/>
	 * 将一个字符串，输出到dex文件缓冲中的当前位置
	 * @param src 要输出的字符串
	 */
	private void StringPrinter(String src)
	{
		int size = src.length();
		byte Word;
		ULEB128Printer(size);
		for (int i = 0; i < size; i++) 
		{
			Word = (byte)src.charAt(i);
			BytePrinter(Word);
		}
		BytePrinter((byte)0);
	}
	
	/**
	 * 工具函数<br/>
	 * 将类型名称与实际存储名称进行转换
	 * @param _type 输入的类型名称字符串
	 * @return 实际存储的类型名称字符串
	 */
	private String FixClassType(String _type)
	{
		String retString;
		if (_type.equals("int")) return "I";
		if (_type.equals("boolean")) return "Z";
		if (_type.equals("void")) return "V";
		if (_type.equals("int[]")) return "[I";
		if (_type.equals("String[]")) return "[Ljava/lang/String;";
 		if (_type.length() > 1)
		{
			if (_type.startsWith("[")) 
			{
				if (_type.length() > 2) 
				{
					retString = new String("[L" + _type.substring(1) + ";" );
					return retString;
				}
				retString = _type;
				return retString;
			}
			retString = new String("L" + _type + ";");
			return retString;
		}
 		else
 		{
 			if (!_type.equals("I") && !_type.equals("Z") && !_type.equals("V")) 
 			{
 				retString = new String("L" + _type + ";");
 				return retString;
			}
 		}
		retString = _type;
		return retString;
	}

	/**
	 * 工具函数<br/>
	 * 将前后添加了修饰的类型字符串还原成原来的样子
	 * @param _fixtype 前后进行了修饰的类型字符串
	 * @return 返回原来样子的类型字符串
	 */
	private String DeFixClassType(String _fixtype)
	{
		String retString;
		if (_fixtype.startsWith("L")) 
		{
			retString = new String(_fixtype.substring(1, _fixtype.length()-1));
			return retString;
		}
		if (_fixtype.startsWith("[L")) 
		{
			retString = new String("[" + _fixtype.substring(2, _fixtype.length()-1));
			return retString;
		}
		retString = new String(_fixtype);
		return retString;
	}
	
	/**
	 * 工具函数<br/>
	 * 将包路径的.转化成/，以符合存储要求
	 * @param _src 原始字符串
	 * @return 转化后的额字符串
	 */
	private String FixPoint(String _src)
	{
		return _src.replace('.', '/');
	}

}
