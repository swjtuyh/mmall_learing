package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Created by yinhuan on 2018/4/12.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessgae("用户名不存在");
        }
        // TODO: 2018/4/12  MD5加密
        String MD5Passwodr = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,MD5Passwodr);
        if(user == null){
            return ServerResponse.createByErrorMessgae("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);

    }
    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
           return ServerResponse.createByErrorMessgae("注册失败");
        }
        return ServerResponse.createBySuccessMessgae("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if (StringUtils.isNotBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessgae("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessgae("邮箱已存在");
                }
            }
        }else{
           return ServerResponse.createByErrorMessgae("参数错误");
        }
        return ServerResponse.createBySuccessMessgae("校验成功");
    }

    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessgae("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessgae("找回密码提示问题失败");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount > 0){
            //问题校验成功，
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessgae("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String fotgettoken){
        if (StringUtils.isBlank(fotgettoken)){
            return ServerResponse.createByErrorMessgae("参数错误，token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessgae("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username );
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessgae("token无效或已过期");
        }
        if (StringUtils.equals(fotgettoken,token)) {
            //MD5加密
            String MD5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int resultCount = userMapper.updatePasswordByUsername(username, MD5Password);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessgae("密码修改成功");
            }
        }else{
            return ServerResponse.createByErrorMessgae("token错误，请重新获取重置密码的tiken");
        }
        return ServerResponse.createByErrorMessgae("密码修改失败");
    }

    public ServerResponse<String> resetPassword(User user,String oldPassword,String newPassword){
        //为了防止横向跃权，需要校验用户与用户的密码，校验用户需要用用户ID（主键）和对应的密码
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if (resultCount > 0){
            String MD5Password = MD5Util.MD5EncodeUtf8(newPassword);
            user.setPassword(MD5Password);
            resultCount = userMapper.updateByPrimaryKeySelective(user);
            if (resultCount > 0){
                return ServerResponse.createBySuccessMessgae("密码更新成功");
            }
            return ServerResponse.createByErrorMessgae("密码更新失败");
        }
        return ServerResponse.createByErrorMessgae("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user){
        //username 是不能被更新的
        int resultCount = userMapper.checkEmailByUserID(user.getEmail(),user.getId());
        if (resultCount >0){
            return ServerResponse.createBySuccessMessgae("邮箱不经被使用，请输入新的邮箱地址");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0){
            System.out.println("test");
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessgae("封信信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessgae("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse checkAdminRole(User user){
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }





}
