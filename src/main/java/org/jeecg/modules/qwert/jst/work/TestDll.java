package org.jeecg.modules.qwert.jst.work;

import java.io.UnsupportedEncodingException;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
//import org.jeecg.modules.qwert.jst.utils.VoiceUtils;

public class TestDll {
    public interface TestDll1 extends Library {
     //   TestDll1 INSTANCE = (TestDll1) Native.loadLibrary("LittleMsgSend", TestDll1.class);

        TestDll1 INSTANCE = (TestDll1)Native.loadLibrary("PhoneDll.dll", TestDll1.class);

        public int Dial(String phones,String alarmMessage,String ext);
	//	public int SendSms(int com, int baund,String telNo, String content);
    }

//    public TestDll() {
//
 //   }

    public static void main(String[] args) {
/*    //    System.setProperty("jna.encoding", "GBK");
		String str = "空调2KT8报警";
        String voiceName = "voice12";
        VoiceUtils.textToSpeech(str,voiceName);
     //   int sret = TestDll1.INSTANCE.SendSms(4, 115200,"15699582810", str);
        int sret = TestDll1.INSTANCE.Dial("13898480908", voiceName,"9,");

        System.out.println("sret=" + sret);
*/
        String s = "0000000000000100";
        int pos = 15 - Integer.valueOf(2);
        String rm = s.substring(pos, pos + 1);
        System.out.println(rm);
    }
}