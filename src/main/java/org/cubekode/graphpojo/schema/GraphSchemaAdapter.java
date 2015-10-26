package org.cubekode.graphpojo.schema;

import java.lang.reflect.Field;

public interface GraphSchemaAdapter {

  boolean isActive();

  boolean validField(Field field);

  String fieldName(Field field);
}
