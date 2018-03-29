package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IproductService {
    ServerRespose saveOrUpdateProduct(Product product);

    ServerRespose<String> setSaleStatus(Integer productId, Integer status);

    ServerRespose<ProductDetailVo> manageProductDetail(Integer productId);

    ServerRespose getProductList(int pageNum, int pageSize);

    ServerRespose searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerRespose<ProductDetailVo> getProductDetail(Integer productId);

    ServerRespose<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String oderBy);
}
