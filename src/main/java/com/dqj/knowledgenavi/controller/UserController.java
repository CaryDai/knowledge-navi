package com.dqj.knowledgenavi.controller;

import com.dqj.knowledgenavi.dataobject.UserInfoDO;
import com.dqj.knowledgenavi.service.UserService;
import com.dqj.knowledgenavi.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author dqj
 * @Date 2020/11/17
 * @Version 1.0
 * @Description 用户操作相关接口
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")    // 处理跨域请求
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    @ResponseBody
    public String register(@RequestParam(value = "name") String name,
                           @RequestParam(value = "telephone") String telephone,
                           @RequestParam(value = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserModel userModel = new UserModel();
        // 用户注册流程
        userModel.setUserName(name);
        userModel.setTelephone(telephone);
        userModel.setPassword(EncodeByMd5(password));

        userService.register(userModel);
        return "register ok";
    }

    // 另外编写加密方法
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 加密字符串
        return base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
    }

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam(value = "telephone") String telephone,
                      @RequestParam(value = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        boolean res = userService.validateLogin(telephone, this.EncodeByMd5(password));
        if (res) {
            System.out.println("登陆成功");
            return "True";
        } else {
            System.out.println("登陆失败");
            return "False";
        }
    }
}
