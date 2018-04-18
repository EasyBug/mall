package com.mmall.service;

import com.mmall.common.ServerRespose;

import java.util.Map;

public interface IOrderService {

    public ServerRespose pay(Long orderNo, Integer userId, String path);

    ServerRespose aliCallback(Map<String, String> params);

    ServerRespose queryOrderPayStatus(Integer userId, Long orgerNo);
}
