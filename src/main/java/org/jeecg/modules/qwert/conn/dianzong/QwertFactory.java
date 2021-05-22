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
package org.jeecg.modules.qwert.conn.dianzong;

import org.jeecg.modules.qwert.conn.dianzong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.dianzong.code.RegisterRange;
import org.jeecg.modules.qwert.conn.dianzong.exception.ModbusIdException;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest;
import org.jeecg.modules.qwert.conn.dianzong.msg.ReadDianzongRequest;
//import org.jeecg.modules.qwert.conn.dianzong.msg.ReadInputRegistersRequest;
import org.jeecg.modules.qwert.conn.dianzong.serial.SerialPortWrapper;
import org.jeecg.modules.qwert.conn.dianzong.serial.dianzong.DianzongMaster;
import org.jeecg.modules.qwert.conn.dianzong.serial.dianzong.DianzongSlave;

/**
 * <p>QwertFactory class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class QwertFactory {
    
    /**
     * <p>createDianzongMaster.</p>
     *
     * @param wrapper a {@link org.jeecg.modules.qwert.conn.dianzong.serial.SerialPortWrapper} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.QwertMaster} object.
     */
    public QwertMaster createDianzongMaster(SerialPortWrapper wrapper) {
        return new DianzongMaster(wrapper);
    }

     /**
     * <p>createDianzongSlave.</p>
     *
     * @param wrapper a {@link org.jeecg.modules.qwert.conn.dianzong.serial.SerialPortWrapper} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.QwertSlaveSet} object.
     */
    public QwertSlaveSet createDianzongSlave(SerialPortWrapper wrapper) {
        return new DianzongSlave(wrapper);
    }

    /**
     * <p>createReadRequest.</p>
     *
     * @param slaveId a int.
     * @param range a int.
     * @param offset a int.
     * @param length a int.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.ModbusIdException if any.
     */
/*    public QwertRequest createReadRequest(int slaveId, int range, int offset, int length)
            throws DianzongTransportException, ModbusIdException {
        QwertUtils.validateRegisterRange(range);

        if (range == RegisterRange.INPUT_REGISTER)
            return new ReadInputRegistersRequest(slaveId, offset, length);

        return new ReadDianzongRequest(slaveId, offset, length);
    } */
}
