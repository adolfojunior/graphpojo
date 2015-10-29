package org.cubekode.graphpojo.schema;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.cubekode.graphpojo.util.ReflectionUtils;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;

public class GraphPojoBuilder {

  static class PojoProperty {

	  String name;
	    Field field;
	    DataFetcher fetcher;
	    boolean isTypeReference;

	    PojoProperty(String name, Field field, DataFetcher fetcher, boolean isTypeReference) {
	      this.name = name;
	      this.field = field;
	      this.fetcher = fetcher;
	      this.isTypeReference = isTypeReference;
	    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + " name=" + name + ", field=" + field + ", fetcher="
          + fetcher + " isTypeReference=" + isTypeReference;
    }
  }

  static class ListProperty extends PojoProperty {

    Class<?> listType;

    public ListProperty(String name, Field field, Class<?> listType, DataFetcher fetcher, boolean isTypeReference) {
        super(name, field, fetcher, isTypeReference);
        this.listType = listType;
      }

    @Override
    public String toString() {
      return super.toString() + ", listType=" + listType;
    }
  }

  static class RelationshipProperty extends PojoProperty {

	  public RelationshipProperty(String name, Field field, DataFetcher fetcher, boolean isTypeReference) {
	      super(name, field, fetcher, isTypeReference);
	    }
  }

  static class TypeMapping {

    String name;
    String queryName;
    String queryListName;
    Class<?> type;
    DataFetcher fetcher;
    Map<String, PojoProperty> fields;
    boolean internal;

    // graphql mapped type
    GraphQLObjectType objectType;
    GraphQLInputObjectType argumentType;

    public TypeMapping(String name, String queryName, String queryListName, Class<?> type, DataFetcher fetcher, boolean internal) {
      this.name = name;
      this.queryName = queryName;
      this.queryListName = queryListName;
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

  private GraphSchemaAdapter schemaAdapter = new JacksonAnnotationsAdapter();

  private PropertyFetcherStrategy fetcherStrategy = PropertyFetcherStrategies.FIELD_REFLECTION;

  private Map<Class<?>, TypeMapping> mappings = new HashMap<>();

  public GraphPojoBuilder fetcherStrategy(PropertyFetcherStrategy fetcherStrategy) {
    this.fetcherStrategy = fetcherStrategy;
    return this;
  }

  public GraphPojoBuilder add(Class<?> type, DataFetcher fetcher) {
    add(type.getSimpleName(), type.getSimpleName(), type.getSimpleName(), type, fetcher);
    return this;
  }
  
  public GraphPojoBuilder add(String name, Class<?> type, DataFetcher fetcher) {
    add(name, name, name, type, fetcher);
    return this;
  }

  public GraphPojoBuilder add(String name, String queryName, String queryListName, Class<?> type, DataFetcher fetcher) {
    TypeMapping mapping = mappings.get(type);
    if (mapping != null) {
      if (mapping.internal) {
        mapping.fetcher = fetcher;
        mapping.internal = false;
      } else {
        throw new IllegalArgumentException("Duplicate definition of " + type);
      }
    } else {
      mapClass(name, queryName, queryListName, type, fetcher, false);
    }
    return this;
  }

  private TypeMapping mapClass(String name, String queryName, String queryListName, Class<?> type, DataFetcher fetcher, boolean internal) {

    TypeMapping mapping = mappings.get(type);

    if (mapping == null) {
      mapping = new TypeMapping(name, queryName, queryListName, type, fetcher, internal);
      // insert type to avoid recursion
      mappings.put(type, mapping);
      // search relationship in filds
      mapFields(mapping);
    }
    return mapping;
  }

  private void mapFields(TypeMapping mapping) {

    Map<String, PojoProperty> fields = new HashMap<>();

    for (Field field : lookupFields(mapping.type).values()) {

      if (!schemaAdapter.validField(field)) {
        continue;
      }

      String name = schemaAdapter.fieldName(field);

      if (isPrimitiveValue(field.getType())) {
          fields.put(name, createSimpleField(field, name));
        } else if (List.class.isAssignableFrom(field.getType())) {
          fields.put(name, createListField(mapping.type, name, field));
        } else {
        	 fields.put(name, createObjectField(mapping.type, name, field));
        }
    }
    mapping.fields = fields;
  }

  private PojoProperty createSimpleField(Field field, String name) {
	  return new PojoProperty(name, field, null, false);
  }

  private PojoProperty createListField(Class<?> parentType, String name, Field field) {

    Type genericType = field.getGenericType();

    if (genericType instanceof ParameterizedType) {
      Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
      if (type instanceof Class) {
        Class<?> typeClass = (Class<?>) type;
        if (!isPrimitiveValue(typeClass)) {
          // internal objecting map
          mapClass(name, null, null, typeClass, null, true);
        }
        return new ListProperty(name, field, typeClass, null, typeClass == parentType);
      }
      throw new IllegalStateException("Generics is needed to be a raw type at " + field);
    }
    throw new IllegalStateException("Generics is mandatory for List type at " + field);
  }

  private PojoProperty createObjectField(Class<?> parentType, String name, Field field) {
    // internal object mapping
    mapClass(name, null, null, field.getType(), null, true);
    return new RelationshipProperty(name, field, (DataFetcher) null, parentType == field.getType());
  }

  private Map<String, Field> lookupFields(Class<?> type) {
    return ReflectionUtils.lookupFields(type);
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
    return new GraphPojoSchema(mappings, fetcherStrategy);
  }
}
