package org.cubekode.graphpojo.schema;

import java.lang.reflect.Field;

public interface GraphPojoMapperListener {

  boolean validField(Field field);
  
}
