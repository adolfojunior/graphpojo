package org.cubekode.graphpojo;

import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.cubekode.graphpojo.sample.Category;
import org.cubekode.graphpojo.sample.Product;
import org.cubekode.graphpojo.schema.GraphPojoFetcher;
import org.cubekode.graphpojo.schema.GraphPojoSchema;
import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;



public class GraphMojoTest {

  private static GraphPojoSchema schema;

  private static Product singleProduct;
  private static Category singleCategory;

  private static List<Product> productList;
  private static List<Category> categoryList;

  @BeforeClass
  public static void mapClasses() {

    singleCategory = new Category(1, "Category 1");
    categoryList = Collections.singletonList(singleCategory);

    productList = new ArrayList<Product>();
    IntStream.rangeClosed(1, 10).forEach(
        (i) -> {
          productList.add(new Product(i, "Product " + i, "Desc Product " + i, (float) i, Arrays
              .asList(new Category(1, "Local Category 1"))));
        });
    singleProduct = productList.get(0);

    GraphPojoSchemaBuilder builder = new GraphPojoSchemaBuilder();

    builder.add(Category.class, new GraphPojoFetcher<Category>() {
      @Override
      protected Category getObject(DataFetchingEnvironment environment) {
        return singleCategory;
      }

      @Override
      protected List<Category> getList(DataFetchingEnvironment environment) {
        return categoryList;
      }
    });

    builder.add(Product.class, new GraphPojoFetcher<Product>() {
      @Override
      protected Product getObject(DataFetchingEnvironment environment) {
        return singleProduct;
      }

      @Override
      protected List<Product> getList(DataFetchingEnvironment environment) {
        return productList;
      }
    });

    schema = builder.build();
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
    Assert.assertEquals(singleProduct.getId(), product.get("id"));
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

    Assert.assertEquals(singleProduct.getId(), product.get("id"));
    Assert.assertEquals(singleProduct.getName(), product.get("name"));
    Assert.assertEquals(singleProduct.getDesc(), product.get("desc"));
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

    Assert.assertEquals(productList.size(), list.size());

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

    Assert.assertEquals(productList.size(), list.size());

    for (Map<String, Object> map : list) {
      Assert.assertEquals(new HashSet<>(Arrays.asList("id", "name")), map.keySet());
    }
  }
}
