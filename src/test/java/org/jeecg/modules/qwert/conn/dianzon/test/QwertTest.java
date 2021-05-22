package org.jeecg.modules.qwert.conn.dianzon.test;

import java.util.Arrays;

import org.jeecg.modules.qwert.conn.dianzong.BatchRead;
import org.jeecg.modules.qwert.conn.dianzong.BatchResults;
import org.jeecg.modules.qwert.conn.dianzong.QwertFactory;
import org.jeecg.modules.qwert.conn.dianzong.QwertMaster;
import org.jeecg.modules.qwert.conn.dianzong.code.DataType;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.locator.BaseLocator;
import org.jeecg.modules.qwert.conn.dianzong.msg.ReadDianzongRequest;
import org.jeecg.modules.qwert.conn.dianzong.msg.ReadDianzongResponse;
//import org.jeecg.modules.qwert.conn.dianzong.msg.ReadInputRegistersRequest;
//import org.jeecg.modules.qwert.conn.dianzong.msg.ReadInputRegistersResponse;
import org.jeecg.modules.qwert.conn.dianzong.msg.ReportSlaveIdRequest;
import org.jeecg.modules.qwert.conn.dianzong.msg.ReportSlaveIdResponse;
import org.jeecg.modules.qwert.conn.dianzong.serial.QwertSerialPortWrapper;

public class QwertTest {
    
    public static void main(String[] args) throws Exception {

    	String commPortId = "COM1";
    	int baudRate = 9600;
    	int flowControlIn = 0;
		int flowControlOut = 0; 
		int dataBits = 8;
		int stopBits = 1;
		int parity = 0;
    	
    	QwertSerialPortWrapper wrapper = new QwertSerialPortWrapper(commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity);
        QwertFactory QwertFactory = new QwertFactory();
        QwertMaster master = QwertFactory.createDianzongMaster(wrapper);
 
        try {
            master.init();
            int slaveId = 3;
            float ver=2.1f;
            int cid1 = 60;
            int cid2 = 47;
            int lenid = 0;
            readDianzongTest(master, ver,slaveId, cid1,cid2,0);

 /*           BatchRead<String> batch = new BatchRead<String>();
			batch.addLocator("10",	BaseLocator.holdingRegister(slaveId, 10, DataType.TWO_BYTE_INT_SIGNED));
			batch.addLocator("21",	BaseLocator.holdingRegister(slaveId, 21, DataType.FOUR_BYTE_FLOAT));
			BatchResults<String> results = master.send(batch);
			System.out.println("::"+results);
			String res = results.toString();*/
            
        }
        finally {
            master.destroy();
        }
    }



    public static void readDianzongTest(QwertMaster master,float ver, int slaveId, int cid1, int cid2,int lenid) {
        try {
            ReadDianzongRequest request = new ReadDianzongRequest(ver,slaveId, cid1, cid2,lenid);
            ReadDianzongResponse response = (ReadDianzongResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getShortData()));
        }
        catch (DianzongTransportException e) {
            e.printStackTrace();
        }
    }
/*
    public static void readInputRegistersTest(QwertMaster master, int slaveId, int start, int len) {
        try {
            ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, start, len);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getShortData()));
        }
        catch (DianzongTransportException e) {
            e.printStackTrace();
        }
    }
*/



    public static void reportSlaveIdTest(QwertMaster master, int slaveId) {
        try {
            ReportSlaveIdRequest request = new ReportSlaveIdRequest(slaveId);
            ReportSlaveIdResponse response = (ReportSlaveIdResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getData()));
        }
        catch (DianzongTransportException e) {
            e.printStackTrace();
        }
    }

}
