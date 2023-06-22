package org.noear.solon.extend.graphql.support;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface Resource {

    boolean exists();

    InputStream getInputStream() throws IOException;
}
