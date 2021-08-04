package org.jeecg.modules.qwert.jst.work;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

public interface TestDll1 extends Library{
   // TestDll1 INSTANCE = (TestDll1) Native.loadLibrary("LittleMsgSend", TestDll1.class);
    TestDll1 INSTANCE = (TestDll1)Native.loadLibrary("PhoneDll.dll", TestDll1.class);
    public int Dial(String phones,String alarmMessage,String ext);
    //public int SendSms(int com, int baund,String telNo, String content);

}
