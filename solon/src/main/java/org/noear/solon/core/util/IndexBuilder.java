package org.noear.solon.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.noear.solon.annotation.Inject;

public class IndexBuilder {

	public static Map<String, Integer> map = new HashMap<>();

	public static int buildIndex(Class clazz) {

		if (map.get(clazz.getSimpleName()) != null) {
			return map.get(clazz.getSimpleName());
		} else {
			// 找到他的依赖类
			List<Class> clazzList = new ArrayList<>();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Inject.class)) {
					Inject inject = field.getAnnotation(Inject.class);
					if (inject.value() != null) {
						clazzList.add(field.getType());
					}
				}
			}

			// 没有依赖类, 直接返回0
			if (clazzList.size() == 0) {
				map.put(clazz.getSimpleName(), 0);
				return 0;
			}

			// 找到依赖类中最小的index
			Integer miniIndex = null;
			for (Class clazzRelate : clazzList) {
				int index = buildIndex(clazzRelate);
				if (miniIndex == null) {
					miniIndex = index;
				} else if (miniIndex < index) {
					miniIndex = index;
				}
			}

			// 返回miniIndex+1
			map.put(clazz.getSimpleName(), miniIndex + 1);
			return miniIndex + 1;
		}

	}

}
