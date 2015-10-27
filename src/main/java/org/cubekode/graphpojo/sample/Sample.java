package org.cubekode.graphpojo.sample;

import org.cubekode.graphpojo.schema.GraphExecutionException;
import org.cubekode.graphpojo.schema.GraphPojoSchema;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder;
import org.cubekode.graphpojo.schema.PropertyFetcherStrategies;

/**
 * @author asantos
 */
public class Sample {

  private GraphPojoSchema schema;

  public Sample(GraphPojoSchema schema) {
    this.schema = schema;
  }

  private void query(String q) {
    System.out.println("# query: " + q);
    try {
      System.out.println(" retult:" + schema.execute(q));
    } catch (GraphExecutionException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws GraphExecutionException {

    GraphPojoSchemaBuilder builder = new GraphPojoSchemaBuilder();

    builder.fetcherStrategy(PropertyFetcherStrategies.METHOD_REFLECTION);
    builder.add(Category.class, new CategoryFetcher());
    builder.add(Product.class, new ProductFetcher());

    Sample sample = new Sample(builder.build());

    sample.query("query Sample { Product { id name categories {name} } }");

    sample.query("query Sample { Product { ..._fragProd } } fragment _fragProd on Product { id name }");
  }
}
