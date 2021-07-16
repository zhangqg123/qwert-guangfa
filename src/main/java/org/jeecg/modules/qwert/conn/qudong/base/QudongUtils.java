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
    public static String getPmBus(byte[] retmessage, String rm2) {
        String rm = null;
        if(rm2.indexOf("_")!=-1){
            String[] rm3=rm2.split("_");
            byte trm = retmessage[Integer.parseInt(rm3[0])] ;
            rm=(trm & 0xff)+"";
        }
        if(rm2.indexOf("$")!=-1){
            String[] rm3=rm2.split("\\$");
            rm=retmessage[Integer.parseInt(rm3[0])-1]+"";
        }
        return rm;
    }
    public static String getKstarString(String retmessage, String rm2) {
        String rm = null;
        if(rm2.indexOf("_")!=-1) {
            String[] rm3 = rm2.split("_");
            rm = retmessage.substring(Integer.parseInt(rm3[0]) - 1, Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1]) -1);
        }
        if(rm2.indexOf("$")!=-1) {
            String[] rm3 = rm2.split("\\$");
            int rm4 = Integer.parseInt(rm3[0]) - 1;
            int rm5 = Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1]);
            rm = retmessage.substring(Integer.parseInt(rm3[0]) - 1, Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1])-1);
        }
        return rm;
    }

    public static String getDeltaString(String retmessage, String rm2) {
        String retValue="0";
        int rn = rm2.lastIndexOf(",");
        String rm3 = rm2.substring(rn + 1);
        String rm4[] = rm3.split("\\)");
        String rm5 = rm4[0];
        String[] rm6 = retmessage.split(";");
        String r7 = rm6[Integer.parseInt(rm5)];
        if (rm2.indexOf("=") == -1) {
            retValue= r7;
        }else{
            String[] r8 = rm2.split("=");
            if(r7.equals(r8[1])){
                retValue= r7;
            }
        }
        return retValue;
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
