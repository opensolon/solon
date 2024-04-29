package org.noear.solon.net.stomp;


import java.util.List;

/**
 * 消息
 *
 * @author limliu
 * @since 2.7
 */
public interface Message {

    Message addHeader(String key, String val);

    List<Header> getHeaders();

    String getHeader(String key);

    String getPayload();

    String getCommand();


    String headerValue(String key);

    Header header(String key);

}
