package org.noear.nami.coder.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.noear.nami.Encoder;

public class GsonEncoderTest {
	// 测试对象编码
	@Test
	void testEncodeObject() throws Throwable {
		Encoder encoder=GsonEncoder.instance;
		User user=new User(100, "zhang", "张三");
		byte[] bytes = encoder.encode(user);
		assertTrue(bytes.length>0);
		String str1=new String(bytes);
		String str2="{\"id\":100,\"code\":\"zhang\",\"name\":\"张三\"}";
		assertEquals(str1, str2);
	}
	// 测试列表编码
	@Test
	void testEncodeList() throws Throwable {
		Encoder encoder=GsonEncoder.instance;
		List<User> users=new ArrayList<>();
		users.add(new User(100, "zhang", "张三"));
		users.add(new User(200, "wang", "王东"));
		byte[] bytes = encoder.encode(users);
		assertTrue(bytes.length>0);
		String str1=new String(bytes);
		String str2="[{\"id\":100,\"code\":\"zhang\",\"name\":\"张三\"},{\"id\":200,\"code\":\"wang\",\"name\":\"王东\"}]";
		assertEquals(str1, str2);
	}
}
