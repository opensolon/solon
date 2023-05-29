package org.noear.solon.cloud.extend.activemq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear
 * @since 2.0
 */
public class ActivemqProps {

    public static final String GROUP_SPLIT_MARK = ":";

    public static String getTopicNew(Event event){
        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + ActivemqProps.GROUP_SPLIT_MARK + event.topic();
        }

        return topicNew;
    }
}
