package org.cubekode.graphpojo.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectionUtils {

  public static Map<String, Field> getFields(Class<?> type) {
    Map<String, Field> fields = new LinkedHashMap<>();
    while (type != null && !Object.class.equals(type)) {
      for (Field field : type.getDeclaredFields()) {
        if (!Modifier.isStatic(field.getModifiers())) {
          fields.putIfAbsent(field.getName(), field);
        }
      }
      type = type.getSuperclass();
    }
    return fields;
  }
}
