package minijava.dexstruct;

/**
 * ULEB128操作的包装类
 */
public class UnsignedLEB128Package {
	private int result;
	private int nextOffset;
	
	UnsignedLEB128Package(int _result,int _nextOffset)
	{
		result = _result;
		nextOffset = _nextOffset;
	}
	
	/**
	 * 获取当前扫描到的ULEB128数值
	 * @return 当前扫描到的ULEB128数值
	 */
	public int Result()
	{
		return result;
	}
	
	/**
	 * 获取下一个ULEB128数的起始offset
	 * @return 下一个ULEB128数的起始offset
	 */
	public int NextOffset()
	{
		return nextOffset;
	}
}
