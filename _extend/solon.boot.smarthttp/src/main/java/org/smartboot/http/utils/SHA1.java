/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: SHA1.java
 * Date: 2020-03-29
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.utils;

import java.security.MessageDigest;


public class SHA1 {

    public static byte[] encode(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            return messageDigest.digest(str.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}