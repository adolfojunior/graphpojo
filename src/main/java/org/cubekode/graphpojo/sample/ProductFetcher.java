package org.cubekode.graphpojo.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cubekode.graphpojo.schema.GraphPojoFetcher;

import graphql.schema.DataFetchingEnvironment;

public class ProductFetcher extends GraphPojoFetcher<Product> {
	
	static List<Category>  categories;
	static List<Product> related;
	static Product bundled;
	
	static{
		  categories = Arrays.asList(new Category(1, "Local Category 1"), new Category(2, "Local Category 2"));

		 Product bundled = new Product(2, "Product 2", "Desc Product 2", 1.0f, categories, null, null);
	    Product product3 = new Product(3, "Product 3", "Desc Product 3", 1.0f, categories, null, null);
	    related = new ArrayList<>();
	    related.add(bundled); related.add(product3);
	}

  @Override
  protected Product getObject(DataFetchingEnvironment environment) {
 
    return new Product(1, "Product 1", "Desc Product 1", 1.0f, categories, related, bundled);
  }

  @Override
  protected List<Product> getList(DataFetchingEnvironment environment) {
    List<Category> categories = Arrays.asList(new Category(1, "Local Category 1"), new Category(2, "Local Category 2"));
    return Arrays.asList(new Product(1, "Product 1", "Desc Product 1", 1.0f, categories, related, null), new Product(2,
        "Product 2", "Desc Product 2", 2.0f, categories, null, null));
  }
}
