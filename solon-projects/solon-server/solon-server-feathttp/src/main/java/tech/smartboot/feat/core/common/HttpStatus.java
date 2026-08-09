/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common;

import io.github.smartboot.socket.transport.WriteBuffer;

import java.io.IOException;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HttpStatus {
    private static final HttpStatus[] HASH_TABLE = new HttpStatus[600];

    public static final HttpStatus CONTINUE = new HttpStatus(100, "Continue", true, true);
    public static final HttpStatus SWITCHING_PROTOCOLS = new HttpStatus(101, "Switching Protocols", true, true);
    public static final HttpStatus PROCESSING = new HttpStatus(102, "Processing", false, true);
    public static final HttpStatus CHECKPOINT = new HttpStatus(103, "Checkpoint", false, true);

    public static final HttpStatus OK = new HttpStatus(200, "OK", true, true);
    public static final HttpStatus CREATED = new HttpStatus(201, "Created", false, true);
    public static final HttpStatus ACCEPTED = new HttpStatus(202, "Accepted", false, true);
    public static final HttpStatus NON_AUTHORITATIVE_INFORMATION = new HttpStatus(203, "Non-Authoritative Information", false, true);
    public static final HttpStatus NO_CONTENT = new HttpStatus(204, "No Content", false, true);
    public static final HttpStatus RESET_CONTENT = new HttpStatus(205, "Reset Content", false, true);
    public static final HttpStatus PARTIAL_CONTENT = new HttpStatus(206, "Partial Content", false, true);
    public static final HttpStatus MULTI_STATUS = new HttpStatus(207, "Multi-Status", false, true);
    public static final HttpStatus ALREADY_REPORTED = new HttpStatus(208, "Already Reported", false, true);
    public static final HttpStatus IM_USED = new HttpStatus(226, "IM Used", false, true);

    public static final HttpStatus MULTIPLE_CHOICES = new HttpStatus(300, "Multiple Choices", false, true);
    public static final HttpStatus MOVED_PERMANENTLY = new HttpStatus(301, "Moved Permanently", false, true);
    public static final HttpStatus FOUND = new HttpStatus(302, "Found", false, true);
    public static final HttpStatus SEE_OTHER = new HttpStatus(303, "See Other", false, true);
    public static final HttpStatus NOT_MODIFIED = new HttpStatus(304, "Not Modified", false, true);
    public static final HttpStatus USE_PROXY = new HttpStatus(305, "Use Proxy", false, true);
    public static final HttpStatus TEMPORARY_REDIRECT = new HttpStatus(307, "Temporary Redirect", false, true);
    public static final HttpStatus PERMANENT_REDIRECT = new HttpStatus(308, "Permanent Redirect", false, true);

    public static final HttpStatus BAD_REQUEST = new HttpStatus(400, "Bad Request", true, true);
    public static final HttpStatus UNAUTHORIZED = new HttpStatus(401, "Unauthorized", true, true);
    public static final HttpStatus PAYMENT_REQUIRED = new HttpStatus(402, "Payment Required", false, true);
    public static final HttpStatus FORBIDDEN = new HttpStatus(403, "Forbidden", false, true);
    public static final HttpStatus NOT_FOUND = new HttpStatus(404, "Not Found", true, true);
    public static final HttpStatus METHOD_NOT_ALLOWED = new HttpStatus(405, "Method Not Allowed", false, true);
    public static final HttpStatus NOT_ACCEPTABLE = new HttpStatus(406, "Not Acceptable", false, true);
    public static final HttpStatus PROXY_AUTHENTICATION_REQUIRED = new HttpStatus(407, "Proxy Authentication Required", false, true);
    public static final HttpStatus REQUEST_TIMEOUT = new HttpStatus(408, "Request Timeout", false, true);
    public static final HttpStatus CONFLICT = new HttpStatus(409, "Conflict", false, true);
    public static final HttpStatus GONE = new HttpStatus(410, "Gone", false, true);
    public static final HttpStatus LENGTH_REQUIRED = new HttpStatus(411, "Length Required", false, true);
    public static final HttpStatus PRECONDITION_FAILED = new HttpStatus(412, "Precondition Failed", false, true);
    public static final HttpStatus PAYLOAD_TOO_LARGE = new HttpStatus(413, "Payload Too Large", false, true);
    public static final HttpStatus URI_TOO_LONG = new HttpStatus(414, "URI Too Long", false, true);
    public static final HttpStatus UNSUPPORTED_MEDIA_TYPE = new HttpStatus(415, "Unsupported Media Type", false, true);
    public static final HttpStatus REQUESTED_RANGE_NOT_SATISFIABLE = new HttpStatus(416, "Requested range not satisfiable", false, true);
    public static final HttpStatus EXPECTATION_FAILED = new HttpStatus(417, "Expectation Failed", false, true);
    public static final HttpStatus I_AM_A_TEAPOT = new HttpStatus(418, "I'm a teapot", false, true);
    public static final HttpStatus INSUFFICIENT_SPACE_ON_RESOURCE = new HttpStatus(419, "Insufficient Space On Resource", false, true);
    public static final HttpStatus METHOD_FAILURE = new HttpStatus(420, "Method Failure", false, true);
    public static final HttpStatus DESTINATION_LOCKED = new HttpStatus(421, "Destination Locked", false, true);
    public static final HttpStatus UNPROCESSABLE_ENTITY = new HttpStatus(422, "Unprocessable Entity", false, true);
    public static final HttpStatus LOCKED = new HttpStatus(423, "Locked", false, true);
    public static final HttpStatus FAILED_DEPENDENCY = new HttpStatus(424, "Failed Dependency", false, true);
    public static final HttpStatus UPGRADE_REQUIRED = new HttpStatus(426, "Upgrade Required", false, true);
    public static final HttpStatus PRECONDITION_REQUIRED = new HttpStatus(428, "Precondition Required", false, true);
    public static final HttpStatus TOO_MANY_REQUESTS = new HttpStatus(429, "Too Many Requests", false, true);
    public static final HttpStatus REQUEST_HEADER_FIELDS_TOO_LARGE = new HttpStatus(431, "Request Header Fields Too Large", false, true);

    public static final HttpStatus INTERNAL_SERVER_ERROR = new HttpStatus(500, "Internal Server Error", true, true);
    public static final HttpStatus NOT_IMPLEMENTED = new HttpStatus(501, "Not Implemented", false, true);
    public static final HttpStatus BAD_GATEWAY = new HttpStatus(502, "Bad Gateway", false, true);
    public static final HttpStatus SERVICE_UNAVAILABLE = new HttpStatus(503, "Service Unavailable", true, true);
    public static final HttpStatus GATEWAY_TIMEOUT = new HttpStatus(504, "Gateway Timeout", false, true);
    public static final HttpStatus HTTP_VERSION_NOT_SUPPORTED = new HttpStatus(505, "HTTP Version not supported", false, true);
    public static final HttpStatus VARIANT_ALSO_NEGOTIATES = new HttpStatus(506, "Variant Also Negotiates", false, true);
    public static final HttpStatus INSUFFICIENT_STORAGE = new HttpStatus(507, "Insufficient Storage", false, true);
    public static final HttpStatus LOOP_DETECTED = new HttpStatus(508, "Loop Detected", false, true);
    public static final HttpStatus BANDWIDTH_LIMIT_EXCEEDED = new HttpStatus(509, "Bandwidth Limit Exceeded", false, true);
    public static final HttpStatus NOT_EXTENDED = new HttpStatus(510, "Not Extended", false, true);
    public static final HttpStatus NETWORK_AUTHENTICATION_REQUIRED = new HttpStatus(511, "Network Authentication Required", false, true);

    private final int value;

    private final String reasonPhrase;

    private final byte[] bytes;


    HttpStatus(int value, String reasonPhrase, boolean cache, boolean inner) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
        if (cache) {
            this.bytes = (value + " " + reasonPhrase + "\r\n").getBytes();
        } else {
            this.bytes = null;
        }
        if (inner) {
            int hash = value % HASH_TABLE.length;
            if (HASH_TABLE[hash] != null) {
                throw new IllegalArgumentException("Duplicate status code: " + value);
            }
            HASH_TABLE[hash] = this;
        }
    }

    public HttpStatus(int value, String reasonPhrase) {
        this(value, reasonPhrase, false, false);
    }

    /**
     * Return the integer value of this status code.
     */
    public int value() {
        return this.value;
    }


    /**
     * Return the reason phrase of this status code.
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public static HttpStatus valueOf(int value) {
        return HASH_TABLE[value % HASH_TABLE.length];
    }

    public void write(WriteBuffer writeBuffer) throws IOException {
        if (bytes == null) {
            writeBuffer.writeByte((byte) (value / 100 + '0'));
            writeBuffer.writeByte((byte) (value / 10 % 10 + '0'));
            writeBuffer.writeByte((byte) (value % 10 + '0'));
            writeBuffer.writeByte(FeatUtils.SP);
            writeBuffer.write(reasonPhrase.getBytes());
            writeBuffer.write(FeatUtils.CRLF_BYTES);
        } else {
            writeBuffer.write(bytes);
        }
    }
}
