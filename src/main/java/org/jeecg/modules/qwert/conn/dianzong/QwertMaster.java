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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.qwert.conn.dianzong.base.KeyedModbusLocator;
import org.jeecg.modules.qwert.conn.dianzong.base.ReadFunctionGroup;
import org.jeecg.modules.qwert.conn.dianzong.base.SlaveProfile;
import org.jeecg.modules.qwert.conn.dianzong.code.DataType;
import org.jeecg.modules.qwert.conn.dianzong.code.ExceptionCode;
import org.jeecg.modules.qwert.conn.dianzong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.dianzong.code.RegisterRange;
import org.jeecg.modules.qwert.conn.dianzong.exception.ErrorResponseException;
import org.jeecg.modules.qwert.conn.dianzong.exception.InvalidDataConversionException;
import org.jeecg.modules.qwert.conn.dianzong.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.locator.BaseLocator;
import org.jeecg.modules.qwert.conn.dianzong.locator.BinaryLocator;
import org.jeecg.modules.qwert.conn.dianzong.locator.NumericLocator;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertResponse;
import org.jeecg.modules.qwert.conn.dianzong.msg.ReadDianzongRequest;
//import org.jeecg.modules.qwert.conn.dianzong.msg.ReadInputRegistersRequest;
import org.jeecg.modules.qwert.conn.dianzong.msg.ReadResponse;
import org.jeecg.modules.qwert.conn.dianzong.sero.epoll.InputStreamEPollWrapper;
import org.jeecg.modules.qwert.conn.dianzong.sero.log.BaseIOLog;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.MessageControl;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.ArrayUtils;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.ProgressiveTask;

