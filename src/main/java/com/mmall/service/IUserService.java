package com.mmall.service;

import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;

public interface IUserService {
    ServerRespose<User> login(String username, String password);

    ServerRespose<String> register(User user);

    ServerRespose<String> checkVild(String str, String type);

    ServerRespose<String> selectQuestion(String username);

    ServerRespose checkQusertionAnswer(String username, String question, String answer);

    ServerRespose<String>forgetrestpassword(String username,String passwordNew,String forgetToken);

    ServerRespose<String> resetPassword(User user, String passwordOld, String passwordNew);

    ServerRespose<User> updateInformation(User user);

    ServerRespose<User> getInformation(Integer userId);

    ServerRespose checkAdminRole(User user);
}
