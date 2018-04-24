package com.mmall.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: mmall
 * @description: 订单详情
 * @author: BoWei
 * @create: 2018-04-19 10:19
 **/
public class OrderItemVo {

    private Long orderNo;
    /*对应产品Id*/
    private Integer productId;
    /*对应产品名字*/
    private String productName;
    /*产品主图*/
    private String productImage;
    /*单价*/
    private BigDecimal currentUnitPrice;
    /*数量*/
    private Integer quantity;
    /*总价*/
    private BigDecimal totalPrice;

    private String createTime;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
