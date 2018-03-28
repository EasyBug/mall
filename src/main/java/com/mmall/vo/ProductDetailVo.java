package com.mmall.vo;

import java.math.BigDecimal;

/**
 * @program: mmall
 * @description: 商品详细信息
 * @author: BoWei
 * @create: 2018-03-22 14:12
 **/
public class ProductDetailVo {
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
    /*子图*/
    private String sub_images;
    /*详情描述*/
    private String detail;
    /*价格*/
    private BigDecimal price;
    /*库存数量*/
    private Integer stock;
    /**/
    private Integer status;
    /*插入时间*/
    private String creatTime;
    /*更新时间*/
    private String updateTime;

    /*图片服务器的前缀*/
    private String iamgeHost;

    /*父母节点*/
    private Integer parentCategoryId;

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

    public String getSub_images() {
        return sub_images;
    }

    public void setSub_images(String sub_images) {
        this.sub_images = sub_images;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIamgeHost() {
        return iamgeHost;
    }

    public void setIamgeHost(String iamgeHost) {
        this.iamgeHost = iamgeHost;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
