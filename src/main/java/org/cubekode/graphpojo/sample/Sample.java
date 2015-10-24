package org.cubekode.graphpojo.sample;

import java.util.Map;

import org.cubekode.graphpojo.GraphPojoMapper;

public class Sample {

  public static void main(String[] args) {

    GraphPojoMapper pojoMapper = new GraphPojoMapper();

    pojoMapper.mapClass(Category.class, new CategoryFetcher());
    pojoMapper.mapClass(Product.class, new ProductFetcher());

    Map<String, Object> queryResult =
        pojoMapper.execute("query GetProduct { Product { id name categories { name } } } ");

    System.out.println(queryResult);
  }
}
