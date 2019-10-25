package org.noear.solon.boot.smartsocket;

import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.io.*;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SsProcessor implements MessageProcessor<byte[]> {
    private SsContextHandler _contextHandler;
    public SsProcessor(SsContextHandler contextHandler){
        this._contextHandler = contextHandler;
    }

    @Override
    public void process(AioSession<byte[]> session, byte[] msg) {
        ObjectInput objectInput = null;
        ObjectOutput objectOutput = null;

        try {
            objectInput = new ObjectInputStream(new ByteArrayInputStream(msg));
            Map<String,Object> rpcxMsg = (Map<String,Object>)objectInput.readObject();
            SsRequest request = new SsRequest(rpcxMsg);
            SsResponse response = new SsResponse(request);

            request.setRemoteAddr(session.getRemoteAddress());

            _contextHandler.handle(request,response);

            response.close();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(response.message());

            session.write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInput != null) {
                try {
                    objectInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutput != null) {
                try {
                    objectOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void stateEvent(AioSession<byte[]> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}