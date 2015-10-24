package org.cubekode.graphpojo;

import static graphql.schema.GraphQLObjectType.newObject;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraphPojoMapper {

  static class PojoMapping {

    GraphQLObjectType type;
    DataFetcher fetcher;
    boolean mapped;

    public PojoMapping(GraphQLObjectType type, DataFetcher fetcher, boolean mapped) {
      this.type = type;
      this.fetcher = fetcher;
      this.mapped = mapped;
    }
  }

  private Map<Class<?>, PojoMapping> mappings = new LinkedHashMap<>();
  private Map<String, GraphQLFieldDefinition> queries = new LinkedHashMap<>();

  private GraphQLSchema schema;

  public <T> GraphQLObjectType mapClass(Class<T> type, GraphPojoFetcher<T> fetcher) {

    PojoMapping mapping = mappings.get(type);

    if (mapping == null) {

      // Insert type to avoid recursion
      mapping = new PojoMapping(null, fetcher, true);
      mappings.put(type, mapping);

      try {

        mapping.type = mapType(type).build();

        // create a default queries for this mapping
        mapQueries(mapping);

      } catch (IntrospectionException e) {
        throw new IllegalStateException("Object mapping error", e);
      }
    }
    return mapping.type;
  }

  private Builder mapType(Class<?> type) throws IntrospectionException {

    Builder builder = newObject().name(type.getSimpleName());

    PropertyDescriptor[] properties = Introspector.getBeanInfo(type).getPropertyDescriptors();

    for (PropertyDescriptor property : properties) {
      if (!"class".equals(property.getName())) {
        builder.field(mapField(property));
      }
    }
    return builder;
  }

  private GraphQLFieldDefinition mapField(PropertyDescriptor prop) {

    graphql.schema.GraphQLFieldDefinition.Builder type = GraphQLFieldDefinition
        .newFieldDefinition()
        .description(prop.getPropertyType().getName())
        .name(prop.getName())
        .type(mapFieldType(prop));
    
    //TODO figure out how to get the generic type from the list
    if (prop.getPropertyType().equals(List.class) 
        && mappings.containsKey(((java.lang.reflect.ParameterizedType)prop.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0])) {
      
      DataFetcher fetcher = mappings.get(((java.lang.reflect.ParameterizedType)prop.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0]).fetcher;
      
      if (fetcher != null) {
        type.dataFetcher(fetcher);
      }
    }
    
    return type.build();
  }

  private GraphQLOutputType mapFieldType(PropertyDescriptor prop) {

    Class<?> type = prop.getPropertyType();

    if (String.class.equals(type)) {
      return Scalars.GraphQLString;
    } else if (Float.class.equals(type)) {
      return Scalars.GraphQLFloat;
    } else if (Integer.class.equals(type)) {
      return Scalars.GraphQLInt;
    } else if (Boolean.class.equals(type)) {
      return Scalars.GraphQLBoolean;
    } else if (List.class.equals(type)) {
      return new GraphQLList(mappings.get(Category.class).type);
    }
    return mapClass(type, null);
  }

  private void mapQueries(PojoMapping mapping) {

    addQuery(GraphQLFieldDefinition
        .newFieldDefinition()
        .name(mapping.type.getName())
        .type(mapping.type)
        .argument(arguments(mapping.type))
        .dataFetcher(mapping.fetcher)
        .build());

    addQuery(GraphQLFieldDefinition
        .newFieldDefinition()
        .name(mapping.type.getName() + "List")
        .type(new GraphQLList(mapping.type))
        .argument(arguments(mapping.type))
        .dataFetcher(mapping.fetcher)
        .build());
  }

  private void addQuery(GraphQLFieldDefinition listField) {
    queries.put(listField.getName(), listField);
  }

  private List<GraphQLArgument> arguments(GraphQLObjectType type) {

    List<GraphQLFieldDefinition> fields = type.getFieldDefinitions();
    List<GraphQLArgument> arguments = new ArrayList<>(fields.size());

    for (GraphQLFieldDefinition field : fields) {
      arguments.add(GraphQLArgument
          .newArgument()
          .name(field.getName())
          .type((GraphQLInputType) field.getType())
          .build());
    }
    return arguments;
  }

  public Map<String, Object> execute(String query) {

    if (schema == null) {
      Builder builder = newObject().name("query");
      for (GraphQLFieldDefinition field : queries.values()) {
        builder.field(field);
      }
      schema = GraphQLSchema.newSchema().query(builder.build()).build();
    }

    return new GraphQL(schema).execute(query).getData();
  }
}
