package ncnipc.androidgroup;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public static Set<String> uidSet = null;
	public static Set<String> alreadyUidSet = null;
	
	public static String targetUid= "";
	
	public static double errorDistance = 0.003;
	
	public static Set<LocationBean> locationXYSet = new HashSet<LocationBean>();
	
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        
        
        if (lpparam.packageName.contains("duowan"))
        {
        	XposedBridge.log("Loaded app: " + lpparam.packageName);
        	
        	
        	
        	final Class<?> gbpClass = lpparam.classLoader.loadClass("com.yymobile.core.ent.protos.gbp");
        	final Class<?> uint32Class = lpparam.classLoader.loadClass("com.yy.mobile.yyprotocol.core.Uint32");
        	final Class<?> anhClass = lpparam.classLoader.loadClass("com.yymobile.core.foundation.and$anh");
        	final Class<?> angClass = lpparam.classLoader.loadClass("com.yymobile.core.foundation.and$ang");
        	final Class<?> aneClass = lpparam.classLoader.loadClass("com.yymobile.core.foundation.and$ane");
        	final Class<?> anfClass = lpparam.classLoader.loadClass("com.yymobile.core.foundation.and$anf");
        	
        	
        	findAndHookMethod("com.yymobile.core.foundation.ams", lpparam.classLoader, "onReceive", gbpClass, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("ams.onReceive 开始了~");
                    
                    String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    
                    Object arg0 = param.args[0];
                    
                    Method method = gbpClass.getDeclaredMethod("ainz");
                	Object ainz_re = method.invoke(arg0);
                	
                	Field field = anhClass.getField("pwx");
                	Object pwx_value = field.get(anhClass);
                	
                	Method method1 = uint32Class.getDeclaredMethod("equals", Object.class);
                	boolean pwx_equals_re = (boolean) method1.invoke(ainz_re,pwx_value);
                	
                	if(pwx_equals_re == true){
                		Method method2 = gbpClass.getDeclaredMethod("aioa");
                    	Object aioa_re = method2.invoke(arg0);
                    	
                    	Field field1 = angClass.getField("pwr");
                    	Object pwr_value = field1.get(angClass);
                    	
                    	boolean pwr_equals_re = (boolean) method1.invoke(aioa_re,pwr_value);
                    	
                    	if(pwr_equals_re == true)
                    	{
                    		Method method3 = angClass.getDeclaredMethod("pww");
                    		Map pww_map = (Map) method3.invoke(arg0);
                    		
                    		Iterator iter = pww_map.entrySet().iterator();
                    		while (iter.hasNext()) {
                    		Map.Entry entry = (Map.Entry) iter.next();
                    		long uid = (long) entry.getKey();
                    		Object ane_val = entry.getValue();
                    		
                    		Method method4 = aneClass.getDeclaredMethod("toString");
                        	String ane_toString_re = (String) method4.invoke(ane_val);
                        	
                        	if(targetUid != null && targetUid.equals(String.valueOf(uid)))
                            {
                        		LocationBean locationBean = new LocationBean();
                        		locationBean.setUid(targetUid);
                        		
                        		String cooStr = readTxtLine(sdCardPath+"/yyhhzz/location/coordinate.txt");
                                
                                if (cooStr != null && !cooStr.equals("")) {
                                	
                               		int index = cooStr.indexOf(",");
                               		
                               		if(index>=0)
                               		{
                               			String lat = cooStr.substring(0, index);               		
                                   		String lon = cooStr.substring(index+1, cooStr.length());
                                   		
                                   		locationBean.setLatitude(Double.valueOf(lat));
                                   		locationBean.setLongitude(Double.valueOf(lon));
                                   		
                               		}
                                }
                                
                                String distanceDesc = "";
                                int distanceDescIndex = ane_toString_re.indexOf("distanceDesc=");
                                if(distanceDescIndex>=0)
                                {
                                	String subStr1 = ane_toString_re.substring(distanceDescIndex+13);
                                	int distanceDescIndex2 = subStr1.indexOf(",");
                                	if(distanceDescIndex2>=0)
                                	{
                                		distanceDesc = subStr1.substring(0, distanceDescIndex2);
                                	}
                                }
                                
                                
                                int distanceIndex = ane_toString_re.indexOf("distance=");
                                if(distanceIndex>=0)
                                {
                                	String subStr1 = ane_toString_re.substring(distanceIndex+9);
                                	int distanceIndex2 = subStr1.indexOf(",");
                                	if(distanceIndex2>=0)
                                	{
                                		String distance_str = subStr1.substring(0, distanceIndex2);
                                		
                                			
                                		if(!distance_str.equals("0") && !distanceDesc.equals(""))
                                		{
                                			long distance_l = Long.valueOf(distance_str);
                                			double distance = (double)distance_l / (double)1000;
                                			locationBean.setDistance(distance);
                                			
                                			String outputStr = locationBean.getUid()+"---"+locationBean.getLatitude()+","+locationBean.getLongitude()+"---distance: "+distance;
                                			writeLineToFile(outputStr, sdCardPath+"/yyhhzz/location/logOut.txt");
                                			
                                			String errorDistanceStr = readTxtLine(sdCardPath+"/yyhhzz/location/errorDistance.txt");
                                			if(!errorDistanceStr.equals(""))
                                			{
                                				errorDistance = Double.valueOf(errorDistanceStr);
                                			}
                                			
                                			if(distance<=errorDistance){
                                				outputStr = outputStr + ane_toString_re+ "\r\n";
                                    			outputStr = outputStr + locationBean.getUid()+"---"+locationBean.getLatitude()+","+locationBean.getLongitude()+"---distance: "+distanceDesc+"\r\n";
                                				outputStr = outputStr + locationBean.getLongitude()+","+locationBean.getLatitude();
                                				
                                				writeLineToFileByCover(outputStr, sdCardPath+"/yyhhzz/location/resultOutput.txt");
                                				writeLineToFileByCover("", sdCardPath+"/yyhhzz/location/targetUid.txt");
                                        	} else {
                                        		locationXYSet.add(locationBean);
                                    			
                                    			if(locationXYSet.size()>=3)
                                    			{
                                    				
                                    				Coordinate reCoo = MapUtils.beginCalculate(locationXYSet);
                                    				
                                    				double startLat = reCoo.getLatitude(); 
                                    				double startLon = reCoo.getLongitude();
                                    				
                                    				writeLineToFileByCover(startLat+","+startLon, sdCardPath+"/yyhhzz/location/coordinate.txt");
                                    				
                                    				locationXYSet = new HashSet<LocationBean>();
                                    			} else {
                                    				double radiusKM = distance;
                                        			
                                            		if(radiusKM>=100)
                                            			radiusKM=10;
                                            		else if(radiusKM>=10)
                                            			radiusKM=1;
                                            		
                                            		double randomAngle = 0.0;
                                            		randomAngle = 360.0 * Math.random();
                                            		
                                            		Coordinate coordinate =  new Coordinate();
                                            		coordinate = MapUtils.ConvertDistanceToLogLat(radiusKM, locationBean.getLatitude(), locationBean.getLongitude(), randomAngle);
                                            		
                                            		double latT = coordinate.getLatitude();
                                            		double lonT = coordinate.getLongitude();
                                            		
                                            		writeLineToFileByCover(latT+","+lonT, sdCardPath+"/yyhhzz/location/coordinate.txt");
                                            		
                                            		
                                    			}
                                    			
                                    			
                                    			Object anfInstance = anfClass.newInstance();
                                                
                                                List uint32List = new ArrayList();
                                                long uid_long = Long.parseLong("1046531132");
                                            	
                                            	Constructor uint32Constructor = uint32Class.getDeclaredConstructor(long.class);
                                            	Object uint32Instance = uint32Constructor.newInstance(uid_long);
                                            	
                                            	uint32List.add(uint32Instance);
                                            	
                                            	Field pwo_field = anfClass.getDeclaredField("pwo");
                                                pwo_field.setAccessible(true); // 抑制Java对修饰符的检查                   
                                                pwo_field.set(anfInstance, uint32List);
                                                
                                                Map<String,String> pwp_field_map = new HashMap<String,String>();
                                                pwp_field_map.put("latitude", "23.329809");
                                                pwp_field_map.put("longitude", "104.245385");
                                                pwp_field_map.put("country", "中国");
                                                pwp_field_map.put("province", "陕西");
                                                pwp_field_map.put("city", "西安");
                                                pwp_field_map.put("dept", "雁塔区");
                                                
                                                Field pwp_field = anfClass.getDeclaredField("pwp");
                                                pwp_field.setAccessible(true); // 抑制Java对修饰符的检查                   
                                                pwp_field.set(anfInstance, pwp_field_map);
                                                
                                                XposedHelpers.callMethod(param.thisObject, "sendEntRequest",anfInstance);
                                                
                                        	}
                                				
                                			
                                			
                                		} else if(distance_str.equals("0") && !distanceDesc.equals("")){
                                			String outputStr = "";
                                			outputStr = outputStr + ane_toString_re+ "\r\n";
                                			outputStr = outputStr + locationBean.getUid()+"---"+locationBean.getLatitude()+","+locationBean.getLongitude()+"---distance: "+distanceDesc+"\r\n";
                            				outputStr = outputStr + locationBean.getLongitude()+","+locationBean.getLatitude();
                            				writeLineToFileByCover(outputStr, sdCardPath+"/yyhhzz/location/resultOutput.txt");
                            				writeLineToFileByCover("", sdCardPath+"/yyhhzz/location/targetUid.txt");
                                		}
                                		else {
                                			writeLineToFileByCover(ane_toString_re, sdCardPath+"/yyhhzz/location/"+uid+"_error.txt");
                                		}
                                	}
                                	
                                	
                                }
                                
                                
                            }
                        	
                        	
                        	
                        	writeLineToFile(uid + "---" + ane_toString_re, sdCardPath+"/yyhhzz/location/output.txt");
                            writeLineToFile(uid+"", sdCardPath+"/yyhhzz/location/alreadyUidSet.txt");
                            
                            
                            if(uidSet!=null && uidSet.size()>0)
                            {
                            	Object anfInstance = anfClass.newInstance();
                                
                                List uint32List = new ArrayList();
                                long uid_long = Long.parseLong("1046531132");
                            	
                            	Constructor uint32Constructor = uint32Class.getDeclaredConstructor(long.class);
                            	Object uint32Instance = uint32Constructor.newInstance(uid_long);
                            	
                            	uint32List.add(uint32Instance);
                            	
                            	Field pwo_field = anfClass.getDeclaredField("pwo");
                                pwo_field.setAccessible(true); // 抑制Java对修饰符的检查                   
                                pwo_field.set(anfInstance, uint32List);
                                
                                Map<String,String> pwp_field_map = new HashMap<String,String>();
                                pwp_field_map.put("latitude", "23.329809");
                                pwp_field_map.put("longitude", "104.245385");
                                pwp_field_map.put("country", "中国");
                                pwp_field_map.put("province", "陕西");
                                pwp_field_map.put("city", "西安");
                                pwp_field_map.put("dept", "雁塔区");
                                
                                Field pwp_field = anfClass.getDeclaredField("pwp");
                                pwp_field.setAccessible(true); // 抑制Java对修饰符的检查                   
                                pwp_field.set(anfInstance, pwp_field_map);
                                
                                XposedHelpers.callMethod(param.thisObject, "sendEntRequest",anfInstance);
                            }
                        	
                        	
                    		}
                    		
                    	}
                    	
                	}
                	
                	
                    
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("ams.onReceive 结束了~");
                    
                }
            });
        	
        	
        	
        	
        	
        	findAndHookMethod("com.yymobile.core.AbstractBaseCore", lpparam.classLoader, "sendEntRequest", gbpClass, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("AbstractBaseCore.sendEntRequest 开始了~");
                    
                    String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    
                    Object arg0 = param.args[0];
                    
                   
                    Method method1 = gbpClass.getDeclaredMethod("ainz");
                	Object ainz_re = method1.invoke(arg0);
                	
                	Method method2 = gbpClass.getDeclaredMethod("aioa");
                	Object aioa_re = method2.invoke(arg0);
                	
                	Field field1 = anhClass.getField("pwx");
                	Object pwx_value = field1.get(anhClass);
                	
                	Field field2 = anfClass.getField("pwn");
                	Object pwn_value = field2.get(anfClass);
                	
                	Method method_equals = uint32Class.getDeclaredMethod("equals", Object.class);
                	boolean pwx_equals_re = (boolean) method_equals.invoke(ainz_re,pwx_value);
                	boolean pwn_equals_re = (boolean) method_equals.invoke(aioa_re,pwn_value);
                	
                	if(pwx_equals_re && pwn_equals_re)
                	{

                        Field pwo_field = anfClass.getDeclaredField("pwo");
                        pwo_field.setAccessible(true); // 抑制Java对修饰符的检查                   
                        
                        Field pwp_field = anfClass.getDeclaredField("pwp");
                        pwp_field.setAccessible(true); // 抑制Java对修饰符的检查                   
                        Map pwp_field_map = (Map) pwp_field.get(arg0);
                        
//                        targetUid.txt
                        targetUid = readTxtLine(sdCardPath+"/yyhhzz/location/targetUid.txt");
                       
                        
                        List uint32List = new ArrayList();
                        
                        if(targetUid != null && !targetUid.equals(""))
                        {
                        	long uid_long = Long.parseLong(targetUid);
                        	
                        	Constructor uint32Constructor = uint32Class.getDeclaredConstructor(long.class);
                        	Object uint32Instance = uint32Constructor.newInstance(uid_long);
                        	
                        	uint32List.add(uint32Instance);
                        	
                            pwo_field.set(arg0, uint32List);
                        }
                        
                        
                        if(uidSet == null)
                        {
                        	uidSet = readLineList(sdCardPath+"/yyhhzz/location/uidSet.txt");
                        	if(alreadyUidSet == null)
                            {
                            	alreadyUidSet = readLineList(sdCardPath+"/yyhhzz/location/alreadyUidSet.txt");
                            }
                        	uidSet.removeAll(alreadyUidSet);
                        }
                       
                        
                        List uint32List2 = new ArrayList();
                        
                        if(uidSet!=null && uidSet.size()>0)
                        {
                        	for(String uid_str : uidSet)
                            {
                            	long uid_long = Long.parseLong(uid_str);
                            	
                            	Constructor uint32Constructor = uint32Class.getDeclaredConstructor(long.class);
                            	Object uint32Instance = uint32Constructor.newInstance(uid_long);
                            	
                            	uint32List2.add(uint32Instance);
                            	
                            	uidSet.remove(uid_str);
                            	break;
                            }
                            
                            pwo_field.set(arg0, uint32List2);
                        }
                        
                        
                        
                        
                        String cooStr = readTxtLine(sdCardPath+"/yyhhzz/location/coordinate.txt");
                        
                        if (cooStr != null && !cooStr.equals("")) {
                        	
                       		int index = cooStr.indexOf(",");
                       		
                       		if(index>=0)
                       		{
                       			String lat = cooStr.substring(0, index);               		
                           		String lon = cooStr.substring(index+1, cooStr.length());
                           		
                           		pwp_field_map.put("latitude", lat);
                           		pwp_field_map.put("longitude", lon);
                           		
                           		pwp_field.set(arg0, pwp_field_map);
                       		}
                       		
                        }
                        
                     	
                	}
                	
                	
                    
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                	
                    XposedBridge.log("AbstractBaseCore.sendEntRequest 结束了~");
                    
                }
            });
        	
        	
    
        }

        
      
        
    }
    
    
    
    public void writeLineToFileByCover(String line, String file_name) throws Exception{
      BufferedWriter out = null;
      File filename = new File(file_name); 
  	  if (!filename.exists()) { 
  	  filename.createNewFile(); 
  	  }
  	  
  	  out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name, false)));
  	  out.write(line+"\r\n");
  	  out.close();
  	  }
    
   
    
    
    public static String readTxtLine(String filePath){
  	  String txtLine = "";
  	  
  	  File file = new File(filePath);
  	  
  	  if(!file.exists())
  		  return txtLine;
   	  
  	  InputStreamReader read;
 	  BufferedReader bufferedReader;
 	 
 	  try {
 		read = new InputStreamReader(new FileInputStream(file),"GBK");
 		bufferedReader = new BufferedReader(read);
 		txtLine = bufferedReader.readLine();
 		read.close();
 		
 	  } catch (Exception e) {
 		e.printStackTrace();
 	  }
   	
  	  return txtLine;
  	  
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