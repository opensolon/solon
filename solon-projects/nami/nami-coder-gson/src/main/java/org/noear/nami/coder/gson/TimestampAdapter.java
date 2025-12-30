package org.noear.nami.coder.gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
/**
 * 秒时间戳转换为Date,时间戳的单位是毫秒
 * 字符串转换为Date
 * @author cqyhm
 * @since 2025年12月30日20:46:06
 */
public class TimestampAdapter implements JsonDeserializer<Date>,JsonSerializer<Date> {
	private static final List<String> PATTERNS = Arrays.asList(
	        "yyyy-MM-dd",
	        "yyyy-MM-dd HH:mm:ss",
	        "yyyy/MM/dd HH:mm:ss");
    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext ctx) throws JsonParseException {
    	long seconds;
        // 1. 字符串
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
        	String v = json.getAsString();
	        for (String p : PATTERNS) {
	            try {
	                return new SimpleDateFormat(p, Locale.CHINA).parse(v);
	            } catch (ParseException ignore) { continue;}
	        }
	        seconds = Long.parseLong(json.getAsString());
        }
        // 2. 数字
        else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            seconds = json.getAsLong();
        } else {
            throw new JsonParseException("时间戳必须是数字或纯数字字符串，当前=" + json);
        }
        return new Date(seconds);   //毫秒
    }

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getTime()); // 单位毫秒
	}

}
