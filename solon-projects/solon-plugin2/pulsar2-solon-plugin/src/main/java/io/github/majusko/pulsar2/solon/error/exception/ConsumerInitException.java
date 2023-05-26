package io.github.majusko.pulsar2.solon.error.exception;

public class ConsumerInitException extends RuntimeException {
    public ConsumerInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
