package com.mmall.service;

import com.mmall.common.ServerRespose;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IproductService {
    ServerRespose saveOrUpdateProduct(Product product);

    ServerRespose<String> setSaleStatus(Integer productId, Integer status);

    ServerRespose<ProductDetailVo> manageProductDetail(Integer productId);

    ServerRespose getProductList(int pageNum, int pageSize);

    ServerRespose searchProduct(String productName, Integer productId, int pageNum, int pageSize);

}
