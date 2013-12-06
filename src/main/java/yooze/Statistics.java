package yooze;

import java.util.concurrent.ConcurrentHashMap;

public class Statistics {
	private static ConcurrentHashMap<String, Long> bytecodeSizes=new ConcurrentHashMap<String, Long>();
	
	public static void addBytecodeSizeForClass(String className, long size){
		bytecodeSizes.put(className, size);
	}
	
	public static Long getByteCodeSizeForClass(String className){
		Long value = bytecodeSizes.get(className);
		return value==null?0:value;
	}
}
