package org.cubekode.graphpojo.sample;

import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.cubekode.graphpojo.GraphPojoFetcher;

public class ProductFetcher extends GraphPojoFetcher<Product> {

  @Override
  protected Product getObject(DataFetchingEnvironment environment) {
    return new Product(1, "Product 1", "Desc Product 1", 1.0f, Collections.emptyList());
  }

  @Override
  protected List<Product> getList(DataFetchingEnvironment environment) {
    List<Category> categories = Arrays.asList(new Category(1, "Category 1"), new Category(2, "Category 2"));
    return Arrays.asList(new Product(1, "Product 1", "Desc Product 1", 1.0f, categories), new Product(2,
        "Product 2", "Desc Product 2", 2.0f, categories));
  }
}
