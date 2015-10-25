package org.cubekode.graphpojo.schema;

import static graphql.schema.GraphQLObjectType.newObject;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.ListTypeField;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.ObjectTypeField;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.TypeField;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.TypeMapping;

public class GraphPojoSchema {

  private GraphQLSchema schema;

  private Map<Class<?>, TypeMapping> mappings;

  private Map<String, GraphQLFieldDefinition> queries = new LinkedHashMap<>();

  public GraphPojoSchema(Map<Class<?>, TypeMapping> mappings) {

    this.mappings = mappings;

    for (TypeMapping mapping : mappings.values()) {
      defineClass(mapping);
      defineQueries(mapping);
    }
    defineSchema();
  }

  protected GraphQLObjectType defineClass(TypeMapping mapping) {
    if (mapping.objectType == null) {
      
      GraphQLObjectType.Builder builder = GraphQLObjectType.newObject().name(mapping.name);
      GraphQLInputObjectType.Builder argumentBuild = GraphQLInputObjectType.newInputObject().name(mapping.name);
      
      for (TypeField property : mapping.fields.values()) {
        if (mapping.type == property.field.getType()) {
          throw new IllegalStateException("Recursive type is not supported yet - " + property.field);
        }
        builder.field(defineProperty(property));
        argumentBuild.field(defineArgumentProperty(property));
      }
      
      mapping.objectType = builder.build();
      mapping.argumentType = argumentBuild.build();
    }
    return mapping.objectType;
  }

  private GraphQLInputObjectField defineArgumentProperty(TypeField property) {
    GraphQLInputType inputType;
    if (property instanceof ObjectTypeField) {
      inputType = mappings.get(property.field.getType()).argumentType;
    } else {
      inputType = (GraphQLInputType) defineType(property);
    }
    return GraphQLInputObjectField.newInputObjectField().name(property.name).type(inputType).build();
  }

  private GraphQLFieldDefinition defineProperty(TypeField property) {
    return GraphQLFieldDefinition.newFieldDefinition().name(property.name)
        .dataFetcher(defineFetcher(property)).type(defineType(property)).build();
  }

  private DataFetcher defineFetcher(TypeField property) {

    DataFetcher fetcher = property.fetcher;

    if (fetcher == null) {
      // lookup mapped fetchers
      if (property instanceof ObjectTypeField) {
        if (mappings.containsKey(property.field.getType())) {
          fetcher = mappings.get(property.field.getType()).fetcher;
        }
      } else if (property instanceof ListTypeField) {
        ListTypeField listProperty = (ListTypeField) property;
        if (mappings.containsKey(listProperty.listType)) {
          fetcher = mappings.get(listProperty.listType).fetcher;
        }
      }
      // create default property fetcher
      if (fetcher == null) {
        fetcher = createPropertyFetcher(property);
      }
      property.fetcher = fetcher;
    }
    return fetcher;
  }

  private DataFetcher createPropertyFetcher(final TypeField property) {

    property.field.setAccessible(true);

    return new DataFetcher() {
      @Override
      public Object get(DataFetchingEnvironment environment) {
        Object source = environment.getSource();
        if (source == null) {
          return null;
        }
        if (source instanceof Map) {
          return ((Map<?, ?>) source).get(property.name);
        }
        try {
          return property.field.get(source);
        } catch (Exception e) {
          throw new IllegalStateException("Cant retrieve property " + property.field, e);
        }
      }
    };
  }

  private GraphQLOutputType defineType(TypeField property) {
    if (property instanceof ListTypeField) {
      return new GraphQLList(defineObjectType(((ListTypeField) property).listType));
    }
    return defineObjectType(property.field.getType());
  }

  private GraphQLOutputType defineObjectType(Class<?> type) {
    if (ScalarsExtension.isScalarType(type)) {
      return ScalarsExtension.getScalarType(type);
    } else {
      return defineClass(this.mappings.get(type));
    }
  }

  private void defineQueries(TypeMapping mapping) {

    addQuery(GraphQLFieldDefinition.newFieldDefinition().name(mapping.name)
        .type(mapping.objectType).argument(defineArguments(mapping)).dataFetcher(mapping.fetcher)
        .build());

    addQuery(GraphQLFieldDefinition.newFieldDefinition().name(mapping.name + "List")
        .type(new GraphQLList(mapping.objectType)).argument(defineArguments(mapping)).dataFetcher(mapping.fetcher)
        .build());
  }

  private void addQuery(GraphQLFieldDefinition listField) {
    queries.put(listField.getName(), listField);
  }

  private List<GraphQLArgument> defineArguments(TypeMapping mapping) {

    Map<String, TypeField> fields = mapping.fields;
    List<GraphQLArgument> arguments = new ArrayList<>(fields.size());

    for (TypeField field : fields.values()) {
      if (mappings.containsKey(mapping.type)) {
        arguments.add(GraphQLArgument.newArgument().name(field.name)
            .type(mappings.get(mapping.type).argumentType).build());
      } else {
        arguments.add(GraphQLArgument.newArgument().name(field.name)
            .type((GraphQLInputType) mapping.objectType.getFieldDefinition(field.name)).build());
      }
    }
    return arguments;
  }

  private void defineSchema() {
    GraphQLObjectType.Builder builder = newObject().name("query");
    for (GraphQLFieldDefinition field : queries.values()) {
      builder.field(field);
    }
    schema = GraphQLSchema.newSchema().query(builder.build()).build();
  }

  public Map<String, Object> execute(String query) {
    return new GraphQL(schema).execute(query).getData();
  }
}
