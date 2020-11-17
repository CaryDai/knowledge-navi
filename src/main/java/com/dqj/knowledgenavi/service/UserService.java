package com.dqj.knowledgenavi.service;

import com.dqj.knowledgenavi.dao.UserInfoDOMapper;
import com.dqj.knowledgenavi.dao.UserPasswordDOMapper;
import com.dqj.knowledgenavi.dataobject.UserInfoDO;
import com.dqj.knowledgenavi.dataobject.UserPasswordDO;
import com.dqj.knowledgenavi.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @Author dqj
 * @Date 2020/11/17
 * @Version 1.0
 * @Description
 */
@Service
public class UserService {
    @Autowired
    private UserInfoDOMapper userInfoDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    /**
     * 用户注册
     * @param userModel
     */
    public void register(UserModel userModel) {
        UserInfoDO userInfoDO = convertFromUserModel(userModel);
        try {
            userInfoDOMapper.insertSelective(userInfoDO);
        } catch (DuplicateKeyException ex) {
            System.out.println("手机号已注册");
        }
        userModel.setId(userInfoDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }

    /**
     * 登陆验证
     * @param telephone
     * @param password
     */
    public boolean validateLogin(String telephone, String password) {
        UserInfoDO userInfoDO = userInfoDOMapper.selectByTelephone(telephone);
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userInfoDO.getId());
        UserModel userModel = convertFromDataObject(userInfoDO,userPasswordDO);
        if (!password.equals(userModel.getPassword())) {
            System.out.println("密码错误");
            return false;
        }
        return true;
    }

    private UserInfoDO convertFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        BeanUtils.copyProperties(userModel,userInfoDO);
        userInfoDO.setUserSubject(null);
        return userInfoDO;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserModel convertFromDataObject(UserInfoDO userInfoDO, UserPasswordDO userPasswordDO) {
        if (userInfoDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfoDO, userModel);

        if (userPasswordDO != null) {
            // 这里不能copy，因为id字段会重复
            userModel.setPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
