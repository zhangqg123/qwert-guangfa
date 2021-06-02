package org.jeecg.modules.qwert.conn.qudong.sero.messaging;

import java.io.IOException;

import org.jeecg.modules.qwert.conn.qudong.sero.io.StreamUtils;
import org.jeecg.modules.qwert.conn.qudong.sero.log.BaseIOLog;
import org.jeecg.modules.qwert.conn.qudong.sero.timer.SystemTimeSource;
import org.jeecg.modules.qwert.conn.qudong.sero.timer.TimeSource;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * In general there are three messaging activities:
 * <ol>
 * <li>Send a message for which no reply is expected, e.g. a broadcast.</li>
 * <li>Send a message and wait for a response with timeout and retries.</li>
 * <li>Listen for unsolicited requests.</li>
 * </ol>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class MessageControl implements DataConsumer {
    private static int DEFAULT_RETRIES = 2;
    private static int DEFAULT_TIMEOUT = 500;

    public boolean DEBUG = true;

    private Transport transport;
    private MessageParser messageParser;
    private RequestHandler requestHandler;
    private WaitingRoomKeyFactory waitingRoomKeyFactory;
    private MessagingExceptionHandler exceptionHandler = new DefaultMessagingExceptionHandler();
    private int retries = DEFAULT_RETRIES;
    private int timeout = DEFAULT_TIMEOUT;
    private int discardDataDelay = 0;
    private long lastDataTimestamp;

    private BaseIOLog ioLog;
    private TimeSource timeSource = new SystemTimeSource();

    private final WaitingRoom waitingRoom = new WaitingRoom();
    private final ByteQueue dataBuffer = new ByteQueue();

    /**
     * <p>start.</p>
     *
     * @param transport a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.Transport} object.
     * @param messageParser a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.MessageParser} object.
     * @param handler a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.RequestHandler} object.
     * @param waitingRoomKeyFactory a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.WaitingRoomKeyFactory} object.
     * @throws java.io.IOException if any.
     */
    public void start(Transport transport, MessageParser messageParser, RequestHandler handler,
            WaitingRoomKeyFactory waitingRoomKeyFactory) throws IOException {
        this.transport = transport;
        this.messageParser = messageParser;
        this.requestHandler = handler;
        this.waitingRoomKeyFactory = waitingRoomKeyFactory;
        waitingRoom.setKeyFactory(waitingRoomKeyFactory);
        transport.setConsumer(this);
    }

    /**
     * <p>close.</p>
     */
    public void close() {
        transport.removeConsumer();
    }

    /**
     * <p>Setter for the field <code>exceptionHandler</code>.</p>
     *
     * @param exceptionHandler a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.MessagingExceptionHandler} object.
     */
    public void setExceptionHandler(MessagingExceptionHandler exceptionHandler) {
        if (exceptionHandler == null)
            this.exceptionHandler = new DefaultMessagingExceptionHandler();
        else
            this.exceptionHandler = exceptionHandler;
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
        this.timeout = timeout;
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
        this.discardDataDelay = discardDataDelay;
    }

    /**
     * <p>Getter for the field <code>ioLog</code>.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.qudong.sero.log.BaseIOLog} object.
     */
    public BaseIOLog getIoLog() {
        return ioLog;
    }

    /**
     * <p>Setter for the field <code>ioLog</code>.</p>
     *
     * @param ioLog a {@link org.jeecg.modules.qwert.conn.qudong.sero.log.BaseIOLog} object.
     */
    public void setIoLog(BaseIOLog ioLog) {
        this.ioLog = ioLog;
    }

    /**
     * <p>Getter for the field <code>timeSource</code>.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.qudong.sero.timer.TimeSource} object.
     */
    public TimeSource getTimeSource() {
        return timeSource;
    }

    /**
     * <p>Setter for the field <code>timeSource</code>.</p>
     *
     * @param timeSource a {@link org.jeecg.modules.qwert.conn.qudong.sero.timer.TimeSource} object.
     */
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    /**
     * <p>send.</p>
     *
     * @param request a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.OutgoingRequestMessage} object.
     * @return a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.IncomingResponseMessage} object.
     * @throws java.io.IOException if any.
     */
    public IncomingResponseMessage send(OutgoingRequestMessage request) throws IOException {
        return send(request, timeout, retries);
    }

    /**
     * <p>send.</p>
     *
     * @param request a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.OutgoingRequestMessage} object.
     * @param timeout a int.
     * @param retries a int.
     * @return a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.IncomingResponseMessage} object.
     * @throws java.io.IOException if any.
     */
    public IncomingResponseMessage send(OutgoingRequestMessage request, int timeout, int retries) throws IOException {
        byte[] data = request.getMessageData2(1);
  //      byte[] data = request.getMessageData();
	//	String putIn = "~210360470000fda9\r\n";
//    		String putIn= "~00P003STB"; //台达
	//	byte[] data = putIn.toUpperCase().getBytes();
    //		String putIn= "~200160420000fdb1\r"; //大金
  //  		String putIn= "$016\r"; //7000d
//    		String putIn= "~00P003STB"; //台达
  //  		String putIn= "Q1\r"; //科士达
		
        if (DEBUG)
            System.out.println("MessagingControl.send: " + StreamUtils.dumpHex(data));

        IncomingResponseMessage response = null;

        if (request.expectsResponse()) {
            WaitingRoomKey key = waitingRoomKeyFactory.createWaitingRoomKey(request);

            // Enter the waiting room
            waitingRoom.enter(key);

            try {
                do {
                    // Send the request.
                    write(data);

                    // Wait for the response.
                    response = waitingRoom.getResponse(key, timeout);

                    if (DEBUG && response == null)
                        System.out.println("Timeout waiting for response");
                }
                while (response == null && retries-- > 0);
            }
            finally {
                // Leave the waiting room.
                waitingRoom.leave(key);
            }

            if (response == null)
                throw new TimeoutException("request=" + request);
        }
        else
            write(data);

        return response;
    }

    /**
     * <p>send.</p>
     *
     * @param response a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.OutgoingResponseMessage} object.
     * @throws java.io.IOException if any.
     */
    public void send(OutgoingResponseMessage response) throws IOException {
		String putIn = "~21036000d03000d201f40014003200000000012c0096032000c800e60078f3f3\r";
//        String putIn = "!9E0000\r"; //M7000d
//        String putIn = "(208.4 140.0 208.4 034 59.9 2.05 35.0 00110000\r"; //科士达
//        String putIn = "~00D0250;0;1;;;000;2740;;029;100";//台达
		byte[] data=putIn.toUpperCase().getBytes();
        write(data);
//        write(response.getMessageData2(2));
    }

    /**
     * {@inheritDoc}
     *
     * Incoming data from the transport. Single-threaded.
     */
    public void data(byte[] b, int len) {
        if (DEBUG)
            System.out.println("MessagingConnection.read: " + StreamUtils.dumpHex(b, 0, len));
        if (ioLog != null)
            ioLog.input(b, 0, len);

        if (discardDataDelay > 0) {
            long now = timeSource.currentTimeMillis();
            if (now - lastDataTimestamp > discardDataDelay)
                dataBuffer.clear();
            lastDataTimestamp = now;
        }

        dataBuffer.push(b, 0, len);

        // There may be multiple messages in the data, so enter a loop.
        while (true) {
            // Attempt to parse a message.
            try {
                // Mark where we are in the buffer. The entire message may not be in yet, but since the parser
                // will consume the buffer we need to be able to backtrack.
                dataBuffer.mark();

                IncomingMessage message = messageParser.parseMessage(dataBuffer);

                if (message == null) {
                    // Nothing to do. Reset the buffer and exit the loop.
                    dataBuffer.reset();
                    break;
                }

                if (message instanceof IncomingRequestMessage) {
                    // Received a request. Give it to the request handler
                    if (requestHandler != null) {
                        OutgoingResponseMessage response = requestHandler
                                .handleRequest((IncomingRequestMessage) message);

                        if (response != null)
                            send(response);
                    }
                }
                else
                    // Must be a response. Give it to the waiting room.
                    waitingRoom.response((IncomingResponseMessage) message);
            }
            catch (Exception e) {
                exceptionHandler.receivedException(e);
                // Clear the buffer
                //                dataBuffer.clear();
            }
        }
    }

    private void write(byte[] data) throws IOException {
        if (ioLog != null)
            ioLog.output(data);

        synchronized (transport) {
            transport.write(data);
        }
    }

    /** {@inheritDoc} */
    public void handleIOException(IOException e) {
        exceptionHandler.receivedException(e);
    }
}
