package org.jeecg.modules.qwert.conn.modbus4j.test;

import java.util.Arrays;

import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadDiscreteInputsRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadDiscreteInputsResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadHoldingRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadHoldingRegistersResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersResponse;

public class ModbusUtil {

	public static void readDiscreteInputTest(ModbusMaster master, int slaveId, int start, int len) {
		try {
			ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, len);
			ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) master.send(request);

			if (response.isException())
				System.out.println("Exception response: message=" + response.getExceptionMessage());
			else
				System.out.println(Arrays.toString(response.getBooleanData()));
		} catch (ModbusTransportException e) {
			e.printStackTrace();
		}
	}

	public static String readHoldingRegistersTest(ModbusMaster master, int slaveId, int start, int len) {
		String result = null;
		try {
			ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
			ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
			if(response==null) {
				result ="devicefail";
				System.out.println("设备连接失败");
				return result;
			}
			if (response.isException()) {
				result = "fail";
				System.out.println("Exception response: message=" + response.getExceptionMessage());
			} else {
				result = Arrays.toString(response.getShortData());
//				System.out.println(Arrays.toString(response.getShortData()));
			}
		} catch (ModbusTransportException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String readInputRegistersTest(ModbusMaster master, int slaveId, int start, int len) {
		String result = null;
		try {
			ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, start, len);
			ReadInputRegistersResponse response = (ReadInputRegistersResponse) master.send(request);
			if(response==null) {
				result ="devicefail";
				System.out.println("设备连接失败");
				return result;
			}
			if (response.isException()) {
				result = "fail";
				System.out.println("Exception response: message=" + response.getExceptionMessage());
			} else {
				result = Arrays.toString(response.getShortData());
				System.out.println(Arrays.toString(response.getShortData()));
			}
		} catch (ModbusTransportException e) {
			e.printStackTrace();
		}
		return result;
	}
}
