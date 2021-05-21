package org.noear.captcha.solon.plugin.util;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceScaner;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ImageUtils {
    /** 滑块底图 **/
    private static Map<String, String> originalCacheMap = new ConcurrentHashMap();
    /** 滑块 **/
    private static Map<String, String> slidingBlockCacheMap = new ConcurrentHashMap();
    /** 点选文字 **/
    private static Map<String, String> picClickCacheMap = new ConcurrentHashMap();

    public static void cacheImage(String captchaOriginalPathJigsaw, String captchaOriginalPathClick) {
        //滑动拼图
        originalCacheMap.putAll(getImagesFile(captchaOriginalPathJigsaw + File.separator + "original"));
        slidingBlockCacheMap.putAll(getImagesFile(captchaOriginalPathJigsaw + File.separator + "slidingBlock"));
        picClickCacheMap.putAll(getImagesFile(captchaOriginalPathClick));
    }

    public static BufferedImage getOriginal() {
        int randomNum = RandomUtils.getRandomInt(1, originalCacheMap.size() + 1);
        String s = originalCacheMap.get("bg".concat(String.valueOf(randomNum)).concat(".png"));
        return getBase64StrToImage(s);
    }

    public static BufferedImage getslidingBlock() {
        int randomNum = RandomUtils.getRandomInt(1, slidingBlockCacheMap.size() + 1);
        String s = slidingBlockCacheMap.get(String.valueOf(randomNum).concat(".png"));
        return getBase64StrToImage(s);
    }

    public static BufferedImage getPicClick() {
        int randomNum = RandomUtils.getRandomInt(1, picClickCacheMap.size() + 1);
        String s = picClickCacheMap.get("bg".concat(String.valueOf(randomNum)).concat(".png"));
        return getBase64StrToImage(s);
    }

    /**
     * 图片转base64 字符串
     *
     * @param templateImage
     * @return
     */
    public static String getImageToBase64Str(BufferedImage templateImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(templateImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encodeBuffer(bytes).trim();
    }

    /**
     * base64 字符串转图片
     *
     * @param base64String
     * @return
     */
    public static BufferedImage getBase64StrToImage(String base64String) {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] bytes = base64Decoder.decodeBuffer(base64String);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getImagesFile(String path) {
        Map<String, String> imgMap = new HashMap();
        try {
            Set<String> map = ResourceScaner.scan(path, s->true);
            map.forEach(m-> {
                URL resourceObj = Utils.getResource(m);
                try {
                    InputStream stream = resourceObj.openStream();
                    byte[] bytes = Utils.transfer(stream, new ByteArrayOutputStream()).toByteArray();
                    String encode = new BASE64Encoder().encode(bytes);
                    String[] split = m.split("/");
                    String name = split[split.length-1];
                    imgMap.put(name, encode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgMap;
    }
}

