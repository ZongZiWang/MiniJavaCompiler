package minijava.compiler;

import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;

import util.Mode;
import util.Optimize;

import main.*;
import minijava.apkbuilder.ApkBuilderEntrance;
import minijava.binary.BinaryCode;
import minijava.informations.BuildInformation;
import minijava.intermediate.IntermediateCode;
import minijava.intermediate.RegCheck;
import minijava.intermediate.RegLabelLock;
import minijava.intermediate.TargetCode;
import minijava.typecheck.TypeCheck;
import minijava.typecheck.symboltable.GlobalTable;

public class MiniJavaCompiler {
	
	private final static String LOG_TAG = "minijava compiler";
	
	String javaPath;
	String dexPath;
	String apkPath;
	public MiniJavaCompiler(String src) {
		javaPath = src;
		dexPath = javaPath.replace(".java", ".dex");
		apkPath = javaPath.replace(".java", ".apk");
	}
	public void compile() throws ParseException, IOException, InterruptedException {
		
		/* Optimizing Level
		 * -1 do nothing include correctness check
		 * 0 default but correctness check
		 * 1 optimizing during generating byte code
		 * 2 several optimizing module (eg. registers allocation)
		 * 3 all optimizing module 
		 */
		Optimize.setLevel(3);
		// Source File Name
		String fileName = javaPath;
		// Debug Mode ?
		//Mode.SetDebugMode();
		
		TypeCheck.init();
		TargetCode.init();
		
		if (Mode.isDebugMode()) {
			Log.i(LOG_TAG, "===Source Code===");
			FileInputStream fis = new FileInputStream(fileName);
			byte[] bbuf = new byte[4096];
			int hasRead = 0;
			while ((hasRead = fis.read(bbuf))>0) {
				Log.i(LOG_TAG, new String(bbuf, 0, hasRead));
			}
		}
		
		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Type Check===");
		
		GlobalTable testGlobalTable = TypeCheck.typeCheck(fileName);
		if (testGlobalTable == null) {
			Log.i(LOG_TAG, "===Type Check Error!===");
			return ;
		}
		
		BuildInformation.Build(fileName, testGlobalTable);
		IntermediateCode.JavaToAsm(fileName, testGlobalTable);
		//BinaryCode.AsmToBin(fileName, testGlobalTable);
		
		RegLabelLock.RegLockAll();
		RegLabelLock.LabelLockAll();
		boolean changed = true;
		int optimizedCnt = 0;
		while (changed && optimizedCnt < 10) {
			optimizedCnt++;
			changed = false;
			if (Optimize.isO2()) changed = TargetCode.reallocateRegister() || changed;
			if (Optimize.isO3()) changed = TargetCode.removeUnusedDefinition() || changed;
			if (Optimize.isO3()) changed = TargetCode.optimizeStaticSingleAssignments() || changed;
			if (Optimize.isO3()) changed = TargetCode.optimizeLoops() || changed;
		}
		RegLabelLock.LabelUnlockAll();
		RegLabelLock.RegUnlockAll();

		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Check Registers OverFlow===");
		
		RegLabelLock.LabelLockAll();
		//Correctness Check
		if (Optimize.isO0()) while (RegCheck.hasRegOverFlowAll()) ;
		RegLabelLock.LabelUnlockAll();
		
		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Check Registers OverFlow Finished===");
		
		RegLabelLock.RegLockAll();
		//Make the registers number continuous
		if (Optimize.isO0()) while (TargetCode.refreshRegistersAll()) ;
		RegLabelLock.RegUnlockAll();

/*		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Check Registers OverFlow===");
		
		RegLabelLock.LabelLockAll();
		//Correctness Check
		if (Optimize.isO3()) TargetCode.removeUnusedDefinition();
		if (Optimize.isO3()) TargetCode.optimizeStaticSingleAssignments();
		if (Optimize.isO0()) while (RegCheck.hasRegOverFlowAll()) ;
		RegLabelLock.LabelUnlockAll();

		RegLabelLock.RegLockAll();
		//Make the registers number continuous
		if (Optimize.isO0()) while (TargetCode.refreshRegistersAll()) ;
		RegLabelLock.RegUnlockAll();
		
		TargetCode.showAllCode();
		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Check Registers OverFlow Finished===");
		
		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Reallocate Registers===");
		RegLabelLock.RegLockAll();
		RegLabelLock.LabelLockAll();
		if (Optimize.isO2()) TargetCode.reallocateRegister();
		if (Optimize.isO3()) TargetCode.optimizeLoops();
		RegLabelLock.LabelUnlockAll();
		RegLabelLock.RegUnlockAll();
		
		TargetCode.showAllCode();
*/		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Reallocate Finished===");
		
		if (Mode.isDebugMode()) Log.i(LOG_TAG, "===Generating Dex Code===");
		
		BinaryCode.AsmToBin(fileName, testGlobalTable);
		testGlobalTable.globalTable.dexPrinter.PrintDex();

		String targetApkName = testGlobalTable.getMainClassName()+".apk";
		
		String[] args = new String[] {
				targetApkName,
				"-z",
				"template/MiniJavaOutputTextViewWithout.zip",
				"-f",
				"classes.dex"
		};
		ApkBuilderEntrance.MakeApk(args);
		
		//GenerateDex.generate(fileName, testGlobalTable.getMainClassName());
	}
	
