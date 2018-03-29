package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "Login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerRespose<User> Login(String username, String password, HttpSession session) {
        ServerRespose<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            } else {
                return ServerRespose.createByErrorMessage("不是管理员");
            }
        }
        return response;
    }
}
