package org.cubekode.graphpojo.sample;

import java.util.List;

import org.cubekode.graphpojo.annotation.Relationship;

public class Product {

  private Integer id;
  private String name;
  private String desc;
  private Float price;

  @Relationship(name = "Category", fetcher = CategoryFetcher.class)
  private List<Category> categories;

  public Product(Integer id, String name, String desc, Float price, List<Category> categories) {
    this.id = id;
    this.name = name;
    this.desc = desc;
    this.price = price;
    this.categories = categories;
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

}
