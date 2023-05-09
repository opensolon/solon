package org.tio.solon.integration;

public class TioServerMsgHandlerNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7400409984244123169L;

	public TioServerMsgHandlerNotFoundException() {
        super();
    }

    public TioServerMsgHandlerNotFoundException(String msg) {
        super(msg);
    }
}