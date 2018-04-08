package com.mmall.service;

import com.mmall.common.ServerRespose;
import com.mmall.vo.CartVo;

/**
 * @program: mmall
 * @description: 购物车Service层
 * @author: BoWei
 * @create: 2018-04-02 09:56
 **/
public interface ICartService {

    ServerRespose<CartVo> add(Integer userId, Integer productId, Integer Count);

    ServerRespose<CartVo> update(Integer userId, Integer productId, Integer Count);

    ServerRespose<CartVo> deleteProduct(Integer userId, String productIds);

    ServerRespose<CartVo> list(Integer userId);

    ServerRespose<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerRespose<Integer> getCartProductCount(Integer userId);
}
