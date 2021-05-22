/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jeecg.modules.qwert.conn.modbus4j.source;

import org.jeecg.modules.qwert.conn.modbus4j.source.ip.IpParameters;
import org.jeecg.modules.qwert.conn.modbus4j.source.base.ModbusUtils;
import org.jeecg.modules.qwert.conn.modbus4j.source.code.RegisterRange;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusIdException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.listener.TcpListener;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.tcp.TcpMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.tcp.TcpSlave;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.udp.UdpMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.udp.UdpSlave;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ModbusRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadCoilsRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadDiscreteInputsRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadHoldingRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.SerialPortWrapper;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.ascii.AsciiMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.ascii.AsciiSlave;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.rtu.RtuMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.rtu.RtuSlave;

/**
 * <p>ModbusFactory class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ModbusFactory {
    //
    // Modbus masters
    //
    /**
     * <p>createRtuMaster.</p>
     *
     * @param wrapper a {@link SerialPortWrapper} object.
     * @return a {@link ModbusMaster} object.
     */
    public ModbusMaster createRtuMaster(SerialPortWrapper wrapper) {
        return new RtuMaster(wrapper);
    }
    
    /**
     * <p>createAsciiMaster.</p>
     *
     * @param wrapper a {@link SerialPortWrapper} object.
     * @return a {@link ModbusMaster} object.
     */
    public ModbusMaster createAsciiMaster(SerialPortWrapper wrapper) {
        return new AsciiMaster(wrapper);
    }

    /**
     * <p>createTcpMaster.</p>
     *
     * @param params a {@link IpParameters} object.
     * @param keepAlive a boolean.
     * @return a {@link ModbusMaster} object.
     */
    public ModbusMaster createTcpMaster(IpParameters params, boolean keepAlive) {
        return new TcpMaster(params, keepAlive);
    }

    /**
     * <p>createUdpMaster.</p>
     *
     * @param params a {@link IpParameters} object.
     * @return a {@link ModbusMaster} object.
     */
    public ModbusMaster createUdpMaster(IpParameters params) {
        return new UdpMaster(params);
    }

    /**
     * <p>createTcpListener.</p>
     *
     * @param params a {@link IpParameters} object.
     * @return a {@link ModbusMaster} object.
     */
    public ModbusMaster createTcpListener(IpParameters params) {
        return new TcpListener(params);
    }

    //
    // Modbus slaves
    //
    /**
     * <p>createRtuSlave.</p>
     *
     * @param wrapper a {@link SerialPortWrapper} object.
     * @return a {@link ModbusSlaveSet} object.
     */
    public ModbusSlaveSet createRtuSlave(SerialPortWrapper wrapper) {
        return new RtuSlave(wrapper);
    }

    /**
     * <p>createAsciiSlave.</p>
     *
     * @param wrapper a {@link SerialPortWrapper} object.
     * @return a {@link ModbusSlaveSet} object.
     */
    public ModbusSlaveSet createAsciiSlave(SerialPortWrapper wrapper) {
        return new AsciiSlave(wrapper);
    }

    /**
     * <p>createTcpSlave.</p>
     *
     * @param encapsulated a boolean.
     * @return a {@link ModbusSlaveSet} object.
     */
    public ModbusSlaveSet createTcpSlave(boolean encapsulated) {
        return new TcpSlave(encapsulated);
    }

    /**
     * <p>createUdpSlave.</p>
     *
     * @param encapsulated a boolean.
     * @return a {@link ModbusSlaveSet} object.
     */
    public ModbusSlaveSet createUdpSlave(boolean encapsulated) {
        return new UdpSlave(encapsulated);
    }

    //
    // Modbus requests
    //
    /**
     * <p>createReadRequest.</p>
     *
     * @param slaveId a int.
     * @param range a int.
     * @param offset a int.
     * @param length a int.
     * @return a {@link ModbusRequest} object.
     * @throws ModbusTransportException if any.
     * @throws ModbusIdException if any.
     */
    public ModbusRequest createReadRequest(int slaveId, int range, int offset, int length)
            throws ModbusTransportException, ModbusIdException {
        ModbusUtils.validateRegisterRange(range);

        if (range == RegisterRange.COIL_STATUS)
            return new ReadCoilsRequest(slaveId, offset, length);

        if (range == RegisterRange.INPUT_STATUS)
            return new ReadDiscreteInputsRequest(slaveId, offset, length);

        if (range == RegisterRange.INPUT_REGISTER)
            return new ReadInputRegistersRequest(slaveId, offset, length);

        return new ReadHoldingRegistersRequest(slaveId, offset, length);
    }
}
