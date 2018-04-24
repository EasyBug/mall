package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @program: mmall
 * @description: 管理员管理订单
 * @author: BoWei
 * @create: 2018-04-23 10:24
 **/
@Controller
@RequestMapping("/manage/order")
public class OrderManagerController {


    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;


    /**
     * @Description: 后台管理List接口
     * @Param: [session, pageNum, pageSize]
     * @return: com.mmall.common.ServerRespose<com.github.pagehelper.PageInfo>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerRespose<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //添加逻辑
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }
    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerRespose<OrderVo> orderDetail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //添加逻辑
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerRespose<PageInfo> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //添加逻辑
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }
    }


    /**
     * @Description: 后台管理员发货
     * @Param: [session, orderNo]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerRespose<String> orderSendGoods(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //添加逻辑
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }
    }
}
