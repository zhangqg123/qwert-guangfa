package org.jeecg.modules.qwert.conn.modbus4j.test;

import java.util.Arrays;

import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
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

public class ModbusUtil2 {

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
