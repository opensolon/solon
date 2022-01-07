package org.noear.solon.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.noear.solon.annotation.Inject;

/**
 * 构建初始化的index
 * @author cym1102
 *
 */
public class IndexBuilder {

	public static Map<String, Integer> map = new HashMap<>();
	public static ArrayList<String> classNameSet = new ArrayList<>();

	public static int buildIndex(Class<?> clazz) {
		return buildIndex(clazz, true);
	}

	/**
	 * 
	 * @param clazz bean类
	 * @param stackTop 是否为查找起始
	 * @return 顺序index
	 */
	public static int buildIndex(Class<?> clazz, Boolean stackTop) {
		if (stackTop) {
			classNameSet.clear();
		} else {
			if (classNameSet.contains(clazz.getName())) {
				String link = "";
				for(String name:classNameSet) {
					link += (name + " -> ");
				}
				link += classNameSet.get(0); 
				throw new RuntimeException("发生依赖循环:" + link);
			}
		}
		classNameSet.add(clazz.getName());
		
		if (map.get(clazz.getName()) != null) {
			return map.get(clazz.getName());
		} else {
			// 找到他的依赖类
			List<Class<?>> clazzList = new ArrayList<>();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Inject.class)) {
					Inject inject = field.getAnnotation(Inject.class);
					if (inject.value().contains("${")) {
						// 注入的是参数, 略过
						continue;
					}

					clazzList.add(field.getType());
				}
			}

			// 没有依赖类, 直接返回0
			if (clazzList.size() == 0) {
				map.put(clazz.getName(), 0);
				return 0;
			}

			// 找到依赖类中最小的index
			Integer miniIndex = null;
			for (Class<?> clazzRelate : clazzList) {
				Integer index = buildIndex(clazzRelate, false);

				if (miniIndex == null) {
					miniIndex = index;
				} else if (miniIndex < index) {
					miniIndex = index;
				}
			}

			// 返回miniIndex + 1
			map.put(clazz.getName(), miniIndex + 1);
			return miniIndex + 1;
		}

	}

}
