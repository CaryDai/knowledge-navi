package com.dqj.knowledgenavi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dqj.knowledgenavi.dao.UserInfoDOMapper;
import com.dqj.knowledgenavi.dao.UserPasswordDOMapper;
import com.dqj.knowledgenavi.dao.UserPatentsDOMapper;
import com.dqj.knowledgenavi.dao.UserSubjectsDOMapper;
import com.dqj.knowledgenavi.dataobject.*;
import com.dqj.knowledgenavi.service.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.dqj.knowledgenavi.utils.TreeUtil.listToTree;

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

    @Autowired
    private UserSubjectsDOMapper userSubjectsDOMapper;

    @Autowired
    private UserPatentsDOMapper userPatentsDOMapper;

    // 用户手机号
    private String userTel;
//    private String userTel = "15957158337";

    @Data
    @AllArgsConstructor
    private static class Node {
        private String classCode;
        private String parentCode;
        private String title;
    }

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
        userTel = telephone;
        return true;
    }

    /**
     * 更新user_info表的user_subject字段
     * @param telephone
     * @param userSubject
     * @return
     */
    public boolean addSubject(String telephone, String userSubject) {
        UserInfoDO userInfoDO = userInfoDOMapper.selectByTelephone(telephone);
        if (userInfoDO.getUserSubject1().equals("")) {
            userInfoDO.setUserSubject1(userSubject);
        } else if (userInfoDO.getUserSubject2().equals("")) {
            userInfoDO.setUserSubject2(userSubject);
        } else if (userInfoDO.getUserSubject3().equals("")) {
            userInfoDO.setUserSubject2(userSubject);
        } else if (userInfoDO.getUserSubject4().equals("")) {
            userInfoDO.setUserSubject2(userSubject);
        } else if (userInfoDO.getUserSubject5().equals("")) {
            userInfoDO.setUserSubject2(userSubject);
        }
        int res = userInfoDOMapper.updateByPrimaryKeySelective(userInfoDO);
        return res == 1;
    }

    /**
     * 更新user_subjects表
     * @param editNodeDO
     * @return
     */
    public boolean updateUserSubjects(EditNodeDO editNodeDO) {
        UserSubjectsDO userSubjectsDO = new UserSubjectsDO();
        userSubjectsDO.setClassCode(userTel.substring(7) + editNodeDO.getClassCode());
        userSubjectsDO.setParentCode(userTel.substring(7) + editNodeDO.getParentCode());
        userSubjectsDO.setClassName(editNodeDO.getTitle());
        userSubjectsDOMapper.insertSelective(userSubjectsDO);
        EditNodeDO[] children = editNodeDO.getChildren();
        if (children != null && children.length > 0) {
            for (EditNodeDO child : children) {
                updateUserSubjects(child);
            }
        }
        return true;
    }

    /**
     * 更新user_patents表
     * @param list
     * @return
     */
    public boolean updateUserPatents(List<PatentCheckNodesDO> list) {
        for (PatentCheckNodesDO patentCheckNodesDO : list) {
            PatentBriefDO patentBriefDO = patentCheckNodesDO.getPatentBriefDO();
            String nameWithem = patentBriefDO.getName();
            String patentNameWithoutem = nameWithem.replaceAll("<em>", "");
            String patentName = patentNameWithoutem.replaceAll("</em>", "");
            String publicationNO = patentBriefDO.getPublicationNO();
            NodeDO[] nodes = patentCheckNodesDO.getNodeDOS();
            for (NodeDO node : nodes) {
                String classCode = userTel.substring(7) + node.getClassCode();
                UserPatentsDO userPatentsDO = new UserPatentsDO();
                userPatentsDO.setPatentName(patentName);
                userPatentsDO.setPublicationNo(publicationNO);
                userPatentsDO.setClassCode(classCode);
                userPatentsDOMapper.insertSelective(userPatentsDO);
            }
        }
        return true;
    }

    /**
     * 更新user_info表，添加专题库名和专题库描述信息
     * @param subjectName
     * @param subjectDescription
     * @return
     */
    public boolean writeSubjectNameAndDescription(String subjectName, String subjectDescription) {
        UserInfoDO userInfoDO = userInfoDOMapper.selectByTelephone(userTel);
        if (!userInfoDO.getUserSubject1().equals("") && userInfoDO.getSubject1Name().equals("") && userInfoDO.getSubject1Discription().equals("")) {
            userInfoDO.setSubject1Name(subjectName);
            userInfoDO.setSubject1Discription(subjectDescription);
        } else if (!userInfoDO.getUserSubject2().equals("") && userInfoDO.getSubject2Name().equals("") && userInfoDO.getSubject2Discription().equals("")) {
            userInfoDO.setSubject2Name(subjectName);
            userInfoDO.setSubject2Discription(subjectDescription);
        } else if (!userInfoDO.getUserSubject3().equals("") && userInfoDO.getSubject3Name().equals("") && userInfoDO.getSubject3Discription().equals("")) {
            userInfoDO.setSubject3Name(subjectName);
            userInfoDO.setSubject3Discription(subjectDescription);
        } else if (!userInfoDO.getUserSubject4().equals("") && userInfoDO.getSubject4Name().equals("") && userInfoDO.getSubject4Discription().equals("")) {
            userInfoDO.setSubject4Name(subjectName);
            userInfoDO.setSubject4Discription(subjectDescription);
        } else if (!userInfoDO.getUserSubject5().equals("") && userInfoDO.getSubject5Name().equals("") && userInfoDO.getSubject5Discription().equals("")) {
            userInfoDO.setSubject5Name(subjectName);
            userInfoDO.setSubject5Discription(subjectDescription);
        }
        int res = userInfoDOMapper.updateByPrimaryKeySelective(userInfoDO);
        return res == 1;
    }

    /**
     * 获取专题库名和描述信息
     * @return
     */
    public UserInfoDO getSubjectNameAndDescription() {
        return userInfoDOMapper.selectByTelephone(userTel);
    }

    /**
     * 构建用户专题树
     */
    public void constructUserSubjectTree() {
        List<Node> list = new ArrayList<>();
        List<UserSubjectsDO> userSubjectsDOS = userSubjectsDOMapper.selectRecodesByPrefix(userTel.substring(7));
        for (UserSubjectsDO userSubjectsDO : userSubjectsDOS) {
            String classCode = userSubjectsDO.getClassCode();
            String parentCode = userSubjectsDO.getParentCode();
            String className = userSubjectsDO.getClassName();
            Node node = new Node(classCode,parentCode,className);
            list.add(node);
        }
        JSONArray result = listToTree(list, "class_code", "parent_code", "children");
        System.out.println(JSON.toJSONString(result));
    }

    private UserInfoDO convertFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        BeanUtils.copyProperties(userModel,userInfoDO);
        userInfoDO.setUserSubject1(null);
        userInfoDO.setSubject1Name(null);
        userInfoDO.setSubject1Discription(null);
        userInfoDO.setUserSubject2(null);
        userInfoDO.setSubject2Name(null);
        userInfoDO.setSubject2Discription(null);
        userInfoDO.setUserSubject3(null);
        userInfoDO.setSubject3Name(null);
        userInfoDO.setSubject3Discription(null);
        userInfoDO.setUserSubject4(null);
        userInfoDO.setSubject4Name(null);
        userInfoDO.setSubject4Discription(null);
        userInfoDO.setUserSubject5(null);
        userInfoDO.setSubject5Name(null);
        userInfoDO.setSubject5Discription(null);
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
