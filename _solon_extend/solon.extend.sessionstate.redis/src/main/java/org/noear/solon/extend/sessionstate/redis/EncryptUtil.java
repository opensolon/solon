package org.noear.solon.extend.sessionstate.redis;


import org.noear.solon.core.event.EventBus;

import java.security.MessageDigest;

class EncryptUtil {
    private static final char[] _hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String md5(String cleanData){
        return md5(cleanData,"utf-8");
    }

    public static String sha1(String cleanData){
        return sha1(cleanData,"utf-8");
    }

    /** 生成md5码 */
    public static String sha1(String cleanData, String chaerset) {
        return hashEncode("SHA-1", cleanData,chaerset);
    }

    public static String md5(String cleanData, String chaerset) {
        return hashEncode("MD5", cleanData,chaerset);
    }

    public static String md5Bytes(byte[] bytes) {
        try {
            return do_hashEncode("MD5", bytes);
        }catch (Exception ex){
            EventBus.push(ex);
            return null;
        }
    }

    private static String hashEncode(String algorithm, String cleanData, String chaerset) {

        try {
            byte[] btInput = cleanData.getBytes(chaerset);
            return do_hashEncode(algorithm,btInput);
        } catch (Exception ex) {
            EventBus.push(ex);
            return null;
        }
    }

    private static String do_hashEncode(String algorithm, byte[] btInput) throws Exception{
        MessageDigest mdInst = MessageDigest.getInstance(algorithm);
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;

        for (int i = 0; i < j; ++i) {
            byte byte0 = md[i];
            str[k++] = _hexDigits[byte0 >>> 4 & 15];
            str[k++] = _hexDigits[byte0 & 15];
        }

        return new String(str);
    }

}
