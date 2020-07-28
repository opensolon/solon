package org.noear.solonclient;

import org.noear.solonclient.channel.Result;

import java.util.Map;

public interface IChannel {
    Result call(XProxy proxy, Map<String, String> headers, Map<String, String> args) throws Exception;
}
