package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IproductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IproductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * @Description: 新增或更新产品
     * @Param: [product]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose saveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArry = product.getSubImages().split(",");
                if (subImageArry.length > 0) {
                    product.setMainImage(subImageArry[0]);
                }
            }
            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0) {
                    return ServerRespose.createBySuccessMessage("更新产品成功");
                } else {
                    return ServerRespose.createByErrorMessage("更新产品失败");
                }
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerRespose.createBySuccessMessage("插入产品成功");
                } else {
                    return ServerRespose.createByErrorMessage("插入产品失败");
                }
            }
        }
        return ServerRespose.createByErrorMessage("产品参数有误");
    }

    /**
     * @Description: 更新商品售出情况
     * @Param: [productId, status]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerRespose.createBySuccessMessage("修改产品成功");
        }
        return ServerRespose.createBySuccessMessage("修改产品失败");
    }

    /**
     * @Description: 查询商品详情
     * @Param: [productId]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.ProductDetailVo>
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    public ServerRespose<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerRespose.createByErrorMessage("商品以下架");
        }
        //vo对象--value object
        //pojo->bo(business object)->vo(view object)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerRespose.createBySuccess(productDetailVo);
    }

    /**
     * @Description: 封装vo对象
     * @Param: [product]
     * @return: com.mmall.vo.ProductDetailVo
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMain_image(product.getMainImage());
        productDetailVo.setSub_images(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        /*读取FTP服务器图片*/
        productDetailVo.setIamgeHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            /*默认fu节点*/
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(product.getCategoryId());
        }
        productDetailVo.setCreatTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }


    /**
     * @Description: 分页查询
     * @Param: [pageNum, pageSize]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    public ServerRespose getProductList(int pageNum, int pageSize) {
        /**
         *使用分页查询三步走1.startPage--start;2.填充Sql逻辑;3.PageHelper收尾
         * */
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVos.add(productListVo);
        }
        PageInfo pageResut = new PageInfo(productList);
        //重置分页jieguo
        pageResut.setList(productListVos);
        return ServerRespose.createBySuccess(pageResut);
    }

    /**
     * @Description: 封装商品ListVo
     * @Param: [product]
     * @return: com.mmall.vo.ProductListVo
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setIamgeHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productListVo.setMain_image(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    /** 
    * @Description: 查询商品并分页
    * @Param: [productName, productId, pageNum, pageSize] 
    * @return: com.mmall.common.ServerRespose 
    * @Author: BoWei 
    * @Date: 2018/3/26 
    */
    public ServerRespose searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVos.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVos);
        return ServerRespose.createBySuccess(pageResult);
    }

}
