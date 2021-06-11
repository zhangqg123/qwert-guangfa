package com.jadyer.util;

import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private final InputStream in;

    public SocketClient(InputStream in) {
        this.in = in;
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            // 和服务器创建连接
            Socket socket = new Socket("72.26.250.11",10006);

            // 要发送给服务器的信息
            OutputStream os = socket.getOutputStream();
        //    PrintWriter pw = new PrintWriter(os);
        //    pw.write("客户端发送信息");
        //    pw.flush();
    		String putIn= "$016\r"; //7000d [1,4,0,2,0,1,90,a]
	    //	byte[] aa = putIn.toUpperCase().getBytes();
      //      String a = "a";
	        byte[] aa=new byte[]{1,4,0,2,0,1,-112,10};
            os.write(aa);
            os.flush();
            socket.shutdownOutput();

            // 从服务器接收的信息

            InputStream is = socket.getInputStream();
            byte[] buf = new byte[1024];
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            String info = null;
//            while((info = br.readLine())!=null){
//                System.out.println("我是客户端，服务器返回信息："+info);
//            }
            BufferedInputStream input = new BufferedInputStream(is);
            int bytesRead=0;
            while (bytesRead==0) {
                // TODO: Rename btrar to something more meaningful
                bytesRead = input.read(buf);
                // Do something with the data...
            }
            ByteQueue dataBuffer = new ByteQueue();
            dataBuffer.push(buf, 0, bytesRead);

            input.close();
            is.close();
            os.close();
//            pw.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}