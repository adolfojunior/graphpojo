package org.cubekode.graphpojo.schema;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;

import java.util.Collections;
import java.util.List;

public class GraphPojoFetcher<T> implements DataFetcher {

  @Override
  public Object get(DataFetchingEnvironment environment) {

    GraphQLType type = environment.getFieldType();

    if (type instanceof GraphQLList) {
      return getList(environment);
    }
    return getObject(environment);
  }

  protected T getObject(DataFetchingEnvironment environment) {
    return null;
  }

  protected List<T> getList(DataFetchingEnvironment environment) {
    return Collections.emptyList();
  }
}
