package org.cubekode.graphpojo.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cubekode.graphpojo.util.ReflectionUtils;

public class GraphSchemaBuilder {

  private static final Set<Class<? extends Annotation>> GQL_ANNOTATIONS = new HashSet<>(
      Arrays.asList(GraphQuery.class, GraphRepository.class));

  private Map<Class<?>, String> types = new HashMap<>();

  public void register(Class<?> repositoryType) {

    for (Method method : ReflectionUtils.findAnnotatedMethods(repositoryType, GQL_ANNOTATIONS)) {

      addType(method.getGenericReturnType());

      if (method.isAnnotationPresent(GraphQuery.class)) {
        addQuery(method.getGenericReturnType());
      } else {
        addFetcherRelationship(method.getGenericReturnType());
      }
    }
  }

  private void addType(Type genericReturnType) {}

  private void addQuery(Type genericReturnType) {}

  private void addFetcherRelationship(Type genericReturnType) {}
}
