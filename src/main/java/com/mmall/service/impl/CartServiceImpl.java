package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDevimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: mmall
 * @description: 购物车实现层
 * @author: BoWei
 * @create: 2018-04-02 09:56
 **/
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    /**
     * @Description: 在购物车中新增商品
     * @Param: [userId, productId, Count]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.CartVo>
     * @Author: BoWei
     * @Date: 2018/4/3
     */
    public ServerRespose<CartVo> add(Integer userId, Integer productId, Integer Count) {
        if (productId == null || Count == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
            /*这个产品不再购物车里，需新增一条购物记录*/
            Cart cartItem = new Cart();
            /*设置物品数量*/
            cartItem.setQuantity(Count);
            /*设置物品是否被选中*/
            cartItem.setChecked(Const.Cart.CHECKED);
            /*设置物品的购买者ID*/
            cartItem.setUserId(userId);
            /*设置购买商品ID*/
            cartItem.setProductId(productId);
            cartMapper.insert(cartItem);
        } else {
            /*说明已经在购物车里只需更新数量*/
            /*如果产品已存在就变成数量相加*/
            Count = cart.getQuantity() + Count;
            cart.setQuantity(Count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }


    /**
     * @Description: 更新商品数量
     * @Param: [userId, productId, Count]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.CartVo>
     * @Author: BoWei
     * @Date: 2018/4/3
     */
    public ServerRespose<CartVo> update(Integer userId, Integer productId, Integer Count) {
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(Count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    /**
     * @Description: 在购物车中删除指定商品
     * @Param: [userId, productId]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/3
     */
    public ServerRespose<CartVo> deleteProduct(Integer userId, String productIds) {
        /*自动将数组分割字符串靠“，”区分*/
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    /**
     * @Description: 查询购物车商品
     * @Param: [userId]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.CartVo>
     * @Author: BoWei
     * @Date: 2018/4/4
     */
    public ServerRespose<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerRespose.createBySuccess(cartVo);
    }

    /**
     * @Description: 选择或反选购物车里的商品
     * @Param: [userId, productId, checked]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.CartVo>
     * @Author: BoWei
     * @Date: 2018/4/4
     */
    public ServerRespose<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return this.list(userId);
    }

    /**
     * @Description: 购物车所有商品数量，用于显示到页面右上角
     * @Param: [userId]
     * @return: com.mmall.common.ServerRespose<java.lang.Integer>
     * @Author: BoWei
     * @Date: 2018/4/4
     */
    public ServerRespose<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerRespose.createBySuccess(0);
        }
        return ServerRespose.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    /**
     * @Description: 计算购物车里商品总价、单选、反选
     * @Param: [userId]
     * @return: com.mmall.vo.CartVo
     * @Author: BoWei
     * @Date: 2018/4/3
     */
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> carts = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (carts != null) {
            for (Cart cartItem : carts) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                /*根据购物车当前的货物查询货物的数量*/
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    /*判断库存*/
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        /*库存充足设置成要购买数量*/
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        /*库存不足设置成最大库存*/
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        /*购物车中更新有效库存*/
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    /*计算总价*/
                    cartProductVo.setProductTotalPrice(BigDevimalUtil.mul(cartProductVo.getQuantity(), product.getPrice().doubleValue()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    /*如果已经勾选增加到总购物车中*/
                    cartTotalPrice = BigDevimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);//总价
        cartVo.setCartProductVoList(cartProductVoList);//所有商品
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     * @Description: 判断用户的购物车是否全选
     * @Param: [userId]
     * @return: boolean
     * @Author: BoWei
     * @Date: 2018/4/3
     */
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }


}
