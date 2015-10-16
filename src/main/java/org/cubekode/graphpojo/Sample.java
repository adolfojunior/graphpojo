package org.cubekode.graphpojo;

import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Sample {
  
  public static void main(String[] args) {
    
    GraphPojoMapper pojoMapper = new GraphPojoMapper();

    pojoMapper.mapClass(SampleProduct.class, new GraphPojoFetcher<SampleProduct>() {

      @Override
      protected SampleProduct getObject(DataFetchingEnvironment environment) {
        return new SampleProduct(1, "Product 1", "Desc Product 1", 1.0f);
      }

      @Override
      protected List<SampleProduct> getList(DataFetchingEnvironment environment) {
        return Arrays.asList(
          new SampleProduct(1, "Product 1", "Desc Product 1", 1.0f),
          new SampleProduct(2, "Product 2", "Desc Product 2", 2.0f)
        );
      }
    });

    Map<String, Object> queryResult = pojoMapper.execute("query GetProduct { SampleProduct { id name } } ");

    System.out.println(queryResult);
  }
}
