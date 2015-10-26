package org.cubekode.graphpojo.sample;

import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.List;

import org.cubekode.graphpojo.schema.GraphPojoFetcher;

public class CategoryFetcher extends GraphPojoFetcher<Category> {
  @Override
  protected Category getObject(DataFetchingEnvironment environment) {
    return new Category(1, "Category 1");
  }

  @Override
  protected List<Category> getList(DataFetchingEnvironment environment) {
    return Arrays.asList(new Category(1, "Category 1"), new Category(2, "Category 2"));
  }
}
