package com.jadyer.util;

import org.jeecg.modules.qwert.conn.modbus4j.source.sero.util.queue.ByteQueue;

import java.util.Map;

import static com.jadyer.util.TCPUtil.sendTCPRequest;

public class TestSocket {
    public static void main(String[] args) {
    //    String reqData = "";
        String reqString= "$016\r"; //7000d
        String IP = "127.0.0.1";
        String port = "502";
  //      byte[] reqData=new byte[]{1,4,0,2,0,1,-112,10};
        byte[] reqData=reqString.toUpperCase().getBytes();
        ByteQueue resp = sendTCPRequest(IP, port, reqData);
        System.out.println("=============================================================================");
        System.out.println("请求报文如下");
        System.out.println("=============================================================================");
    }
}
