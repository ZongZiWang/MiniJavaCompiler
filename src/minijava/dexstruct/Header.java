package minijava.dexstruct;

/**
 * Header段的数据存储结构
 */
public class Header {
	public char[] magicCode;
	//public byte[] checksum;
	//public byte[] sha1;
	//public int fileLength;
	//public int headerLength;
	public byte[] endianTag;
	//public int linkSize;
	//public int linkOffset;
	public int mapOffset;
	//public int stringIDsSize;
	public int stringIDsOffset;
	//public int typeIDsSize;
	public int typeIDsOffset;
	//public int protoIDsSize;
	public int protoIDsOffset;
	//public int fieldIDsSize;
	public int fieldIDsOffset;
	//public int methodIDsSize;
	public int methodIDsOffset;
	//public int classDefSize;
	public int classDefOffset;
	//public int dataSize;
	//public int dataOffset;
	public int codeItemOffset;
	public int typeListOffset;
	public int stringItemOffset;
	public int classdataItemOffset;
	
	
	/**
	 * 构造函数
	 */
	public Header()
	{
		magicCode = new char[8];
		magicCode[0] = 100;
		magicCode[1] = 101;
		magicCode[2] = 120;
		magicCode[3] = 10;
		magicCode[4] = 48;
		magicCode[5] = 51;
		magicCode[6] = 53;
		magicCode[7] = 0;
		
		//checksum = new byte[4];
		
		//sha1 = new byte[20];
		
		endianTag = new byte[4];
		endianTag[0] = 120;
		endianTag[1] = 86;
		endianTag[2] = 52;
		endianTag[3] = 18;
	}
	
}