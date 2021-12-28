package org.noear.solon.schedule;

/**
 * @author noear
 * @since 1.5
 */
public class ScheduledException extends RuntimeException {
    public ScheduledException(Throwable cause) {
        super(cause);
    }
}
