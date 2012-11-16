package minijava.dexstruct.items;

/**
 * CodeItem的数据结构
 */
public class CodeItem
{
	public short registersSize;
	public short insSize;
	public short outsSize;
	public short triedSize = 0;
	public int debugInfoOff = 0;
	public int insnsSize;
	public byte[] code;
	/**
	 * 构造函数，需要传入信息生成
	 * @param _registersSize 使用寄存器数量
	 * @param _insSize 使用的入口寄存器数量
	 * @param _outsSize 调用使用的寄存器数量
	 * @param _insnsSize 字节码长度（以2Byte计）
	 * @param _code 字节码数组
	 */
	public CodeItem(short _registersSize, short _insSize, short _outsSize, int _insnsSize, byte[] _code)
	{
		registersSize = _registersSize;
		insSize = _insSize;
		outsSize = _outsSize;
		insnsSize = _insnsSize;
		code = new byte[insnsSize*2];
		for (int i = 0; i < _code.length; i++) 
		{
			code[i] = _code[i];
		}
	}
}