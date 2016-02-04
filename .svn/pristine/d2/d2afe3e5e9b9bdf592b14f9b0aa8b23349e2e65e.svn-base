package org.sagarius.radix.server.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class BaseEntity implements Serializable, JSONAware {
	protected Map<String, Object[]> fieldList;

	public abstract String render();

	public BaseEntity(Map<String, Object[]> fieldList) {
		this.fieldList = fieldList;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void parse(String json, Class<? extends BaseEntity> clazz) {
		parse((JSONArray) JSONValue.parse(json), clazz);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void parse(JSONArray array, Class<? extends BaseEntity> clazz) {
		int size = array.size();
		while (!clazz.isInterface() && !clazz.equals(BaseEntity.class)) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
					continue;
				}
				String fieldName = field.getName();
				if (fieldName.startsWith("jdo")) {
					continue;
				}
				Object[] fieldMeta = fieldList.get(fieldName);
				if (fieldMeta == null) {
					continue;
				}
				Integer index = (Integer) fieldMeta[0];
				if (index >= size) {
					continue;
				}
				Object jsonValue = array.get(index);
				if (jsonValue == null) {
					continue;
				}
				Class<?> type = field.getType();
				Method method = null;
				try {
					method = clazz.getMethod(
							"set" + StringUtils.capitalize(fieldName), type);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				try {
					if (type.equals(Key.class)) {
						method.invoke(this,
								KeyFactory.stringToKey((String) jsonValue));
					} else if (type.isEnum()) {
						Enum enumValue = Enum.valueOf(
								(Class<? extends Enum>) type,
								(String) jsonValue);
						method.invoke(this, enumValue);
					} else if (type.equals(Date.class)) {
						method.invoke(this, new Date((Long) jsonValue));
					} else if (type.isAssignableFrom(BaseEntity.class)) {
						method.invoke(this,
								type.getConstructor(JSONArray.class)
										.newInstance(jsonValue));
					} else {
						method.invoke(this, jsonValue);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(fieldName);
					continue;
				}
			}
			clazz = (Class<? extends BaseEntity>) clazz.getSuperclass();
		}
		for (int i = 0; i < size; i++) {
			Object jsonValue = array.get(i);
			if (jsonValue == null) {
				continue;
			}

		}
	}

	public String render(Class<? extends BaseEntity> clazz) {
		JSONArray array = toJSON(clazz);
		return array.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public JSONArray toJSON(Class<? extends BaseEntity> clazz) {
		JSONArray array = new JSONArray();
		while (!clazz.isInterface() && !clazz.equals(BaseEntity.class)) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
					continue;
				}
				String fieldName = field.getName();
				if (fieldName.startsWith("jdo")) {
					continue;
				}
				Object value = null;
				try {
					value = clazz.getMethod(
							"get" + StringUtils.capitalize(fieldName)).invoke(
							this);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(fieldName);
					continue;
				}
				if (value == null) {
					continue;
				}
				Object[] fieldProperties = fieldList.get(fieldName);
				if (fieldProperties == null) {
					continue;
				}
				Integer index = (Integer) fieldProperties[0];
				int size = array.size();
				if (size <= index) {
					for (int i = 0; i <= index; i++) {
						array.add(null);
					}
				}
				if (value instanceof Key) {
					array.set(index, (KeyFactory.keyToString((Key) value)));
				} else if (value instanceof Date) {
					array.set(index, ((Date) value).getTime());
				} else if (value instanceof Enum<?>) {
					array.set(index, ((Enum<?>) value).name());
				} else if (value instanceof BaseEntity) {
					array.set(index, ((BaseEntity) value)
							.toJSON((Class<? extends BaseEntity>) value
									.getClass()));
				} else {
					array.set(index, value);
				}
			}
			clazz = (Class<? extends BaseEntity>) clazz.getSuperclass();
		}
		return array;
	}

	@Override
	public String toJSONString() {
		return render();
	}

	public abstract Metadata getMeta();

	public abstract void setMeta(Metadata metadata);
}
