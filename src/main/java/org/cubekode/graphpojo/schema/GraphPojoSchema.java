package org.cubekode.graphpojo.schema;

import static graphql.schema.GraphQLObjectType.newObject;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
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

import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.ListProperty;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.PojoProperty;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.RelationshipProperty;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.TypeMapping;

public class GraphPojoSchema {

  private Map<Class<?>, TypeMapping> mappings;

  private PropertyFetcherStrategy fetcherStrategy;

  private Map<String, GraphQLFieldDefinition> queries = new LinkedHashMap<>();

  private GraphQLSchema schema;

  GraphPojoSchema(Map<Class<?>, TypeMapping> mappings, PropertyFetcherStrategy fetcherStrategy) {

    this.fetcherStrategy = fetcherStrategy;
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
      GraphQLInputObjectType.Builder argumentBuild =
          GraphQLInputObjectType.newInputObject().name(mapping.name);

      for (PojoProperty property : mapping.fields.values()) {
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

  private GraphQLInputObjectField defineArgumentProperty(PojoProperty property) {
    GraphQLInputType inputType;
    if (property instanceof RelationshipProperty) {
      inputType = mappings.get(property.field.getType()).argumentType;
    } else {
      inputType = (GraphQLInputType) defineType(property);
    }
    return GraphQLInputObjectField.newInputObjectField().name(property.name).type(inputType)
        .build();
  }

  private GraphQLFieldDefinition defineProperty(PojoProperty property) {
    return GraphQLFieldDefinition.newFieldDefinition().name(property.name)
        .dataFetcher(defineFetcher(property)).type(defineType(property)).build();
  }

  private DataFetcher defineFetcher(PojoProperty property) {

    DataFetcher fetcher = property.fetcher;

    if (fetcher == null) {
      // lookup mapped fetchers
      if (property instanceof RelationshipProperty) {
        if (mappings.containsKey(property.field.getType())) {
          fetcher = mappings.get(property.field.getType()).fetcher;
        }
      } else if (property instanceof ListProperty) {
        ListProperty listProperty = (ListProperty) property;
        if (mappings.containsKey(listProperty.listType)) {
          fetcher = mappings.get(listProperty.listType).fetcher;
        }
      }
      // create default property fetcher
      if (fetcher == null) {
        fetcher = fetcherStrategy.createFetcher(property);
      }
      property.fetcher = fetcher;
    }
    return fetcher;
  }

  private GraphQLOutputType defineType(PojoProperty property) {
    if (property instanceof ListProperty) {
      return new GraphQLList(defineObjectType(((ListProperty) property).listType));
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
        .type(new GraphQLList(mapping.objectType)).argument(defineArguments(mapping))
        .dataFetcher(mapping.fetcher).build());
  }

  private void addQuery(GraphQLFieldDefinition listField) {
    queries.put(listField.getName(), listField);
  }

  private List<GraphQLArgument> defineArguments(TypeMapping mapping) {

    Map<String, PojoProperty> fields = mapping.fields;
    List<GraphQLArgument> arguments = new ArrayList<>(fields.size());

    for (PojoProperty field : fields.values()) {
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

  @SuppressWarnings("unchecked")
  public Map<String, Object> execute(String query) throws GraphExecutionException {
    ExecutionResult result = new GraphQL(schema).execute(query);
    if (!result.getErrors().isEmpty()) {
      throw new GraphExecutionException(result.getErrors());
    }
    return (Map<String, Object>) result.getData();
  }
}
