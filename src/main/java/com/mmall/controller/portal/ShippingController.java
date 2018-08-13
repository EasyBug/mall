package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;

import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @program: mmall
 * @description: 地址
 * @author: BoWei
 * @create: 2018-04-08 13:09
 **/

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerRespose add(HttpServletRequest httpServletRequest, Shipping shipping) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(), shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerRespose del(HttpServletRequest httpServletRequest, Integer shippingId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerRespose update(HttpServletRequest httpServletRequest, Shipping shipping) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(), shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerRespose<Shipping> select(HttpServletRequest httpServletRequest, Integer shippingId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerRespose<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") int pagNum,
                                        @RequestParam(value = "pagSize", defaultValue = "10") int pagSize,
                                        HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(), pagNum, pagSize);
    }
}
