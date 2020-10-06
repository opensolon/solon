/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: LoggerSystemException.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.logging;

class LoggerSystemException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1218011247171440869L;

	public LoggerSystemException() {
		super();
	}

	public LoggerSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoggerSystemException(String message) {
		super(message);
	}

	public LoggerSystemException(Throwable cause) {
		super(cause);
	}

}