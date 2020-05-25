package org.noear.fineio.extension;

import org.noear.fineio.SessionProcessor;
import org.noear.fineio.NetSession;

import java.util.ArrayList;

/**
 * 消息处理组
 * */
public class MessageProcessorGroup<T> implements SessionProcessor<T> {
    private ArrayList<SessionProcessor<T>> group = new ArrayList<>();

    public void append(SessionProcessor<T> processor) {
        group.add(processor);
    }

    @Override
    public void process(NetSession<T> session) {
        for (SessionProcessor<T> processor : group) {
            processor.process(session);
        }
    }
}
