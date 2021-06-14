package org.jeecg.modules.qwert.conn.qudong.base;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.qwert.conn.qudong.QwertFactory;
import org.jeecg.modules.qwert.conn.qudong.QwertMaster;
import org.jetbrains.annotations.Nullable;

public class QudongUtils {
    @Nullable
    public static QwertMaster getQwertMaster(JSONObject jsonConInfo) {
        String type = jsonConInfo.getString("type");
        org.jeecg.modules.qwert.conn.qudong.ip.IpParameters ipParameters = null;
        if(type.equals("MODBUSTCP")) {
            String ipAddress = jsonConInfo.getString("ipAddress");
            String port = jsonConInfo.getString("port");
            ipParameters = new org.jeecg.modules.qwert.conn.qudong.ip.IpParameters();
            ipParameters.setHost(ipAddress);
            ipParameters.setPort(Integer.parseInt(port));
            ipParameters.setEncapsulated(true);
        }
        QwertFactory qwertFactory = new QwertFactory();
        QwertMaster master=null;

        if(type.equals("MODBUSTCP")) {
            master = qwertFactory.createTcpMaster(ipParameters, false);
        }
        return master;
    }
    @Nullable
    public static String getM7000DString(String retmessage, String rm2) {
        String rm=null;
        int pos=0;
        if(rm2.indexOf(".")!=-1){
            String[] rm3 = rm2.split("\\.");
            pos=7-Integer.parseInt(rm3[1]);
            rm=retmessage.substring(pos,pos+1);
        }else{
            rm=retmessage.substring(7,8);
        }
        return rm;
    }

}
