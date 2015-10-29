package org.cubekode.graphpojo.util;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReflectionUtils {

  public static Map<String, Field> lookupFields(Class<?> type) {
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

  public static MethodHandle findGetterHandler(Field field) throws NoSuchMethodException,
      IllegalAccessException {
    return MethodHandles.publicLookup().findVirtual(field.getDeclaringClass(),
        javaGetterName(field), MethodType.methodType(field.getType()));
  }

  public static Method findGetterMethod(Field field) throws NoSuchMethodException,
      IllegalAccessException {
    return field.getDeclaringClass().getMethod(javaGetterName(field));
  }

  private static String javaGetterName(Field field) {
    String name = field.getName();
    String preffix =
        field.getType() == Boolean.class || field.getType() == boolean.class ? "is" : "get";
    return preffix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  public static List<Method> findAnnotatedMethods(
      Class<?> type,
      Set<Class<? extends Annotation>> annotationTypes) {
    List<Method> methods = new LinkedList<>();
    for (Method method : type.getMethods()) {
      for (Class<? extends Annotation> annotationType : annotationTypes) {
        if (method.isAnnotationPresent(annotationType)) {
          methods.add(method);
        }
      }
    }
    return methods;
  }
}
