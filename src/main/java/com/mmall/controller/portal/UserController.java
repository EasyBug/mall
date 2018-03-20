package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.SunHints;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> Login(String username, String password, HttpSession session) {
        ServerRespose<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
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
    public ServerRespose<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerRespose<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user, passwordOld, passwordNew);
    }

    @RequestMapping(value = "updata_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> updataInformation(HttpSession session, User user) {

        User currenuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currenuser == null) {
            return ServerRespose.createByErrorMessage("用户未登录");
        }
        user.setId(currenuser.getId());
        user.setUsername(currenuser.getUsername());
        ServerRespose<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currenuser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> get_information(HttpSession seesion) {
        User current = (User) seesion.getAttribute(Const.CURRENT_USER);
        if (current == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录需要强制登录Stsus= 10");
        }
        return iUserService.getInformation(current.getId());
    }
}

