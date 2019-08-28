package com.h3c.iclouds.utils;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.validate.PatternType;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 利用反射机制设置页面请求参数
 * 
 * @author zkf5485
 *
 */
public class InvokeSetForm {

	/**
	 * 过滤含不设定标志位的值
	 * 
	 * @param param
	 * @return
	 */
	public static boolean noCopyParam(String param) {
		String[] unSetParams = new String[] { "update", "create" };
		for (String unSetParam : unSetParams) {
			if (param.indexOf(unSetParam) > -1) { // 含不设定字符标识则不设置
				return false;
			}
		}
		return true;
	}

	/**
	 * 实体类差异性对比，前者属性有值则覆盖后者属性
	 * 
	 * @param srcEntity
	 * @param targetEntity
	 * @return 返回覆盖之后的对象，用于更新
	 */
	public static Object copyFormProperties(Object srcEntity, Object targetEntity) {
		try {
			Field[] fields = srcEntity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组

			for (Field field : fields) {
				String type = field.getGenericType().toString(); // 获取属性的类型
				String name = field.getName(); // 获取属性的名字
				if (!noCopyParam(name)) {
					continue;
				}
				if ("id".equals(name)) { // id不做拷贝
					continue;
				}
				name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
				Method method = null;
				try {
					method = srcEntity.getClass().getMethod("get" + name);
				} catch (NoSuchMethodException e) {
					continue;
				}
				PatternType[] types = null;
				PatternType contains = null;
				boolean fk = false;
				InvokeAnnotate annotation = method.getAnnotation(InvokeAnnotate.class);
				if (annotation != null && method.isAnnotationPresent(InvokeAnnotate.class)) {
					if (annotation != null) {
						types = annotation.type();
						boolean unCopy = false;
						for (PatternType pt : types) {
							if (PatternType.UNCOPY == pt) { // 是否需要参数覆盖
								unCopy = true;
							} else if (PatternType.CONTAINS == pt) {
								contains = PatternType.CONTAINS;
							} else if (PatternType.FK == pt) {
								fk = true;
							}
						}
						if (unCopy) {
							continue;
						}
					}
				}
				if (type.equals("class java.lang.String")) {
					String value = (String) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null || fk) {
						if (contains != null && contains == PatternType.CONTAINS) {
							String[] values = annotation.values();
							for (int i = 0; i < values.length; i++) {
								if (values[i].equals(value)) {
									break;
								}
							}
							continue;
						}

						method = targetEntity.getClass().getMethod("set" + name, String.class);

						if (fk) { // 是外键，如果为空字符串，则value修改为null
							if (value != null && "".equals(value.trim())) {
								value = null;

							}
						}
						method.invoke(targetEntity, value);
					}
				} else if (type.equals("class java.lang.Integer")) {
					Integer value = (Integer) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null || fk) { //

						method = targetEntity.getClass().getMethod("set" + name, Integer.class);
						method.invoke(targetEntity, value);

					}
				} else if (type.equals("class java.lang.Double")) {
					Double value = (Double) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null) { //

						method = targetEntity.getClass().getMethod("set" + name, Double.class);
						method.invoke(targetEntity, value);

					}
				} else if (type.equals("class java.lang.Boolean")) {
					Boolean value = (Boolean) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null) { //

						method = targetEntity.getClass().getMethod("set" + name, Boolean.class);
						method.invoke(targetEntity, value);

					}
				} else if (type.equals("class java.util.Date")) {
					Date value = (Date) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null) { //

						method = targetEntity.getClass().getMethod("set" + name, Date.class);
						method.invoke(targetEntity, value);

					}
				} else if (type.equals("double")) {
					double value = (Double) method.invoke(srcEntity); // 调用getter方法获取属性值
					// if (value != 0.0) { //

					method = targetEntity.getClass().getMethod("set" + name, double.class);
					method.invoke(targetEntity, value);

					// }
				} else if (type.equals("int")) {
					int value = (Integer) method.invoke(srcEntity); // 调用getter方法获取属性值
					// if (value != 0) { //

					method = targetEntity.getClass().getMethod("set" + name, int.class);
					method.invoke(targetEntity, value);

					// }
				} else if (type.equals("char")) {
					char value = (Character) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != 0) { //

						method = targetEntity.getClass().getMethod("set" + name, char.class);
						method.invoke(targetEntity, value);

					}
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return targetEntity;
	}

	/**
	 * 前者属性比后者属性多，多的部分不进行赋值
	 * 
	 * @param srcEntity
	 * @param targetEntity
	 * @return 返回覆盖之后的对象，用于更新
	 */
	public static Object copyFormProperties1(Object srcEntity, Object targetEntity) {
		try {
			Field[] fields = srcEntity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
			List<String> targetFieldNames = new ArrayList<String>();
			for (Field field : targetEntity.getClass().getDeclaredFields()) {
				String name = field.getName();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				targetFieldNames.add(name);
			}
			for (Field field : fields) {
				String type = field.getGenericType().toString(); // 获取属性的类型
				String name = field.getName(); // 获取属性的名字
				if (!noCopyParam(name)) {
					continue;
				}
				if ("id".equals(name)) { // id不做拷贝
					continue;
				}
				name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
				Method method = null;
				try {
					method = srcEntity.getClass().getMethod("get" + name);
				} catch (NoSuchMethodException e) {
					continue;
				}
				PatternType[] types = null;
				PatternType contains = null;
				boolean fk = false;
				InvokeAnnotate annotation = method.getAnnotation(InvokeAnnotate.class);
				if (annotation != null && method.isAnnotationPresent(InvokeAnnotate.class)) {
					if (annotation != null) {
						types = annotation.type();
						boolean unCopy = false;
						for (PatternType pt : types) {
							if (PatternType.UNCOPY == pt) { // 是否需要参数覆盖
								unCopy = true;
							} else if (PatternType.CONTAINS == pt) {
								contains = PatternType.CONTAINS;
							} else if (PatternType.FK == pt) {
								fk = true;
							}
						}
						if (unCopy) {
							continue;
						}
					}
				}
				if (type.equals("class java.lang.String")) {
					String value = (String) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null || fk) {
						if (contains != null && contains == PatternType.CONTAINS) {
							String[] values = annotation.values();
							for (int i = 0; i < values.length; i++) {
								if (values[i].equals(value)) {
									break;
								}
							}
							continue;
						}
						if (targetFieldNames.contains(name)) {
							method = targetEntity.getClass().getMethod("set" + name, String.class);
							method.invoke(targetEntity, value);
						} else {
							continue;
						}
						if (fk) { // 是外键，如果为空字符串，则value修改为null
							if (value != null && "".equals(value.trim())) {
								value = null;
								method.invoke(targetEntity, value);
							}
						}

					}
				} else if (type.equals("class java.lang.Integer")) {
					Integer value = (Integer) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null || fk) { //
						if (targetFieldNames.contains(name)) {
							method = targetEntity.getClass().getMethod("set" + name, Integer.class);
							method.invoke(targetEntity, value);
						} else {
							continue;
						}

					}
				} else if (type.equals("class java.lang.Double")) {
					Double value = (Double) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null) { //
						if (targetFieldNames.contains(name)) {
							method = targetEntity.getClass().getMethod("set" + name, Double.class);
							method.invoke(targetEntity, value);
						} else {
							continue;
						}

					}
				} else if (type.equals("class java.lang.Boolean")) {
					Boolean value = (Boolean) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null) { //
						if (targetFieldNames.contains(name)) {
							method = targetEntity.getClass().getMethod("set" + name, Boolean.class);
							method.invoke(targetEntity, value);
						} else {
							continue;
						}

					}
				} else if (type.equals("class java.util.Date")) {
					Date value = (Date) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != null) { //
						if (targetFieldNames.contains(name)) {
							method = targetEntity.getClass().getMethod("set" + name, Date.class);
							method.invoke(targetEntity, value);
						} else {
							continue;
						}

					}
				} else if (type.equals("double")) {
					double value = (Double) method.invoke(srcEntity); // 调用getter方法获取属性值
					// if (value != 0.0) { //
					if (targetFieldNames.contains(name)) {
						method = targetEntity.getClass().getMethod("set" + name, double.class);
						method.invoke(targetEntity, value);
					} else {
						continue;
					}

					// }
				} else if (type.equals("int")) {
					int value = (Integer) method.invoke(srcEntity); // 调用getter方法获取属性值
					// if (value != 0) { //
					if (targetFieldNames.contains(name)) {
						method = targetEntity.getClass().getMethod("set" + name, int.class);
						method.invoke(targetEntity, value);
					} else {
						continue;
					}

					// }
				} else if (type.equals("char")) {
					char value = (Character) method.invoke(srcEntity); // 调用getter方法获取属性值
					if (value != 0) { //
						if (targetFieldNames.contains(name)) {
							method = targetEntity.getClass().getMethod("set" + name, char.class);
							method.invoke(targetEntity, value);
						} else {
							continue;
						}
					}
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return targetEntity;
	}

	/**
	 * map的key对应实体属性，自动设置实体属性
	 * 
	 * @param map
	 * @param model
	 * @return
	 */
	public static Object settingForm(Map<String, Object> map, Object model) {
		Field[] fields = model.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		try {
			for (String mapKey : map.keySet()) {
				if (StrUtils.checkParam(map.get(mapKey))) {
					for (Field field : fields) {
						String fieldName = field.getName(); // 获取属性的名字
						if (fieldName.contains("created") || fieldName.contains("updated")) {
							continue;
						}

						if (mapKey.equalsIgnoreCase(fieldName)) {
							fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1); // 将属性的首字符大写，方便构造get，set方法
							String type = field.getGenericType().toString(); // 获取属性的类型
							// 属性内容为空则不做处理
							String value = StrUtils.tranString(map.get(mapKey));
							if (value != null && !"".equals(value.trim())) {
								// 如果type是类类型，则前面包含"class "，后面跟类名
								if (type.equals("class java.lang.String")) {
									Method m = model.getClass().getMethod("set" + fieldName, String.class);
									if (m != null) {
										m.invoke(model, value);
									}
								} else if (type.equals("class java.lang.Integer")) {
									Method m = model.getClass().getMethod("set" + fieldName, Integer.class);
									if (m != null && !"".equals(value)) {
										m.invoke(model, new Integer(value));
									}
								} else if (type.equals("class java.lang.Double")) {
									Method m = model.getClass().getMethod("set" + fieldName, Double.class);
									if (m != null && !"".equals(value)) {
										m.invoke(model, new Double(value));
									}
								} else if (type.equals("class java.lang.Boolean")) {
									Method m = model.getClass().getMethod("set" + fieldName, Boolean.class);
									if (m != null) {
										m.invoke(model, Boolean.valueOf(value));
									}
								} else if (type.equals("class java.util.Date")) {
									Method m = model.getClass().getMethod("set" + fieldName, Date.class);
									if (m != null && !"".equals(value)) {
										String format = null;
										if (value.length() == 10) { // 根据字符串长度判断日期转化类型
											format = DateUtils.dateFormat;
										} else if (value.length() == 8) {
											format = DateUtils.timeFormat;
										}
										Date date = DateUtils.getDateByString(value, format);
										m.invoke(model, date);
									}
								} else if (type.equals("double")) {
									Method m = model.getClass().getMethod("set" + fieldName, double.class);
									if (m != null) {
										m.invoke(model, Double.valueOf("".equals(value) ? "0" : value));
									}
								} else if (type.equals("int")) {
									Method m = model.getClass().getMethod("set" + fieldName, int.class);
									if (m != null) {
										m.invoke(model, Integer.valueOf("".equals(value) ? "0" : value));
									}
								} else if (type.equals("char")) {
									Method m = model.getClass().getMethod("set" + fieldName, char.class);
									if (m != null) {
										m.invoke(model, value);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	public static Map<String, Object> tranClassToMap(Object object) {
		@SuppressWarnings("rawtypes")
		Class type = object.getClass();
		Map<String, Object> returnMap = new HashMap<>();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(type);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		if (beanInfo != null) {
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				if (!propertyName.equals("class")) {
					Method readMethod = descriptor.getReadMethod();
					Object result = null;
					try {
						result = readMethod.invoke(object, new Object[0]);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					if (result != null) {
						returnMap.put(propertyName, result);
					}
				}
			}
		}
		return returnMap;
	}
	
	/**
	 * 将json转为对象（忽略大小写和下划线）
	 * @param json
	 * @param entity
	 * @return
	 */
	public static Object tranJson2Object(JSONObject json, Object entity) {
		Field[] fields = entity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		try {
			Set<String> keys = json.keySet();
			Map<String, Object> map = new HashMap<>();
			for (String key : keys) {
				String s = key.replace("_", "").toLowerCase();//去掉下划线
				map.put(s, json.get(key));
			}
			json.clear();
			json.putAll(map);
			for (Field field : fields) {
				String fieldName = field.getName();
				if (fieldName.contains("created") || fieldName.contains("updated")) {
					continue;
				}
				//没有值和不包括该属性时
				if (!json.containsKey(fieldName.toLowerCase()) || !StrUtils.checkParam(json.get(fieldName.toLowerCase()))) {
					continue;
				}
				String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1); //
				// 将属性的首字符大写，方便构造get，set方法
				String type = field.getGenericType().toString(); // 获取属性的类型
				// 如果type是类类型，则前面包含"class "，后面跟类名
				if (type.equals("class java.lang.String")) {
					Method m = entity.getClass().getMethod("set" + methodName, String.class);
					if (m != null) {
						m.invoke(entity, json.getString(fieldName.toLowerCase()));
					}
				} else if (type.equals("class java.lang.Long")) {
					Method m = entity.getClass().getMethod("set" + methodName, Long.class);
					if (m != null) {
						m.invoke(entity, json.getLong(fieldName.toLowerCase()));
					}
				} else if (type.equals("class java.lang.Integer")) {
					Method m = entity.getClass().getMethod("set" + methodName, Integer.class);
					if (m != null) {
						m.invoke(entity, json.getInteger(fieldName.toLowerCase()));
					}
				} else if (type.equals("class java.lang.Double")) {
					Method m = entity.getClass().getMethod("set" + methodName, Double.class);
					if (m != null) {
						m.invoke(entity, json.getDouble(fieldName.toLowerCase()));
					}
				} else if (type.equals("class java.lang.Boolean")) {
					Method m = entity.getClass().getMethod("set" + methodName, Boolean.class);
					if (m != null) {
						m.invoke(entity, json.getBoolean(fieldName.toLowerCase()));
					}
				} else if (type.equals("class java.util.Date")) {
					Method m = entity.getClass().getMethod("set" + methodName, Date.class);
					if (m != null) {
						m.invoke(entity, json.getDate(fieldName.toLowerCase()));
					}
				} else if (type.equals("double")) {
					Method m = entity.getClass().getMethod("set" + methodName, double.class);
					if (m != null) {
						m.invoke(entity, json.getDoubleValue(fieldName.toLowerCase()));
					}
				} else if (type.equals("int")) {
					Method m = entity.getClass().getMethod("set" + methodName, int.class);
					if (m != null) {
						m.invoke(entity, json.getIntValue(fieldName.toLowerCase()));
					}
				} else if (type.equals("long")) {
					Method m = entity.getClass().getMethod("set" + methodName, long.class);
					if (m != null) {
						m.invoke(entity, json.getLongValue(fieldName.toLowerCase()));
					}
				} else if (type.equals("char")) {
					Method m = entity.getClass().getMethod("set" + methodName, char.class);
					if (m != null) {
						m.invoke(entity, json.getString(fieldName.toLowerCase()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}
	
}
