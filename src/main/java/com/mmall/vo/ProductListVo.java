package com.mmall.vo;

import java.math.BigDecimal;

/**
 * @program: mmall
 * @description: ProductVo
 * @author: BoWei
 * @create: 2018-03-23 13:54
 **/
public class ProductListVo {
    /**/
    private Integer id;
    /**/
    private Integer categoryId;
    /**/
    private String name;
    /*副标题*/
    private String subtitle;
    /*第一张图*/
    private String main_image;
    /*价格*/
    private BigDecimal price;
    /**/
    private Integer status;
    /*图片服务器的前缀*/
    private String iamgeHost;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMain_image() {
        return main_image;
    }

    public void setMain_image(String main_image) {
        this.main_image = main_image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIamgeHost() {
        return iamgeHost;
    }

    public void setIamgeHost(String iamgeHost) {
        this.iamgeHost = iamgeHost;
    }
}