	/*
	public void WriteSignature(Context context)
	{
	 // all of this is fairly well documented
	 // if it doesn't work, just search around.
	  String packageName = "minijava.output";
		
	 PackageManager pm = context.getPackageManager();
	 PackageInfo pi = null;
	 try {
	  pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
	 } catch (NameNotFoundException e1) {
	  e1.printStackTrace();
	 }
	 Signature[] s = pi.signatures;
	  
	 // you can use toChars or get the hashcode, whatever
	 String sig = new String(s[0].toChars());
	 
	 try {
	  File root = Environment.getExternalStorageDirectory();
	  if ( root.canWrite() )
	  {
	   // toChars is long, so i write it to a file on the external storage
	   File f = new File(root, "signature.txt");
	   FileWriter fw = new FileWriter(f);
	   BufferedWriter out = new BufferedWriter(fw);
	   out.write(packageName + "\nSignature: " + sig);
	   out.close();
	   fw.close();
	  }
	 } catch (IOException e) {
	  e.printStackTrace();
	 }
	}

	 
	 private static final Object mSync = new Object();
	 private static WeakReference<byte[]> mReadBuffer;
	 
	 public static void main(String[] args) {
		 if (args.length < 1) {
			 System.out.println("Usage: java -jar GetAndroidSig.jar <apk/jar>");
			 System.exit(-1);
		 }
	 
		 System.out.println(args[0]);
	 
		 String mArchiveSourcePath = args[0];
	 
		 WeakReference<byte[]> readBufferRef;
		 byte[] readBuffer = null;
		 synchronized (mSync) {
			 readBufferRef = mReadBuffer;
			 if (readBufferRef != null) {
				 mReadBuffer = null;
				 readBuffer = readBufferRef.get();
			 }
			 if (readBuffer == null) {
				 readBuffer = new byte[8192];
				 readBufferRef = new WeakReference<byte[]>(readBuffer);
			 }
		 }
	 
		 try {
			 JarFile jarFile = new JarFile(mArchiveSourcePath);
			 java.security.cert.Certificate[] certs = null;
	 
			 Enumeration<?> entries = jarFile.entries();
			 while (entries.hasMoreElements()) {
				 JarEntry je = (JarEntry) entries.nextElement();
				 if (je.isDirectory()) {
					 continue;
				 }
				 if (je.getName().startsWith("META-INF/")) {
					 continue;
				 }
				 java.security.cert.Certificate[] localCerts = loadCertificates(jarFile, je, readBuffer);
				 if (Mode.isDebugMode()) {
					 System.out.println("File " + mArchiveSourcePath + " entry " + je.getName()
							 + ": certs=" + certs + " ("
							 + (certs != null ? certs.length : 0) + ")");
				 }
				 if (localCerts == null) {
					 System.err.println("Package has no certificates at entry "
							 + je.getName() + "; ignoring!");
					 jarFile.close();
					 return;
				 } else if (certs == null) {
					 certs = localCerts;
				 } else {
					 // Ensure all certificates match.
					 for (int i = 0; i < certs.length; i++) {
						 boolean found = false;
						 for (int j = 0; j < localCerts.length; j++) {
							 if (certs[i] != null
									 && certs[i].equals(localCerts[j])) {
								 found = true;
								 break;
							 }
						 }
						 if (!found || certs.length != localCerts.length) {
							 System.err.println("Package has mismatched certificates at entry "
									 + je.getName() + "; ignoring!");
							 jarFile.close();
							 return; // false
						 }
					 }
				 }
			 }
	 
			 jarFile.close();
	 
			 synchronized (mSync) {
				 mReadBuffer = readBufferRef;
			 }
	 
			 if (certs != null && certs.length > 0) {
				 final int N = certs.length;
	     
				 for (int i = 0; i < N; i++) {
					 String charSig = new String(toChars(certs[i].getEncoded()));
					 System.out.println("Cert#: " + i + "  Type:" + certs[i].getType()
							 + "\nPublic key: " + certs[i].getPublicKey()
							 + "\nHash code: " + certs[i].hashCode()
							 + " / 0x" + Integer.toHexString(certs[i].hashCode())
							 + "\nTo char: " + charSig);
				 }	
			 } else {
				 System.err.println("Package has no certificates; ignoring!");
				 return;
			 }
		 } catch (CertificateEncodingException ex) {
			 System.err.println("Exception "+ex);
		 } catch (IOException e) {
			 System.err.println("Exception reading " + mArchiveSourcePath + "\n" + e);
			 return;
		 } catch (RuntimeException e) {
			 System.err.println("Exception reading " + mArchiveSourcePath + "\n" + e);
			 return;
		 }
	 }
	 
	 private static char[] toChars(byte[] mSignature) {
	    byte[] sig = mSignature;
	    final int N = sig.length;
	    final int N2 = N*2;
	    char[] text = new char[N2];
	 
	    for (int j=0; j<N; j++) {
	      byte v = sig[j];
	      int d = (v>>4)&0xf;
	      text[j*2] = (char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
	      d = v&0xf;
	      text[j*2+1] = (char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
	    }
	 
	    return text;
	    }
	 
	 private static java.security.cert.Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
		 try {
		   // We must read the stream for the JarEntry to retrieve
		   // its certificates.
			 InputStream is = jarFile.getInputStream(je);
			 while (is.read(readBuffer, 0, readBuffer.length) != -1) {
				 // not using
			 }
			 is.close();
	 
			 return (java.security.cert.Certificate[]) (je != null ? je.getCertificates() : null);
		 } catch (IOException e) {
			 System.err.println("Exception reading " + je.getName() + " in "
					 + jarFile.getName() + ": " + e);
		 }
		 return null;
	 }
	 
	 */
}
