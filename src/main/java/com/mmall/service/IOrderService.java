package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerRespose;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {

    public ServerRespose pay(Long orderNo, Integer userId, String path);

    ServerRespose aliCallback(Map<String, String> params);

    ServerRespose queryOrderPayStatus(Integer userId, Long orgerNo);

    ServerRespose craeeteOrder(Integer userId, Integer shippingId);

    ServerRespose<String> cancel(Integer userId, Long orderNo);

    ServerRespose getOrderCartProduct(Integer userId);

    ServerRespose<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerRespose<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    //backend
    ServerRespose<PageInfo> manageList(int pageNum, int pageSize);

    ServerRespose<OrderVo> manageDetail(Long orderNo);

    ServerRespose<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerRespose<String> manageSendGoods(Long orderNo);
}
