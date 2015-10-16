package org.cubekode.graphpojo;

import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class GraphMojoTest {

  private static GraphPojoMapper pojoMapper;

  private static List<Product> productList;

  private static Product singleProduct;

  @BeforeClass
  public static void mapClasses() {

    productList = new ArrayList<Product>();
    IntStream.rangeClosed(1, 10).forEach((i) -> {
      productList.add(new Product(i, "Product " + i, "Desc Product " + i, (float) i));
    });
    singleProduct = productList.get(0);

    pojoMapper = new GraphPojoMapper();
    pojoMapper.mapClass(Product.class, new GraphPojoFetcher<Product>() {

      @Override
      protected Product getObject(DataFetchingEnvironment environment) {
        return singleProduct;
      }

      @Override
      protected List<Product> getList(DataFetchingEnvironment environment) {
        return productList;
      }
    });
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSingleResultIdOnly() throws Exception {

    Map<String, Object> singleResult =
        pojoMapper.execute("query testSingleResult { Product { id } }");

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
        pojoMapper.execute("query testSingleResult { Product { id name desc } }");

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
        pojoMapper.execute("query testSingleResult { ProductList { id } }");

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
        pojoMapper.execute("query testSingleResult { ProductList { id name } }");

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