/**
 * <p>Abstract QwertMaster class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class QwertMaster extends Qwert {
    private int timeout = 500;
    private int retries = 2;
    
    /**
     * Should we validate the responses:
     *  - ensure that the requested slave id is what is in the response
     */
    protected boolean validateResponse;

    /**
     * If connection is established with slave/slaves
     */
    protected boolean connected = false;

    /**
     * <p>isConnected.</p>
     *
     * @return a boolean.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * <p>Setter for the field <code>connected</code>.</p>
     *
     * @param connected a boolean.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * If the slave equipment only supports multiple write commands, set this to true. Otherwise, and combination of
     * single or multiple write commands will be used as appropriate.
     */
    private boolean multipleWritesOnly;

    private int discardDataDelay = 0;
    private BaseIOLog ioLog;

    /**
     * An input stream ePoll will use a single thread to read all input streams. If multiple serial or TCP modbus
     * connections are to be made, an ePoll can be much more efficient.
     */
    private InputStreamEPollWrapper ePoll;

    private final Map<Integer, SlaveProfile> slaveProfiles = new HashMap<>();
    protected boolean initialized;

    /**
     * <p>init.</p>
     *
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.ModbusInitException if any.
     */
    abstract public void init() throws ModbusInitException;

    /**
     * <p>isInitialized.</p>
     *
     * @return a boolean.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * <p>destroy.</p>
     */
    abstract public void destroy();

    /**
     * <p>send.</p>
     *
     * @param request a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertResponse} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    public final QwertResponse send(QwertRequest request) throws DianzongTransportException {
//        request.validate(this);
		QwertResponse QwertResponse = sendImpl(request);
		if(validateResponse)
		    QwertResponse.validateResponse(request);
		return QwertResponse;
    }

    /**
     * <p>sendImpl.</p>
     *
     * @param request a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertResponse} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    abstract public QwertResponse sendImpl(QwertRequest request) throws DianzongTransportException;

    /**
     * Returns a value from the modbus network according to the given locator information. Various data types are
     * allowed to be requested including multi-word types. The determination of the correct request message to send is
     * handled automatically.
     *
     * @param locator
     *            the information required to locate the value in the modbus network.
     * @return an object representing the value found. This will be one of Boolean, Short, Integer, Long, BigInteger,
     *         Float, or Double. See the DataType enumeration for details on which type to expect.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException
     *             if there was an IO error or other technical failure while sending the message
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.ErrorResponseException
     *             if the response returned from the slave was an exception.
     * @param <T> a T object.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(BaseLocator<T> locator) throws DianzongTransportException, ErrorResponseException {
        BatchRead<String> batch = new BatchRead<>();
        batch.addLocator("", locator);
        BatchResults<String> result = send(batch);
        return (T) result.getValue("");
    }


    /**
     * Node scanning. Returns a list of slave nodes that respond to a read exception status request (perhaps with an
     * error, but respond nonetheless).
     *
     * Note: a similar scan could be done for registers in nodes, but, for one thing, it would take some time to run,
     * and in any case the results would not be meaningful since there would be no semantic information accompanying the
     * results.
     *
     * @return a {@link java.util.List} object.
     */
    public List<Integer> scanForSlaveNodes() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= 240; i++) {
            if (testSlaveNode(i))
                result.add(i);
        }
        return result;
    }

    /**
     * <p>scanForSlaveNodes.</p>
     *
     * @param l a {@link org.jeecg.modules.qwert.conn.dianzong.NodeScanListener} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.ProgressiveTask} object.
     */
    public ProgressiveTask scanForSlaveNodes(final NodeScanListener l) {
        l.progressUpdate(0);
        ProgressiveTask task = new ProgressiveTask(l) {
            private int node = 1;

            @Override
            protected void runImpl() {
                if (testSlaveNode(node))
                    l.nodeFound(node);

                declareProgress(((float) node) / 240);

                node++;
                if (node > 240)
                    completed = true;
            }
        };

        new Thread(task).start();

        return task;
    }

    /**
     * <p>testSlaveNode.</p>
     *
     * @param node a int.
     * @return a boolean.
     */
    public boolean testSlaveNode(int node) {
        try {
            send(new ReadDianzongRequest(2.1f,node, 0, 1,0));
        //    send(new ReadInputRegistersRequest(node, 0, 4));
        }
        catch (DianzongTransportException e) {
            // If there was a transport exception, there's no node there.
            return false;
        }
        return true;
    }

    /**
     * <p>Getter for the field <code>retries</code>.</p>
     *
     * @return a int.
     */
    public int getRetries() {
        return retries;
    }

    /**
     * <p>Setter for the field <code>retries</code>.</p>
     *
     * @param retries a int.
     */
    public void setRetries(int retries) {
        if (retries < 0)
            this.retries = 0;
        else
            this.retries = retries;
    }

    /**
     * <p>Getter for the field <code>timeout</code>.</p>
     *
     * @return a int.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * <p>Setter for the field <code>timeout</code>.</p>
     *
     * @param timeout a int.
     */
    public void setTimeout(int timeout) {
        if (timeout < 1)
            this.timeout = 1;
        else
            this.timeout = timeout;
    }

    /**
     * <p>isMultipleWritesOnly.</p>
     *
     * @return a boolean.
     */
    public boolean isMultipleWritesOnly() {
        return multipleWritesOnly;
    }

    /**
     * <p>Setter for the field <code>multipleWritesOnly</code>.</p>
     *
     * @param multipleWritesOnly a boolean.
     */
    public void setMultipleWritesOnly(boolean multipleWritesOnly) {
        this.multipleWritesOnly = multipleWritesOnly;
    }

    /**
     * <p>Getter for the field <code>discardDataDelay</code>.</p>
     *
     * @return a int.
     */
    public int getDiscardDataDelay() {
        return discardDataDelay;
    }

    /**
     * <p>Setter for the field <code>discardDataDelay</code>.</p>
     *
     * @param discardDataDelay a int.
     */
    public void setDiscardDataDelay(int discardDataDelay) {
        if (discardDataDelay < 0)
            this.discardDataDelay = 0;
        else
            this.discardDataDelay = discardDataDelay;
    }

    /**
     * <p>Getter for the field <code>ioLog</code>.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.sero.log.BaseIOLog} object.
     */
    public BaseIOLog getIoLog() {
        return ioLog;
    }

    /**
     * <p>Setter for the field <code>ioLog</code>.</p>
     *
     * @param ioLog a {@link org.jeecg.modules.qwert.conn.dianzong.sero.log.BaseIOLog} object.
     */
    public void setIoLog(BaseIOLog ioLog) {
        this.ioLog = ioLog;
    }

    /**
     * <p>Getter for the field <code>ePoll</code>.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.sero.epoll.InputStreamEPollWrapper} object.
     */
    public InputStreamEPollWrapper getePoll() {
        return ePoll;
    }

    /**
     * <p>Setter for the field <code>ePoll</code>.</p>
     *
     * @param ePoll a {@link org.jeecg.modules.qwert.conn.dianzong.sero.epoll.InputStreamEPollWrapper} object.
     */
    public void setePoll(InputStreamEPollWrapper ePoll) {
        this.ePoll = ePoll;
    }

    /**
     * Useful for sending a number of polling commands at once, or at least in as optimal a batch as possible.
     *
     * @param batch a {@link org.jeecg.modules.qwert.conn.dianzong.BatchRead} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.BatchResults} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.ErrorResponseException if any.
     * @param <K> type of result
     */
    public <K> BatchResults<K> send(BatchRead<K> batch) throws DianzongTransportException, ErrorResponseException {
        if (!initialized)
            throw new DianzongTransportException("not initialized");

        BatchResults<K> results = new BatchResults<>();
        List<ReadFunctionGroup<K>> functionGroups = batch.getReadFunctionGroups(this);

        // Execute each read function and process the results.
        for (ReadFunctionGroup<K> functionGroup : functionGroups) {
            sendFunctionGroup(functionGroup, results, batch.isErrorsInResults(), batch.isExceptionsInResults());
            if (batch.isCancel())
                break;
        }

        return results;
    }

    //
    //
    // Protected methods
    //
    /**
     * <p>getMessageControl.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.sero.messaging.MessageControl} object.
     */
    protected MessageControl getMessageControl() {
        MessageControl conn = new MessageControl();
        conn.setRetries(getRetries());
        conn.setTimeout(getTimeout());
        conn.setDiscardDataDelay(getDiscardDataDelay());
        conn.setExceptionHandler(getExceptionHandler());
        conn.setIoLog(ioLog);
        return conn;
    }

    /**
     * <p>closeMessageControl.</p>
     *
     * @param conn a {@link org.jeecg.modules.qwert.conn.dianzong.sero.messaging.MessageControl} object.
     */
    protected void closeMessageControl(MessageControl conn) {
        if (conn != null)
            conn.close();
    }

    //
    //
    // Private stuff
    //
    /**
     * This method assumes that all locators have already been pre-sorted and grouped into valid requests, say, by the
     * createRequestGroups method.
     */
    private <K> void sendFunctionGroup(ReadFunctionGroup<K> functionGroup, BatchResults<K> results,
            boolean errorsInResults, boolean exceptionsInResults) throws DianzongTransportException,
            ErrorResponseException {
        int slaveId = functionGroup.getSlaveAndRange().getSlaveId();
        int startOffset = functionGroup.getStartOffset();
        int length = functionGroup.getLength();

        // Inspect the function group for data required to create the request.
        QwertRequest request;
        if (functionGroup.getFunctionCode() == FunctionCode.READ_DIANZONG_REGISTERS)
            request = new ReadDianzongRequest(2.1f,slaveId, startOffset, length,0);
 //       else if (functionGroup.getFunctionCode() == FunctionCode.READ_INPUT_REGISTERS)
 //           request = new ReadInputRegistersRequest(slaveId, startOffset, length);
        else
            throw new RuntimeException("Unsupported function");

        ReadResponse response;
        try {
            response = (ReadResponse) send(request);
        }
        catch (DianzongTransportException e) {
            if (!exceptionsInResults)
                throw e;

            for (KeyedModbusLocator<K> locator : functionGroup.getLocators())
                results.addResult(locator.getKey(), e);

            return;
        }

        byte[] data = null;
        if (!errorsInResults && response.isException())
            throw new ErrorResponseException(request, response);
        else if (!response.isException())
            data = response.getData();

        for (KeyedModbusLocator<K> locator : functionGroup.getLocators()) {
            if (errorsInResults && response.isException())
                results.addResult(locator.getKey(), new ExceptionResult(response.getExceptionCode()));
            else {
                try {
                    results.addResult(locator.getKey(), locator.bytesToValue(data, startOffset));
                }
                catch (RuntimeException e) {
                    throw new RuntimeException("Result conversion exception. data=" + ArrayUtils.toHexString(data)
                            + ", startOffset=" + startOffset + ", locator=" + locator + ", functionGroup.functionCode="
                            + functionGroup.getFunctionCode() + ", functionGroup.startOffset=" + startOffset
                            + ", functionGroup.length=" + length, e);
                }
            }
        }
    }

    private void setValue(QwertRequest request) throws DianzongTransportException, ErrorResponseException {
        QwertResponse response = send(request);
        if (response == null)
            // This should only happen if the request was a broadcast
            return;
        if (response.isException())
            throw new ErrorResponseException(request, response);
    }

    private SlaveProfile getSlaveProfile(int slaveId) {
        SlaveProfile sp = slaveProfiles.get(slaveId);
        if (sp == null) {
            sp = new SlaveProfile();
            slaveProfiles.put(slaveId, sp);
        }
        return sp;
    }
}
