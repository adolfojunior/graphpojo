package org.cubekode.graphpojo.sample;

import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.List;

import org.cubekode.graphpojo.schema.GraphPojoFetcher;

public class ProductFetcher extends GraphPojoFetcher<Product> {

  @Override
  protected Product getObject(DataFetchingEnvironment environment) {
    List<Category> categories = Arrays.asList(new Category(1, "Local Category 1"), new Category(2, "Local Category 2"));
    return new Product(1, "Product 1", "Desc Product 1", 1.0f, categories);
  }

  @Override
  protected List<Product> getList(DataFetchingEnvironment environment) {
    List<Category> categories = Arrays.asList(new Category(1, "Local Category 1"), new Category(2, "Local Category 2"));
    return Arrays.asList(new Product(1, "Product 1", "Desc Product 1", 1.0f, categories), new Product(2,
        "Product 2", "Desc Product 2", 2.0f, categories));
  }
}
