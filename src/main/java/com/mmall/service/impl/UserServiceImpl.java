package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerRespose;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.server.UnicastServerRef;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper UserMapper;

    /**
     * @Description:登陆实现方法
     * @Param: [username, password]
     * @return: com.mmall.common.ServerRespose<com.mmall.pojo.User>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    @Override
    public ServerRespose<User> login(String username, String password) {
        int resultCount = UserMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerRespose.createByErrorMessage("用户名不存在");
        }
        //MD5加密密码
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = UserMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerRespose.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerRespose.createBySuccess("登陆成功", user);

    }

    /**
     * @Description:
     * @Param: [user]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<String> register(User user) {
        ServerRespose checkserverRespose = this.checkVild(user.getUsername(), Const.USERNAME);
        if (!checkserverRespose.isSuccess()) {
            return checkserverRespose;
        }
        checkserverRespose = this.checkVild(user.getEmail(), Const.EMAIL);
        if (!checkserverRespose.isSuccess()) {
            return checkserverRespose;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = UserMapper.insert(user);
        if (resultCount == 0) {
            return ServerRespose.createByErrorMessage("注册失败");
        }
        return ServerRespose.createBySuccessMessage("注册成功");
    }

    /**
     * @Description:
     * @Param: [str, type]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<String> checkVild(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                int resultCount = UserMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerRespose.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = UserMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerRespose.createByErrorMessage("Email已存在");
                }
            }
        } else {
            return ServerRespose.createByErrorMessage("信息有误");
        }
        return ServerRespose.createBySuccessMessage("校验成功");
    }

    /**
     * @Description:
     * @Param: [username]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<String> selectQuestion(String username) {
        ServerRespose serverrespose = this.checkVild(username, Const.USERNAME);
        if (serverrespose.isSuccess()) {
            return ServerRespose.createByErrorMessage("该用户不存在");
        }
        String question = UserMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerRespose.createBySuccess(question);
        }
        return ServerRespose.createByErrorMessage("找回密码的问题是空的");
    }

    /**
     * @Description:
     * @Param: [username, question, answer]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<String> checkQusertionAnswer(String username, String question, String answer) {
        int resultCount = UserMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerRespose.createBySuccess(forgetToken);
        }
        return ServerRespose.createByErrorMessage("答案错误");
    }

    /**
     * @Description:
     * @Param: [username, passwordNew, forgetToken]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<String> forgetrestpassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerRespose.createByErrorMessage("token需要传递");
        }
        ServerRespose validResponse = this.checkVild(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerRespose.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerRespose.createByErrorMessage("Token过期或无效");
        }
        if (StringUtils.equals(forgetToken, token)) {
            String md5password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = UserMapper.updatePasswordByUsername(username, md5password);
            if (rowCount > 0) {
                return ServerRespose.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerRespose.createByErrorMessage("Token错误,请重新获取重置密码的Token");
        }
        return ServerRespose.createByErrorMessage("密码修改失败");
    }

    /**
     * @Description:
     * @Param: [user, passwordOld, passwordNew]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<String> resetPassword(User user, String passwordOld, String passwordNew) {
        //防止横向越权再次校验用户名和该用户名的密码
        int rowCount = UserMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (rowCount == 0) {
            return ServerRespose.createByErrorMessage("旧密码输入错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = UserMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerRespose.createBySuccessMessage("密码更新成功");
        }
        return ServerRespose.createByErrorMessage("密码更新失败");

    }

    /**
     * @Description:
     * @Param: [user]
     * @return: com.mmall.common.ServerRespose<com.mmall.pojo.User>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<User> updateInformation(User user) {
        int emailCount = UserMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (emailCount > 0) {
            return ServerRespose.createByErrorMessage("Email 已经被占用");
        }

        User updateuser = new User();
        updateuser.setId(user.getId());
        updateuser.setEmail(user.getEmail());
        updateuser.setPhone(user.getPhone());
        updateuser.setQuestion(user.getQuestion());
        updateuser.setAnswer(user.getAnswer());
        int updateUser = UserMapper.updateByPrimaryKeySelective(updateuser);
        if (updateUser > 0) {
            return ServerRespose.createBySuccess("更新个人信息成功", updateuser);
        }
        return ServerRespose.createByErrorMessage("更新个人信息失败");
    }

    /**
     * @Description:
     * @Param: [userId]
     * @return: com.mmall.common.ServerRespose<com.mmall.pojo.User>
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose<User> getInformation(Integer userId) {
        User user = UserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerRespose.createByErrorMessage("找不到该用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerRespose.createBySuccess(user);
    }

    /**
     * @Description:
     * @Param: [user]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/3/22
     */
    public ServerRespose checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerRespose.createBySuccess();
        }
        return ServerRespose.createByError();
    }
}
