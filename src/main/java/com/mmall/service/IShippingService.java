package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.Shipping;

public interface IShippingService {
    ServerRespose add(Integer userId, Shipping shipping);

    ServerRespose<String> del(Integer userId, Integer shippingId);

    ServerRespose update(Integer userId, Shipping shippingId);

    ServerRespose<Shipping> select(Integer userId, Integer shippingId);

    ServerRespose<PageInfo> list(Integer userId, int pageNum, int pageSize);

}
