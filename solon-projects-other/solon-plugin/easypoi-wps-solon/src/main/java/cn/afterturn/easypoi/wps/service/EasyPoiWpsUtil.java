package cn.afterturn.easypoi.wps.service;

import cn.hutool.json.JSONUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * Wps相关的工具类
 *
 * @author jueyue on 20-5-8.
 */
public class EasyPoiWpsUtil {



    public static String getKeyValueStr(Map<String, String> params) {
        List<String> keys = new ArrayList<String>() {
            {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    add(entry.getKey());
                }
            }
        };
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key) + "&";
            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }

    public static String getSignature(Map<String, String> params, String appSecret) {
        List<String> keys = new ArrayList<String>() {
            {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    add(entry.getKey());
                }
            }
        };

        // 将所有参数按key的升序排序
        keys.sort(String::compareTo);

        // 构造签名的源字符串
        StringBuilder contents = new StringBuilder();
        for (String key : keys) {
            if (key.equals("_w_signature")) {
                continue;
            }
            contents.append(key).append("=").append(params.get(key));
        }
        contents.append("_w_secretkey=").append(appSecret);

        // 进行hmac sha1 签名
        byte[] bytes = HmacUtils.hmacSha1(appSecret.getBytes(), contents.toString().getBytes());

        //字符串经过Base64编码
        String sign = encodeBase64String(bytes);
        try {
            return URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> paramToMap(String paramStr) {
        String[] params = paramStr.split("&");
        return new HashMap<String, String>() {
            {
                for (String param1 : params) {
                    String[] param = param1.split("=");
                    if (param.length >= 2) {
                        String        key   = param[0];
                        StringBuilder value = new StringBuilder(param[1]);
                        for (int j = 2; j < param.length; j++) {
                            value.append("=").append(param[j]);
                        }
                        put(key, value.toString());
                    }
                }
            }
        };
    }

    public static String getGMTDate(){
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'" , Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        String str = sdf.format(cd.getTime());
        return str;
    }

    /**
     * 计算MD5
     * @param  paramMap 参加运算的参数，调转换接口时，参数的顺序需与调用API时一致；调查询接口时，传null
     *
     */
    public static String getMD5(Map<String, Object> paramMap) {
            String req = "";
            if (paramMap != null){
                req = JSONUtil.toJsonStr(paramMap);
            }
            String md5Value = DigestUtils.md5Hex(req);
            return md5Value;
    }

    /**
     * 生成签名
     * @param action GET、POST
     * @param url 调用接口的url，转换接口时传入接口地址不带参；查询接口时地址带参数
     * @param contentMd5 通过getMD5方法计算的值
     * @param headerDate 通过getGMTDate方法计算的值
     * */
    public static String getSignature(String action, String url, String contentMd5, String headerDate, String contentType, String appsecret) throws MalformedURLException {
            String req = getUri(url);
            String signStr = action + "\n" + contentMd5 + "\n" + contentType + "\n" + headerDate + "\n" + req ;
            // 进行hmac sha1 签名
            byte[] bytes = HmacUtils.hmacSha1(appsecret.getBytes(), signStr.toString().getBytes());
            String sign = encodeBase64String(bytes); //
            return sign;

    }

    /**
     * 取得地址的uri
     * （去掉地址中的域名）
     * */
    private static String getUri(String link) throws MalformedURLException {
            URL url = new URL(link);
            String key = url.getPath();
            if (!StringUtils.isEmpty(url.getQuery())){
                key = key + "?"+ url.getQuery();
            }
            return key;
    }
}
