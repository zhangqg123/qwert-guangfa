package org.jeecg.modules.qwert.conn.modbus4j.test;

import java.util.Arrays;

import org.jeecg.modules.qwert.conn.modbus4j.source.BatchRead;
import org.jeecg.modules.qwert.conn.modbus4j.source.BatchResults;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusFactory;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.code.DataType;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.IpParameters;
import org.jeecg.modules.qwert.conn.modbus4j.source.locator.BaseLocator;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadCoilsRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadCoilsResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadDiscreteInputsRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadDiscreteInputsResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadExceptionStatusRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadExceptionStatusResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadHoldingRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadHoldingRegistersResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReportSlaveIdRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReportSlaveIdResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteCoilRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteCoilResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteCoilsRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteCoilsResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteMaskRegisterRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteMaskRegisterResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteRegisterRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteRegisterResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.WriteRegistersResponse;

public class MasterTest {
    public static void main(String[] args) throws Exception {

/*    	String commPortId = "COM4";
    	int baudRate = 9600;
    	int flowControlIn = 0;
		int flowControlOut = 0; 
		int dataBits = 8;
		int stopBits = 1;
		int parity = 0;
*/   	
//    	TestSerialPortWrapper wrapper = new TestSerialPortWrapper(commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity);
        
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("10.66.70.103");
        ipParameters.setPort(10001);
        ipParameters.setEncapsulated(true);

        ModbusFactory modbusFactory = new ModbusFactory();

   //     ModbusMaster master = modbusFactory.createRtuMaster(wrapper);
        // ModbusMaster master = modbusFactory.createAsciiMaster(wrapper);
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, false);
        // ModbusMaster master = modbusFactory.createUdpMaster(ipParameters);

