package org.cubekode.graphpojo.schema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class GraphPojoMapperListenerImpl implements GraphPojoMapperListener {

  private static Class<? extends Annotation> jacksonIgnoreField;

  static {
    try {
      jacksonIgnoreField =
          (Class<? extends Annotation>) Class
              .forName("com.fasterxml.jackson.annotation.JsonIgnore");
    } catch (ClassNotFoundException expected) {
    }
  }

  @Override
  public boolean validField(Field field) {
    return !(jacksonIgnoreField != null && field.isAnnotationPresent(jacksonIgnoreField));
  }
}
