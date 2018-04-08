package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: mmall
 * @description: 包含多个CartProductVo
 * @author: BoWei
 * @create: 2018-04-02 13:25
 **/
public class CartVo {

    /*商品结合*/
    private List<CartProductVo> cartProductVoList;
    /*所有商品总价*/
    private BigDecimal cartTotalPrice;
    /*是否被勾选*/
    private Boolean allChecked;
    /*图片*/
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
