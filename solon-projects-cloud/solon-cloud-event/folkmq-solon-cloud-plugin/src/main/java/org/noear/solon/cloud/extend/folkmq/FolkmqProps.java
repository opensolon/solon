package org.noear.solon.cloud.extend.folkmq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear
 * @since 2.0
 */
public class FolkmqProps {

    public static final String GROUP_SPLIT_MARK = "--";

    public static String getTopicNew(Event event){
        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + FolkmqProps.GROUP_SPLIT_MARK + event.topic();
        }

        return topicNew;
    }
}
