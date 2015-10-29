package org.cubekode.graphpojo.sample;

import graphql.schema.DataFetchingEnvironment;

import org.cubekode.graphpojo.annotation.GraphQuery;
import org.cubekode.graphpojo.annotation.GraphRelationship;
import org.cubekode.graphpojo.annotation.GraphRepository;

@GraphRepository
public class SampleRepository {

  @GraphQuery
  public Category category(DataFetchingEnvironment environment) {
    return null;
  }

  /**
   * Any Category will use this fetcher
   */
  @GraphRelationship
  public Category defaultCategory(DataFetchingEnvironment environment) {
    return null;
  }

  /**
   * The specific Product->Category will use this fetcher
   */
  @GraphRelationship(type = Product.class, property = "related")
  public Category relatedCategory(DataFetchingEnvironment environment) {
    return null;
  }
  
  /**
   * The specific fetcher for Product->name field
   */
  @GraphRelationship(type = Product.class, property = "name")
  public String productName(DataFetchingEnvironment environment) {
    return null;
  }
}
