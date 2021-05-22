package org.jeecg.modules.qwert.conn.dianzon.test;

import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

public class DCHKSUM {
	//二进制补齐16位方法
	public static String addZeroForNum(String str, int strLength) {
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左补0
	            // sb.append(str).append("0");//右补0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}

	public static String chkDrc(String putIn){
		char[] c = putIn.toCharArray();
		int plus = 0;
		int remainder;
		String bin;
		String coverBin;
		String notBin;
		int dec;
		
		//将输入的值求和
		for (int i = 0; i < c.length; i++) {
			plus += c[i];
		}
		//求和的值模65536后余
		remainder = plus % 65536;
		int a1 = ~remainder+1;
		return Integer.toHexString(a1);
	}
    public static byte[] toBytes(String s) throws Exception {
        return s.getBytes("ASCII");
    }

	public static String chkDrc2(String putIn) throws Exception{
        ByteQueue queue = new ByteQueue(toBytes(putIn));
	//	char[] ca = putIn.toCharArray();
		int plus = 0;
		int remainder;
		
		//将输入的值求和
//		for (int i = 0; i < ca.length; i++) {
//			plus += ca[i];
//		}
		for (int i = 0; i < queue.size(); i++) {
			plus += queue.peek(i);
		}
		//求和的值模65536后余
		remainder = plus % 65536;
		int a1 = ~remainder+1;
		return Integer.toHexString(a1);
	}
	
	public static String chkLength(int value){
		byte a1 = (byte) (value & 0xf);
		byte a2 = (byte) ((value>>4) & 0xf);
		byte a3 = (byte) ((value>>8) & 0xf);
		int sum = a1+a2+a3;
		sum=((~sum%0x10000+1)& 0xf)<<12 | (value&0xffff);
		return Integer.toHexString(sum);
	}
	
	public static void main(String[] args) {
		char[] c = new char[16];
//		String putIn = "1203400456ABCDFE";
//		String putIn = "20014043E00200";
		String putIn = "210360470000";
//		String putIn = "21036000d03000d201f40014003200000000012c0096032000c800e60078";
		byte[] aa = putIn.toUpperCase().getBytes();
		System.out.println(aa);
		
		String ret = null;
		try {
			ret = chkDrc2(putIn.toUpperCase());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	byte i = (byte) (0xff & (value >> 8));
	//	String ret = chkLength(48);
		System.out.println(ret);
	}
	
}

