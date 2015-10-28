package org.cubekode.graphpojo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.cubekode.graphpojo.sample.Category;
import org.cubekode.graphpojo.sample.Product;
import org.cubekode.graphpojo.schema.GraphPojoSchema;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GraphMojoTest {

  private static GraphDataTest data;
  private static GraphPojoSchema schema;

  @Before
  public void mapClasses() {

    data = new GraphDataTest();

    GraphPojoSchemaBuilder builder = createSchemaBuilder();

    addClasses(builder);

    schema = builder.build();
  }

  protected GraphPojoSchemaBuilder createSchemaBuilder() {
    return new GraphPojoSchemaBuilder();
  }

  protected void addClasses(GraphPojoSchemaBuilder builder) {
    builder.add(Category.class, data.getCategoryFetcher());
    builder.add(Product.class, data.getProductFetcher());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSingleResultIdOnly() throws Exception {

    Map<String, Object> singleResult = schema.execute("query testSingleResult { Product { id } }");

    Assert.assertNotNull(singleResult);
    Assert.assertFalse(singleResult.isEmpty());
    Assert.assertTrue(singleResult.containsKey("Product"));

    Map<String, Object> product = (Map<String, Object>) singleResult.get("Product");

    Assert.assertEquals(new HashSet<>(Arrays.asList("id")), product.keySet());
    Assert.assertEquals(data.getSingleProduct().getId(), product.get("id"));
  }


  @Test
  @SuppressWarnings("unchecked")
  public void testSingleResultManyFields() throws Exception {

    Map<String, Object> singleResult =
        schema.execute("query testSingleResult { Product { id name desc } }");

    Assert.assertNotNull(singleResult);
    Assert.assertFalse(singleResult.isEmpty());
    Assert.assertTrue(singleResult.containsKey("Product"));

    Map<String, Object> product = (Map<String, Object>) singleResult.get("Product");

    Assert.assertEquals(new HashSet<>(Arrays.asList("id", "name", "desc")), product.keySet());

    Assert.assertEquals(data.getSingleProduct().getId(), product.get("id"));
    Assert.assertEquals(data.getSingleProduct().getName(), product.get("name"));
    Assert.assertEquals(data.getSingleProduct().getDesc(), product.get("desc"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testResultListIdOnly() throws Exception {

    Map<String, Object> singleResult =
        schema.execute("query testSingleResult { ProductList { id } }");

    Assert.assertNotNull(singleResult);
    Assert.assertFalse(singleResult.isEmpty());
    Assert.assertTrue(singleResult.containsKey("ProductList"));

    List<Map<String, Object>> list = (List<Map<String, Object>>) singleResult.get("ProductList");

    Assert.assertEquals(data.getProductList().size(), list.size());

    for (Map<String, Object> map : list) {
      Assert.assertEquals(new HashSet<>(Arrays.asList("id")), map.keySet());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testResultListManyFields() throws Exception {

    Map<String, Object> singleResult =
        schema.execute("query testSingleResult { ProductList { id name } }");

    Assert.assertNotNull(singleResult);
    Assert.assertFalse(singleResult.isEmpty());
    Assert.assertTrue(singleResult.containsKey("ProductList"));

    List<Map<String, Object>> list = (List<Map<String, Object>>) singleResult.get("ProductList");

    Assert.assertEquals(data.getProductList().size(), list.size());

    for (Map<String, Object> map : list) {
      Assert.assertEquals(new HashSet<>(Arrays.asList("id", "name")), map.keySet());
    }
  }
}
