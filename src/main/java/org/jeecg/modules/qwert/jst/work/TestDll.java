package org.jeecg.modules.qwert.jst.work;

import java.io.UnsupportedEncodingException;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

public class TestDll {
    public interface TestDll1 extends Library {
     //   TestDll1 INSTANCE = (TestDll1) Native.loadLibrary("LittleMsgSend", TestDll1.class);

        TestDll1 INSTANCE = (TestDll1)Native.loadLibrary("PhoneDll.dll", TestDll1.class);

        public int Dial(String phones,String alarmMessage);
	//	public int SendSms(int com, int baund,String telNo, String content);
    }

//    public TestDll() {
//
 //   }

    public static void main(String[] args) {
        System.setProperty("jna.encoding", "GBK");
		String str = "中午吃什么？";

     //   int sret = TestDll1.INSTANCE.SendSms(4, 115200,"15699582810", str);
        int sret = TestDll1.INSTANCE.Dial("13898480908", "ZKT8");

        System.out.println("sret=" + sret);
    }
}