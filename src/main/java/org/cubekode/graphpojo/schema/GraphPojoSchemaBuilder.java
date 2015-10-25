package org.cubekode.graphpojo.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.cubekode.graphpojo.util.ReflectionUtils;

public class GraphPojoSchemaBuilder {

  static class TypeField {

    String name;
    Field field;
    DataFetcher fetcher;

    TypeField(String name, Field field, DataFetcher fetcher) {
      this.name = name;
      this.field = field;
      this.fetcher = fetcher;
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + " name=" + name + ", field=" + field + ", fetcher="
          + fetcher;
    }
  }

  static class ListTypeField extends TypeField {

    Class<?> listType;

    public ListTypeField(String name, Field field, Class<?> listType, DataFetcher fetcher) {
      super(name, field, fetcher);
      this.listType = listType;
    }

    @Override
    public String toString() {
      return super.toString() + ", listType=" + listType;
    }
  }

  static class ObjectTypeField extends TypeField {

    public ObjectTypeField(String name, Field field, DataFetcher fetcher) {
      super(name, field, fetcher);
    }
  }

  static class TypeMapping {

    String name;
    Class<?> type;
    DataFetcher fetcher;
    Map<String, TypeField> fields;
    boolean internal;
    // graphql mapped type
    GraphQLObjectType objectType;

    public TypeMapping(String name, Class<?> type, DataFetcher fetcher, boolean internal) {
      this.name = name;
      this.type = type;
      this.fetcher = fetcher;
      this.internal = internal;
    }

    public void print() {
      System.out.println(" - Type [name:" + name + ", internal:" + internal + ", type:" + type
          + "]");
      System.out.println(fields.entrySet().stream().map((s) -> "\n -- " + s)
          .collect(Collectors.toList()));
    }
  }

  private Map<Class<?>, TypeMapping> mappings = new HashMap<>();

  public TypeMapping add(Class<?> type, DataFetcher fetcher) {
    return add(type.getSimpleName(), type, fetcher);
  }

  public TypeMapping add(String name, Class<?> type, DataFetcher fetcher) {
    TypeMapping mapping = mappings.get(type);
    if (mapping != null) {
      if (mapping.internal) {
        mapping.fetcher = fetcher;
        mapping.internal = false;
        return mapping;
      }
      throw new IllegalArgumentException("Duplicate definition of " + type);
    }
    return mapClass(name, type, fetcher, false);
  }

  private TypeMapping mapClass(String name, Class<?> type, DataFetcher fetcher, boolean internal) {

    TypeMapping mapping = mappings.get(type);

    if (mapping == null) {
      mapping = new TypeMapping(name, type, fetcher, internal);
      // insert type to avoid recursion
      mappings.put(type, mapping);
      // search relationship in filds
      mapFields(mapping);
    }
    return mapping;
  }

  private void mapFields(TypeMapping mapping) {

    Map<String, TypeField> fields = new HashMap<>();

    for (Field field : lookupFields(mapping.type).values()) {

      String name = field.getName();

      if (isPrimitiveValue(field.getType())) {
        fields.put(name, createSimpleField(field, name));
      } else if (List.class.isAssignableFrom(field.getType())) {
        fields.put(name, createListField(name, field));
      } else {
        fields.put(name, createObjectField(name, field));
      }
    }
    mapping.fields = fields;
  }

  private TypeField createSimpleField(Field field, String name) {
    return new TypeField(name, field, null);
  }

  private TypeField createListField(String name, Field field) {

    Type genericType = field.getGenericType();

    if (genericType instanceof ParameterizedType) {
      Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
      if (type instanceof Class) {
        Class<?> typeClass = (Class<?>) type;
        if (!isPrimitiveValue(typeClass)) {
          // internal objecting map
          mapClass(name, typeClass, null, true);
        }
        return new ListTypeField(name, field, typeClass, null);
      }
      throw new IllegalStateException("Generics is needed to be a raw type at " + field);
    }
    throw new IllegalStateException("Generics is mandatory for List type at " + field);
  }

  private TypeField createObjectField(String name, Field field) {
    // internal object mapping
    mapClass(name, field.getType(), null, true);
    return new ObjectTypeField(name, field, (DataFetcher) null);
  }

  private Map<String, Field> lookupFields(Class<?> type) {
    return ReflectionUtils.getFields(type);
  }

  private boolean isPrimitiveValue(Class<?> type) {
    return ScalarsExtension.isScalarType(type);
  }

  void print() {
    for (Entry<Class<?>, TypeMapping> e : mappings.entrySet()) {
      System.out.println("Mapping: " + e.getKey());
      e.getValue().print();
    }
  }

  public GraphPojoSchema build() {
    return new GraphPojoSchema(mappings);
  }
}
