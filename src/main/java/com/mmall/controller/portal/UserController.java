package com.mmall.controller.portal;

import com.github.pagehelper.StringUtil;
import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> Login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
        ServerRespose<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            //session.setAttribute(Const.CURRENT_USER, response.getData());
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisPoolUtil.del(loginToken);
        //session.removeAttribute(Const.CURRENT_USER);
        return ServerRespose.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> checkValid(String str, String type) {
        return iUserService.checkVild(str, type);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> getUserInfo(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user != null) {
            return ServerRespose.createBySuccess(user);
        }
        return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
    }

    @RequestMapping(value = "forget_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> forgetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkQusertionAnswer(username, question, answer);
    }

    @RequestMapping(value = "forget_rest_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> forgetRestPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetrestpassword(username, passwordNew, forgetToken);
    }

    @RequestMapping(value = "rest_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> resetPassword(HttpServletRequest httpServletRequest, String passwordOld, String passwordNew) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2obj(userJsonStr,User.class);
        if (user == null) {
            return ServerRespose.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user, passwordOld, passwordNew);
    }

    @RequestMapping(value = "updata_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> updataInformation(HttpServletRequest httpServletRequest, User user) {

        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User currenuser = JsonUtil.string2obj(userJsonStr,User.class);
        if (currenuser == null) {
            return ServerRespose.createByErrorMessage("用户未登录");
        }
        user.setId(currenuser.getId());
        user.setUsername(currenuser.getUsername());
        ServerRespose<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currenuser.getUsername());
            RedisPoolUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> get_information(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLonginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerRespose.createByErrorMessage("用户未登陆,获取信息失败");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User current = JsonUtil.string2obj(userJsonStr,User.class);
        if (current == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录需要强制登录Stsus= 10");
        }
        return iUserService.getInformation(current.getId());
    }
}

