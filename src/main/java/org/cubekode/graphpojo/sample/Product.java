package org.cubekode.graphpojo.sample;

import java.util.List;

import org.cubekode.graphpojo.annotation.Relationship;

public class Product {

  private Integer id;
  private String name;
  private String desc;
  private Float price;
  private List<Product> relatedProducts;
  private Product bundledProduct;

  @Relationship(name = "Category", fetcher = CategoryFetcher.class)
  private List<Category> categories;

  public Product(Integer id, String name, String desc, Float price, List<Category> categories, List<Product> products, Product bundled) {
    this.id = id;
    this.name = name;
    this.desc = desc;
    this.price = price;
    this.categories = categories;
    this.relatedProducts = products;
    this.bundledProduct = bundled;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public Float getPrice() {
    return price;
  }

  public void setPrice(Float price) {
    this.price = price;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }
  
  public List<Product> getRelatedProducts(){
	  return relatedProducts;
  }
  
  public void setRelatedProducts(List<Product> products){
	  this.relatedProducts = products;
  }
  
  public Product getBundledProduct(){
	  return this.bundledProduct;
  }
  
  public void setRelatedProducts(Product product){
	  this.bundledProduct = product;
  }

}
