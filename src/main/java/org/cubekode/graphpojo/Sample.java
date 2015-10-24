package org.cubekode.graphpojo;

import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Sample {
  
  public static void main(String[] args) {
    
    GraphPojoMapper pojoMapper = new GraphPojoMapper();

    pojoMapper.mapClass(Category.class, new GraphPojoFetcher<Category>() {

      @Override
      protected Category getObject(DataFetchingEnvironment environment) {
        return new Category(1, "Category 1");
      }

      @Override
      protected List<Category> getList(DataFetchingEnvironment environment) {
        return Arrays.asList(
          new Category(1, "Category 1"),
          new Category(2, "Category 2")
        );
      }
    });
    
    pojoMapper.mapClass(SampleProduct.class, new GraphPojoFetcher<SampleProduct>() {

      @Override
      protected SampleProduct getObject(DataFetchingEnvironment environment) {
        return new SampleProduct(1, "Product 1", "Desc Product 1", 1.0f, Arrays.asList(new Category(1, "CAT 1")));
      }

      @Override
      protected List<SampleProduct> getList(DataFetchingEnvironment environment) {
        return Arrays.asList(
          new SampleProduct(1, "Product 1", "Desc Product 1", 1.0f, Arrays.asList(new Category(1, "CAT 1"))),
          new SampleProduct(2, "Product 2", "Desc Product 2", 2.0f, Arrays.asList(new Category(1, "CAT 1")))
        );
      }
    });

    Map<String, Object> queryResult = pojoMapper.execute("query GetProduct { SampleProduct { id name categories { name }} } ");

    System.out.println(queryResult);
  }
}
