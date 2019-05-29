/**
  * Copyright 2019 bejson.com 
  */
package com.xwf.common.http.product;
import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2019-05-23 23:56:18
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DYProduct {

    private BigDecimal price;
    private String product_id;
    private String card_url;
    private List<Elastic_images> elastic_images;
    private int promotion_source;
    private String promotion_id;
    private List<String> label;
    private String title;
    private Visitor visitor;
    private int sales;
    private String elastic_title;
    public void setPrice(BigDecimal price) {
         this.price = price;
     }
     public BigDecimal getPrice() {
         return price;
     }

    public void setProduct_id(String product_id) {
         this.product_id = product_id;
     }
     public String getProduct_id() {
         return product_id;
     }

    public void setCard_url(String card_url) {
         this.card_url = card_url;
     }
     public String getCard_url() {
         return card_url;
     }

    public void setElastic_images(List<Elastic_images> elastic_images) {
         this.elastic_images = elastic_images;
     }
     public List<Elastic_images> getElastic_images() {
         return elastic_images;
     }

    public void setPromotion_source(int promotion_source) {
         this.promotion_source = promotion_source;
     }
     public int getPromotion_source() {
         return promotion_source;
     }

    public void setPromotion_id(String promotion_id) {
         this.promotion_id = promotion_id;
     }
     public String getPromotion_id() {
         return promotion_id;
     }

    public void setLabel(List<String> label) {
         this.label = label;
     }
     public List<String> getLabel() {
         return label;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setVisitor(Visitor visitor) {
         this.visitor = visitor;
     }
     public Visitor getVisitor() {
         return visitor;
     }

    public void setSales(int sales) {
         this.sales = sales;
     }
     public int getSales() {
         return sales;
     }

    public void setElastic_title(String elastic_title) {
         this.elastic_title = elastic_title;
     }
     public String getElastic_title() {
         return elastic_title;
     }

}