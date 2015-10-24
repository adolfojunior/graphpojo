package org.cubekode.graphpojo;

public class Product {

  private Integer id;
  private String name;
  private String desc;
  private Float price;

  public Product(Integer id, String name, String desc, Float price) {
    this.id = id;
    this.name = name;
    this.desc = desc;
    this.price = price;
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
}
