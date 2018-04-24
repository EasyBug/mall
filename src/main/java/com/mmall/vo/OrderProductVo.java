package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: mmall
 * @description: 用户已经勾选的商品信息
 * @author: BoWei
 * @create: 2018-04-23 09:15
 **/
public class OrderProductVo {

    //每个物品信息包括数量等
    private List<OrderItemVo> orderItemVoList;
    //总价
    private BigDecimal productTotalPrice;
    //图片服务器前缀
    private String imageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
