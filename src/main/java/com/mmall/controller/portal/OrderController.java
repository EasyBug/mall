package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import net.sf.jsqlparser.schema.Server;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;

/**
 * @program: mmall
 * @description: 支付
 * @author: BoWei
 * @create: 2018-04-11 13:45
 **/
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * @Description: 创建订单
     * @Param: [session, shippingId]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerRespose create(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.craeeteOrder(user.getId(), shippingId);
    }


    /**
     * @Description: 取消订单
     * @Param: [session, orderNo]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerRespose cancel(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerRespose getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }


    /**
     * @Description: 订单详情
     * @Param: [session, orderNo]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerRespose detail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    //订单列表
    @RequestMapping("list.do")
    @ResponseBody
    public ServerRespose list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }


    /**
     * @Description: 支付
     * @Param: [session, orderNo, request] [用户session;订单编号;]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/11
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerRespose pay(HttpSession session, Long orderNo, HttpServletRequest request) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(), path);
    }


    /**
     * @Description: 回调函数
     * @Param: [request]
     * @return: java.lang.Object
     * @Author: BoWei
     * @Date: 2018/4/13
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {

        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        /*迭代器看一下是否有下一个键值因为MAP不是按照顺序存储
        * 也是遍历map的方法*/
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调---sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

        //非常重要，验证回调的正确性，是不是支付宝发的。并且还有回避重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                return ServerRespose.createByErrorMessage("非法请求不通过！！！！小心我报警");
            }
        } catch (AlipayApiException e) {
            logger.info("支付宝验证回调异常", e);
        }

        //todo 验证各种数据正确性，查看老师源码


        //业务逻辑
        ServerRespose serverRespose = iOrderService.aliCallback(params);
        if (serverRespose.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerRespose<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerRespose serverRespose = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverRespose.isSuccess()) {
            return ServerRespose.createBySuccess(true);
        }
        return ServerRespose.createBySuccess(false);
    }
}
