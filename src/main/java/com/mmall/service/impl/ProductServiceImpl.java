package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
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

    @Autowired
    private ICategoryService iCategoryService;

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
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
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


    /**
     * @Description: 查询商品详情及商品状态
     * @Param: [productId]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.ProductDetailVo>
     * @Author: BoWei
     * @Date: 2018/3/29
     */
    public ServerRespose<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerRespose.createByErrorMessage("商品已下架或删除");
        }
        if (product.getStatus() != Const.ProductEnum.ON_SALE.getCode()) {
            return ServerRespose.createByErrorMessage("商品已下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerRespose.createBySuccess(productDetailVo);
    }

    /**
     * @Description: 获取列表
     * @Param: [keyword, categoryId, pageNum, pageSize]
     * @return: com.mmall.common.ServerRespose<com.github.pagehelper.PageInfo>
     * @Author: BoWei
     * @Date: 2018/3/29
     */
    public ServerRespose<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String oderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> cateGoryList = new ArrayList<Integer>();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (StringUtils.isBlank(keyword) && category == null) {
                /*没有分类，并且还没有关键字这时候返回一个空集合不报错*/
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerRespose.createBySuccess(pageInfo);
            }
            cateGoryList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        /**准备分页
         * 分页两个步骤，首先将分页参数传给startPage
         *然后开始分页
         * **/
        PageHelper.startPage(pageNum, pageSize);
        /*开始排序*/
        if (StringUtils.isNotBlank(oderBy)) {
            if (Const.ProductListOdorby.PRICE_ASC_DESC.contains(oderBy)) {
                String[] oderByArray = oderBy.split("_");
                PageHelper.orderBy(oderByArray[0] + " " + oderByArray[1]);
            }
        }
        /*搜索Product*/
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, cateGoryList.size() == 0 ? null : cateGoryList);

        List<ProductListVo> productListVos = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVos.add(productListVo);
        }
        /**
         * 开始分页
         * **/
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVos);
        return ServerRespose.createBySuccess(pageInfo);
    }
}
