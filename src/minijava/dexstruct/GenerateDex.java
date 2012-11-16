package minijava.dexstruct;

import java.io.IOException;

import util.Mode;

import minijava.apkbuilder.ApkBuilderEntrance;
/**
 * Generate dex file and apk file
 * @author ZongZiWang
 *
 */
public class GenerateDex {
	/**
	 * generate apk file from dex file, which is the output of source file 'filename'
	 * @param fileName
	 * @param mainclassname
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void generate(String fileName, String mainclassname) throws IOException, InterruptedException {
		Runtime rt = Runtime.getRuntime();

		System.out.println("//You should type the following command in the System Terminal");
		System.out.println("$your_android_sdk_directory/platform-tools/adb install -r "+fileName.substring(0, fileName.lastIndexOf("/")+1)+mainclassname+".apk");
		
		//insert the dex file into the template zip by using apkbuilder
		String[] args = new String[5];
		args[0] = new String(fileName.substring(0, fileName.lastIndexOf("/")+1)+mainclassname+".apk");
		args[1] = new String("-z");
		args[2] = new String("factory/template/MiniJavaOutputTextViewWithout.zip");
		args[3] = new String("-f");
		args[4] = new String("classes.dex");
		ApkBuilderEntrance.MakeApk(args);
		
		//remove template dex file
		rt.exec("rm classes.dex");
		
		if (Mode.isDebugMode()) System.out.println("===Generate Test APK by classes.dex Finished===");
		
	}

}
