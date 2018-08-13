package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;

import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.vo.CartVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @program: mmall
 * @description: 购物车控制层
 * @author: BoWei
 * @create: 2018-04-02 09:35
 **/
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerRespose<CartVo> add(HttpServletRequest httpServletRequest, Integer count, Integer productId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(), productId, count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerRespose<CartVo> update(HttpServletRequest httpServletRequest, Integer count, Integer productId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(), productId, count);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerRespose<CartVo> delete(HttpServletRequest httpServletRequest, String productIds) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerRespose<CartVo> list(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerRespose<CartVo> selectAll(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    //全反选
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerRespose<CartVo> unSelectAll(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    //单独选
    @RequestMapping("select.do")
    @ResponseBody
    public ServerRespose<CartVo> select(HttpServletRequest httpServletRequest, Integer productId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    //单独反选
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerRespose<CartVo> unSelect(HttpServletRequest httpServletRequest, Integer productId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }


    //查询当前用户购物车里面的产品数量，如果一个产品有10个，那么数量就是10.
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerRespose<Integer> getCartProductCount(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }


}
