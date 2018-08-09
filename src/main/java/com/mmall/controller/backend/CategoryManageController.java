package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerRespose addCategory(HttpServletRequest httpServletRequest, String cateGoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(org.apache.commons.lang.StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //添加商品逻辑
            return iCategoryService.addCategory(cateGoryName, parentId);
        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerRespose setCategoryName(HttpServletRequest httpServletRequest, Integer categoryId, String categoryName) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(org.apache.commons.lang.StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //更新CategoryName
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }
    }


    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerRespose getChildrenParallelCategory(HttpServletRequest httpServletRequest, String cateGoryName, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(org.apache.commons.lang.StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //查询字节点category信息并且不递归,保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);

        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerRespose getCatagoryAndDeepChildrenCatagory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(org.apache.commons.lang.StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //查询当前节点的ID和递归字节点ID
            return iCategoryService.selectCategoryAndChildrenById(categoryId);

        } else {
            return ServerRespose.createByErrorMessage("该用户不是管理员");
        }

    }
}
