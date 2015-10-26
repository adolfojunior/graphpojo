package org.cubekode.graphpojo.schema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class JacksonAnnotationsAdapter implements GraphSchemaAdapter {

  private static Class<? extends Annotation> JACKSON_IGNORE;
  private static Class<? extends Annotation> JACKSON_PROPERTY;
  private static boolean ACTIVE;

  static {
    try {
      JACKSON_IGNORE =
          (Class<? extends Annotation>) Class
              .forName("com.fasterxml.jackson.annotation.JsonIgnore");
      JACKSON_PROPERTY =
          (Class<? extends Annotation>) Class
              .forName("com.fasterxml.jackson.annotation.JsonProperty");
      ACTIVE = true;
    } catch (ClassNotFoundException expected) {
    }
  }

  public boolean isActive() {
    return ACTIVE;
  }

  @Override
  public boolean validField(Field field) {
    return !isAnnotatedWith(field, JACKSON_IGNORE);
  }

  public String fieldName(Field field) {
    String name = null;
    if (isAnnotatedWith(field, JACKSON_PROPERTY)) {
      name = getAnnotationConfig(field, field.getAnnotation(JACKSON_PROPERTY), "value");
    }
    return name == null || name.isEmpty() ? field.getName() : name;
  }

  private boolean isAnnotatedWith(Field field, Class<? extends Annotation> annotation) {
    return annotation != null && field.isAnnotationPresent(annotation);
  }

  private <T> T getAnnotationConfig(Field field, Annotation annotation, String configuration) {
    try {
      return (T) annotation.getClass().getMethod("value").invoke(annotation);
    } catch (Exception e) {
      throw new IllegalStateException("Jackson integration problemn", e);
    }
  }

}
