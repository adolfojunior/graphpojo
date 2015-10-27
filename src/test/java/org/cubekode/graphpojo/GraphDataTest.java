package org.cubekode.graphpojo;

import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cubekode.graphpojo.sample.Category;
import org.cubekode.graphpojo.sample.Product;
import org.cubekode.graphpojo.schema.GraphPojoFetcher;

public class GraphDataTest {

  private Product singleProduct;
  private Category singleCategory;
  private List<Product> productList;
  private List<Category> categoryList;

  public GraphDataTest() {

    categoryList = new ArrayList<>();
    productList = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      categoryList.add(new Category(i, "Category " + i));
      productList.add(new Product(i, "Product " + i, "Desc Product " + i, (float) i, Arrays
          .asList(new Category(1, "Local Category " + i))));
    }
    
    singleCategory = categoryList.get(0);
    singleProduct = productList.get(0);
  }

  public List<Category> getCategoryList() {
    return categoryList;
  }

  public List<Product> getProductList() {
    return productList;
  }

  public Category getSingleCategory() {
    return singleCategory;
  }

  public Product getSingleProduct() {
    return singleProduct;
  }

  public GraphPojoFetcher<Category> getCategoryFetcher() {
    return new GraphPojoFetcher<Category>() {
      @Override
      protected Category getObject(DataFetchingEnvironment environment) {
        return singleCategory;
      }

      @Override
      protected List<Category> getList(DataFetchingEnvironment environment) {
        return categoryList;
      }
    };
  }

  public GraphPojoFetcher<Product> getProductFetcher() {
    return new GraphPojoFetcher<Product>() {
      @Override
      protected Product getObject(DataFetchingEnvironment environment) {
        return singleProduct;
      }

      @Override
      protected List<Product> getList(DataFetchingEnvironment environment) {
        return productList;
      }
    };
  }
}
