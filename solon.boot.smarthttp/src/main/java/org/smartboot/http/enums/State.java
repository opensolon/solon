package org.smartboot.http.enums;

public enum State {
    method,
    uri,
    protocol,
    request_line_end,
    head_name,
    head_value,
    head_line_LF,
    head_line_end,
    head_finished,
    body,
    finished;
}