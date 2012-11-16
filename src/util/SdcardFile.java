package util;

import java.io.File;

public class SdcardFile extends File {
	private static final long serialVersionUID = 1016661816971385396L;

	public SdcardFile(String path) {
		super(SdcardManager.ROOT_PATH, path);
	}
}