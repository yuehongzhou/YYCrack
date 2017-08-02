package ncnipc.androidgroup;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.os.Environment;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
public class HookSystemAPI implements IXposedHookLoadPackage {

    /**
     * 包加载时候的回调
     */
	
	public Set<String> uidSet = null;
	public Set<String> alreadyUidSet = null;
	
	public static int testUid = 0;
	
	
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        
        
        if (lpparam.packageName.contains("duowan"))
        {
        	XposedBridge.log("Loaded app: " + lpparam.packageName);
        	
        	
        	
        	findAndHookMethod("com.yyproto.jni.YYSdk", lpparam.classLoader, "sendRequest", int.class, int.class, byte[].class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("YYSdk.sendRequest 开始了~");
                    
                    String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    
                	int arg0 = (int) param.args[0];
                	int arg1 = (int) param.args[1];
                	
                	if(arg0 == 0 && arg1 ==23)
                	{
                		if(testUid == 0)
                		{
                			uidSet = readLineList(sdCardPath+"/yyhhzz/gonghui/source/uidSet.txt");
                            alreadyUidSet = readLineList(sdCardPath+"/yyhhzz/gonghui/source/alreadyUidSet.txt");
                            
                            for(String uid_str : uidSet)
                            {
                            	if(!alreadyUidSet.contains(uid_str))
                            	{
                            		int uid_int = Integer.parseInt(uid_str);
                            		testUid = uid_int;
                            		
                            		
                            	}
                            	
                            }
                		}
                		
                        if(testUid != Integer.MAX_VALUE && testUid != 0)
                        {
                        	byte[] bytetmp = new byte[] { (byte) 0x10,
									(byte) 0x00, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0xc8, (byte) 0x00 };

							byte[] bb = intToBytes(testUid);

							byte[] bbx = byteMerger(bytetmp, bb);

							bytetmp = new byte[] { (byte) 0x00, (byte) 0x00 };

							byte[] bby = byteMerger(bbx, bytetmp);

							writeLineToFile(String.valueOf(testUid), sdCardPath+"/yyhhzz/gonghui/source/alreadyUidSet.txt");

							param.args[2] = bby;
                        }

						
                	}
                	
                	
                    
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("YYSdk.sendRequest 结束了~");
                    
                }
            });
        	
        	
        	
        	 
        	 
        	findAndHookMethod("com.yyproto.jni.YYSdk", lpparam.classLoader, "onEvent", int.class, int.class, byte[].class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("YYSdk.onEvent 开始了~");
                    
                    
                    String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
               	 
                	int arg0 = (int) param.args[0];
                	int arg1 = (int) param.args[1];
                	byte[] arg2 = (byte[])param.args[2];
                	
                	if(arg0 == 0 && arg1 == 12)
                	{
                		if(testUid != Integer.MAX_VALUE && testUid != 0)
                        {
                			
                		byte[] subByteData = new byte[4];  
                        System.arraycopy(arg2, 16, subByteData, 0, 4);
                        int uid = getInt(subByteData, true, 4);
                        
                        String file_name = sdCardPath+"/yyhhzz/gonghui/result/"+uid+".txt"; 
                    	
                        FileOutputStream fos = new FileOutputStream(file_name);  
                        fos.write(arg2);  
                        fos.close();
                        
                        
                        
                        	uidSet = readLineList(sdCardPath+"/yyhhzz/gonghui/source/uidSet.txt");
                            alreadyUidSet = readLineList(sdCardPath+"/yyhhzz/gonghui/source/alreadyUidSet.txt");
                            
                            int finishedUidSignal = 0;
                            
                            for(String uid_str : uidSet)
                            {
                            	if(!alreadyUidSet.contains(uid_str))
                            	{
                            		finishedUidSignal = 1;
                            		
                            		int uid_int = Integer.parseInt(uid_str);
                            		testUid = uid_int;
                            		
                            		byte[] bytetmp = new byte[] { (byte) 0x00};
                                    
                                    XposedHelpers.callMethod(param.thisObject, "sendRequest",0,23,bytetmp);
                            		
                            		break;
                            	}
                            	
                            }
                            
                            if(finishedUidSignal == 0)
                            {
                            	testUid = Integer.MAX_VALUE;
                            }
                        }
                        
                        
                	}
                	
                	
                	
                    
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("YYSdk.onEvent 结束了~");
                    
                }
            });
        	 
        	 
        	 
    
    
        }

        
      
        
    }
    
    
    public static void writeLineToFile(String line, String file_name) throws Exception{
  	  File filename = new File(file_name); 
  	  
  	  if (!filename.exists()) {
  		  
  		  if(!filename.getParentFile().exists()) {
  	            //如果目标文件所在的目录不存在，则创建父目录
  	            System.out.println("目标文件所在目录不存在，准备创建它！");
  	            if(!filename.getParentFile().mkdirs()) {
  	                System.out.println("创建目标文件所在目录失败！");
  	            }
  	        }  
  		  
  	  filename.createNewFile(); 
  	  }
  	  
  	  BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name, true)));  //以追加的方式写入到指定的文件
  	  out.write(line+"\r\n");
  	  out.close();
  	  }
    
    
    /* int -> byte[] */  
    public static byte[] intToBytes(int num) {  
       byte[] b = new byte[4];  
       for (int i = 0; i < 4; i++) {  
        b[3-i] = (byte) (num >>> (24 - i * 8));  
       }  
        
       return b;  
    }  
    
    
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }
    
    
    
    public static Set<String> readLineList(String filePath) throws Exception{
	    Set<String> fileSet = new HashSet<String>();
	    
	    File filename = new File(filePath); 
		  if (!filename.exists()) { 
		  return fileSet; 
		  }
	    
        try {
                String encoding="GBK";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                                 
                    while((lineTxt = bufferedReader.readLine()) != null){
//                        System.out.println(lineTxt);
                        fileSet.add(lineTxt);
                        
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
		return fileSet;
     
    
	  }
    
    
    public final static int getInt(byte[] buf, boolean asc, int len) {  
        if (buf == null) {  
          throw new IllegalArgumentException("byte array is null!");  
        }  
        if (len > 4) {  
          throw new IllegalArgumentException("byte array size > 4 !");  
        }  
        int r = 0;  
        if (asc)  
          for (int i = len - 1; i >= 0; i--) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        else  
          for (int i = 0; i < len; i++) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        return r;  
    } 
    
}