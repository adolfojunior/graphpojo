package org.cubekode.graphpojo.sample;

import java.util.Map;

import org.cubekode.graphpojo.schema.GraphPojoSchema;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder;

/**
 * @author asantos
 *
 */
public class Sample {

  public static void main(String[] args) {

    GraphPojoSchemaBuilder builder = new GraphPojoSchemaBuilder();

    builder.add(Category.class, new CategoryFetcher());
    builder.add(Product.class, new ProductFetcher());
    
    GraphPojoSchema schema = builder.build();

    Map<String, Object> queryResult =
        schema.execute("query GetProduct { Product { id name categories { id name } } }");

    System.out.println(queryResult);
  }
}
