/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HeaderValue.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.utils.StringUtils;

/**
 * 支持多Value的Header
 *
 * @author 三刀
 * @version V1.0 , 2019/11/30
 */
class HeaderValue {
    /**
     * name
     */
    private String name;
    /**
     * Value 值
     */
    private String value;


    private int valuePosition;

    private int valueLength;

    private char[] charArray;
    /**
     * 同名Value
     */
    private HeaderValue nextValue;

    public HeaderValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        if (value == null) {
            value = StringUtils.convertToString(charArray, valuePosition, valueLength, StringUtils.String_CACHE_HEADER_VALUE);
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int position, int length) {
        this.valuePosition = position;
        this.valueLength = length;
    }

    public void setCharArray(char[] charArray) {
        this.charArray = charArray;
    }

    public HeaderValue getNextValue() {
        return nextValue;
    }

    public void setNextValue(HeaderValue nextValue) {
        this.nextValue = nextValue;
    }
}
