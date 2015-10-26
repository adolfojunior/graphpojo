package org.cubekode.graphpojo.sample;

import java.util.Map;

import org.cubekode.graphpojo.schema.GraphExecutionException;
import org.cubekode.graphpojo.schema.GraphPojoSchema;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder;
import org.cubekode.graphpojo.schema.PropertyFetcherStrategies;

/**
 * @author asantos
 */
public class Sample {

  public static void main(String[] args) throws GraphExecutionException {

    GraphPojoSchemaBuilder builder = new GraphPojoSchemaBuilder();

    builder.fetcherStrategy(PropertyFetcherStrategies.METHOD_REFLECTION);
    builder.add(Category.class, new CategoryFetcher());
    builder.add(Product.class, new ProductFetcher());

    GraphPojoSchema schema = builder.build();

    Map<String, Object> queryResult =
        schema.execute("query GetProduct { Product { id name categories {name} } }");

    System.out.println(queryResult);
  }
}
