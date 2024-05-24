package org.apache.thrift.solon.integration;

import org.noear.solon.boot.prop.impl.BaseServerProps;

/**
 * Thrift 服务属性
 * <p>
 * 例如：server.thrift.port: 9090
 *
 * @author LIAO.Chunping
 */
public class ThriftServerProps extends BaseServerProps {

    private final static String SIGNAL_NAME = "thrift";

    public ThriftServerProps(int portBase) {
        super(SIGNAL_NAME, portBase);
    }
}
