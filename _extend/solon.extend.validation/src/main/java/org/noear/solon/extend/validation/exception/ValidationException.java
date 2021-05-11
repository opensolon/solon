package org.noear.solon.extend.validation.exception;

import org.noear.solon.core.handle.Result;
import org.noear.solon.core.handle.Resultable;
import org.noear.solon.ext.DataThrowable;

/**
 * @author noear
 * @since 1.3
 */
public class ValidationException extends DataThrowable implements Resultable {
    private final Result result;

    @Override
    public Result getResult() {
        return result;
    }

    public ValidationException(Result result) {
        super(result.getDescription());
        this.result = result;
    }
}