        try {
            master.init();
            int slaveId = 2;

          //  readCoilTest(master, slaveId, 98, 200);
            // readCoilTest(master, slaveId, 99, 200);
   //          readDiscreteInputTest(master, slaveId, 0, 2);
            // readDiscreteInputTest(master, slaveId, 449, 72);
            // readHoldingRegistersTest(master, slaveId, 9, 125);
            // readHoldingRegistersTest(master, slaveId, 9, 120);
            // readInputRegistersTest(master, slaveId, 0, 1);
            // readInputRegistersTest(master, slaveId, 14, 8);
            // writeCoilTest(master, slaveId, 1, true);
            // writeCoilTest(master, slaveId, 110, true);
            // writeRegisterTest(master, slaveId, 1, 9);
            // writeRegisterTest(master, slaveId, 14, 12345);
            // readExceptionStatusTest(master, slaveId);
            // reportSlaveIdTest(master, slaveId);
            // writeCoilsTest(master, slaveId, 50, new boolean[] {true, false, false, true, false});
            // writeCoilsTest(master, slaveId, 115, new boolean[] {true, false, false, true, false});
            // writeRegistersTest(master, slaveId, 300, new short[] {1, 10, 100, 1000, 10000, (short)65535});
            // writeRegistersTest(master, slaveId, 21, new short[] {1, 10, 100, 1000, 10000, (short)65535});
            // writeMaskRegisterTest(master, slaveId, 26, 0xf2, 0x25);

            // readCoilTest(master, slaveId, 9, 5);
            // readCoilTest(master, slaveId, 10, 5);
            // readDiscreteInputTest(master, slaveId, 10, 6);
            // readDiscreteInputTest(master, slaveId, 10, 5);
            readHoldingRegistersTest(master, slaveId, 140, 2);
           //  readHoldingRegistersTest(master, slaveId, 3037, 2);
    //         readInputRegistersTest(master, slaveId, 2340, 2);
            // readInputRegistersTest(master, slaveId, 0, 5);
            // writeCoilTest(master, slaveId, 8, true);
            // writeCoilTest(master, slaveId, 11, true);
            // writeRegisterTest(master, slaveId, 1, 1);
            // writeRegisterTest(master, slaveId, 14, 12345);
            // readExceptionStatusTest(master, slaveId);
            // reportSlaveIdTest(master, slaveId);
            // writeCoilsTest(master, slaveId, 11, new boolean[] {false, true, false, false, true});
            // writeCoilsTest(master, slaveId, 10, new boolean[] {false, true, false, false, true});
            // writeRegistersTest(master, slaveId, 11, new short[] {(short)65535, 1000, 100, 10, 1});
            // writeRegistersTest(master, slaveId, 10, new short[] {(short)65535, 1000, 100, 10, 1});
            // writeMaskRegisterTest(master, slaveId, 9, 0xf2, 0x25);
            // writeMaskRegisterTest(master, slaveId, 10, 0xf2, 0x25);

            // Automatic WriteMaskRegister failover test
            // ModbusLocator locator = new ModbusLocator(slaveId, RegisterRange.HOLDING_REGISTER, 15, (byte)2);
            // System.out.println(master.getValue(locator));
            // master.setValue(locator, true);
            // System.out.println(master.getValue(locator));
            // master.setValue(locator, false);
            // System.out.println(master.getValue(locator));

         //    BatchRead<String> batch = new BatchRead<String>();
            // batch.addLocator("0-1 sb true", BaseLocator.holdingRegisterBit(slaveId, 0, 1));
            // batch.addLocator("1-1 sb false", BaseLocator.holdingRegisterBit(slaveId, 1, 1));

            //batch.addLocator("hr1", new ModbusLocator(31, RegisterRange.HOLDING_REGISTER, 80,
           //  DataType.TWO_BYTE_BCD));
           //  batch.addLocator("hr2", new ModbusLocator(31, RegisterRange.HOLDING_REGISTER, 81,
           //  DataType.FOUR_BYTE_BCD));
            
         //    batch.addLocator("hr1",
         //            BaseLocator.holdingRegister(slaveId, 6, DataType.FOUR_BYTE_FLOAT_SWAPPED));
/*             batch.addLocator("hr2",
                     BaseLocator.holdingRegister(slaveId, 3025, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr3",
                     BaseLocator.holdingRegister(slaveId, 3027, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr4",
                     BaseLocator.holdingRegister(slaveId, 3029, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr5",
                     BaseLocator.holdingRegister(slaveId, 3083, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr6",
                     BaseLocator.holdingRegister(slaveId, 3109, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr7",
                     BaseLocator.holdingRegister(slaveId, 3203, DataType.TWO_BYTE_INT_UNSIGNED));
             batch.addLocator("hr8",
                     BaseLocator.holdingRegister(slaveId, 3204, DataType.TWO_BYTE_INT_UNSIGNED));
             batch.addLocator("hr9",
                     BaseLocator.holdingRegister(slaveId, 3205, DataType.TWO_BYTE_INT_UNSIGNED));
             batch.addLocator("hr10",
                     BaseLocator.holdingRegister(slaveId, 3206, DataType.TWO_BYTE_INT_UNSIGNED));
             BatchResults<String> results = master.send(batch);
             System.out.println(results);
             System.out.println(results.getValue("hr1"));
             System.out.println(results.getValue("hr2"));
             System.out.println(results.getValue("hr3"));
             System.out.println(results.getValue("hr4"));
             System.out.println(results.getValue("hr5"));
             System.out.println(results.getValue("hr6"));
             System.out.println(results.getValue("hr7"));
             System.out.println(results.getValue("hr8"));
             System.out.println(results.getValue("hr9"));
             System.out.println(results.getValue("hr10"));
*/
/*             
             batch.addLocator("hr1",
                     BaseLocator.inputRegister(slaveId, 2340, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr2",
                     BaseLocator.inputRegister(slaveId, 2342, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr3",
                     BaseLocator.inputRegister(slaveId, 2346, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr4",
                     BaseLocator.inputRegister(slaveId, 2352, DataType.FOUR_BYTE_FLOAT));
*/
  /*          batch.addLocator("hr5",
                     BaseLocator.holdingRegister(slaveId, 3083, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr6",
                     BaseLocator.holdingRegister(slaveId, 3109, DataType.FOUR_BYTE_FLOAT));
             batch.addLocator("hr7",
                     BaseLocator.holdingRegister(slaveId, 3203, DataType.TWO_BYTE_INT_UNSIGNED));
             batch.addLocator("hr8",
                     BaseLocator.holdingRegister(slaveId, 3204, DataType.TWO_BYTE_INT_UNSIGNED));
             batch.addLocator("hr9",
                     BaseLocator.holdingRegister(slaveId, 3205, DataType.TWO_BYTE_INT_UNSIGNED));
             batch.addLocator("hr10",
                     BaseLocator.holdingRegister(slaveId, 3206, DataType.TWO_BYTE_INT_UNSIGNED));
*/
          //   BatchResults<String> results = master.send(batch);
          //   System.out.println(results);
/*             System.out.println(results.getValue("hr1"));
             System.out.println(results.getValue("hr2"));
             System.out.println(results.getValue("hr3"));
             System.out.println(results.getValue("hr4"));
*/
/*
             System.out.println(results.getValue("hr5"));
             System.out.println(results.getValue("hr6"));
             System.out.println(results.getValue("hr7"));
             System.out.println(results.getValue("hr8"));
             System.out.println(results.getValue("hr9"));
             System.out.println(results.getValue("hr10"));
*/

             //            BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, 50, DataType.EIGHT_BYTE_INT_UNSIGNED);
//            master.setValue(locator, 10000000);
//            System.out.println(master.getValue(locator));
        }
        finally {
            master.destroy();
        }
    }

    public static void readCoilTest(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, len);
            ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getBooleanData()));
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void readDiscreteInputTest(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, len);
            ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getBooleanData()));
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void readHoldingRegistersTest(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getShortData()));
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void readInputRegistersTest(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, start, len);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getShortData()));
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeCoilTest(ModbusMaster master, int slaveId, int offset, boolean value) {
        try {
            WriteCoilRequest request = new WriteCoilRequest(slaveId, offset, value);
            WriteCoilResponse response = (WriteCoilResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println("Success");
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeRegisterTest(ModbusMaster master, int slaveId, int offset, int value) {
        try {
            WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
            WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println("Success");
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void readExceptionStatusTest(ModbusMaster master, int slaveId) {
        try {
            ReadExceptionStatusRequest request = new ReadExceptionStatusRequest(slaveId);
            ReadExceptionStatusResponse response = (ReadExceptionStatusResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(response.getExceptionStatus());
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void reportSlaveIdTest(ModbusMaster master, int slaveId) {
        try {
            ReportSlaveIdRequest request = new ReportSlaveIdRequest(slaveId);
            ReportSlaveIdResponse response = (ReportSlaveIdResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println(Arrays.toString(response.getData()));
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeCoilsTest(ModbusMaster master, int slaveId, int start, boolean[] values) {
        try {
            WriteCoilsRequest request = new WriteCoilsRequest(slaveId, start, values);
            WriteCoilsResponse response = (WriteCoilsResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println("Success");
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeRegistersTest(ModbusMaster master, int slaveId, int start, short[] values) {
        try {
            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, start, values);
            WriteRegistersResponse response = (WriteRegistersResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println("Success");
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static void writeMaskRegisterTest(ModbusMaster master, int slaveId, int offset, int and, int or) {
        try {
            WriteMaskRegisterRequest request = new WriteMaskRegisterRequest(slaveId, offset, and, or);
            WriteMaskRegisterResponse response = (WriteMaskRegisterResponse) master.send(request);

            if (response.isException())
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            else
                System.out.println("Success");
        }
        catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }
}
