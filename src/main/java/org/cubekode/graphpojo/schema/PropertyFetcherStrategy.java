package org.cubekode.graphpojo.schema;

import graphql.schema.DataFetcher;

import org.cubekode.graphpojo.schema.GraphPojoBuilder.PojoProperty;


public interface PropertyFetcherStrategy {

  DataFetcher createFetcher(PojoProperty property);
}
