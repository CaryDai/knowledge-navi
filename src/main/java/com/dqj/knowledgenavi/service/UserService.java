package com.dqj.knowledgenavi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dqj.knowledgenavi.dao.UserInfoDOMapper;
import com.dqj.knowledgenavi.dao.UserPasswordDOMapper;
import com.dqj.knowledgenavi.dao.UserPatentsDOMapper;
import com.dqj.knowledgenavi.dao.UserSubjectsDOMapper;
import com.dqj.knowledgenavi.dataobject.*;
import com.dqj.knowledgenavi.service.model.UserModel;
import com.dqj.knowledgenavi.utils.TreeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    JdbcTemplate jdbcTemplate;

    // 用户手机号
//    private String userTel;
    private String userTel = "15957158337";

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
     * @param userSubject
     * @return
     */
    public boolean addSubject(String userSubject) {
        UserInfoDO userInfoDO = userInfoDOMapper.selectByTelephone(userTel);
        if (userInfoDO.getUserSubject1().equals("")) {
            userInfoDO.setUserSubject1("01" + userSubject);
        } else if (userInfoDO.getUserSubject2().equals("")) {
            userInfoDO.setUserSubject2("02" + userSubject);
        } else if (userInfoDO.getUserSubject3().equals("")) {
            userInfoDO.setUserSubject3("03" + userSubject);
        } else if (userInfoDO.getUserSubject4().equals("")) {
            userInfoDO.setUserSubject4("04" + userSubject);
        } else if (userInfoDO.getUserSubject5().equals("")) {
            userInfoDO.setUserSubject5("05" + userSubject);
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
        String prefix = judgeNum();
        userSubjectsDO.setClassCode(prefix + userTel.substring(7) + editNodeDO.getClassCode());
        userSubjectsDO.setParentCode(prefix + userTel.substring(7) + editNodeDO.getParentCode());
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
            String prefix = judgeNum();
            for (NodeDO node : nodes) {
                String classCode = prefix + userTel.substring(7) + node.getClassCode();
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
     *vv
     */
    public boolean writeSubjectNameAndDescription(String subjectName, String subjectDescription) {
        UserInfoDO userInfoDO = userInfoDOMapper.selectByTelephone(userTel);
        String num = judgeNum();
        switch (num) {
            case "01":
                userInfoDO.setSubject1Name(subjectName);
                userInfoDO.setSubject1Discription(subjectDescription);
                break;
            case "02":
                userInfoDO.setSubject2Name(subjectName);
                userInfoDO.setSubject2Discription(subjectDescription);
                break;
            case "03":
                userInfoDO.setSubject3Name(subjectName);
                userInfoDO.setSubject3Discription(subjectDescription);
                break;
            case "04":
                userInfoDO.setSubject4Name(subjectName);
                userInfoDO.setSubject4Discription(subjectDescription);
                break;
            case "05":
                userInfoDO.setSubject5Name(subjectName);
                userInfoDO.setSubject5Discription(subjectDescription);
                break;
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
     * @param num 第几个专题库
     * @return
     */
    public JSONArray constructUserSubjectTree(String num) {
        List<NodeDO> list = new ArrayList<>();
        List<UserSubjectsDO> userSubjectsDOS = userSubjectsDOMapper.selectRecodesByPrefix(num + userTel.substring(7));
        for (UserSubjectsDO userSubjectsDO : userSubjectsDOS) {
            String classCode = userSubjectsDO.getClassCode();
            String parentCode = userSubjectsDO.getParentCode();
            String className = userSubjectsDO.getClassName();
            NodeDO node = new NodeDO(classCode,parentCode,className);
            list.add(node);
        }
        JSONArray result = TreeUtil.listToTree(list, "classCode", "parentCode", "children");
//        System.out.println("构建的用户树" + JSON.toJSONString(result));
        return result;
    }

    /**
     * 获取用户所有的专利
     * @return
     */
    public List<PatentBriefDO> getUserPatents(String num) {
        List<PatentBriefDO> list = new ArrayList<>();
        String prefix = num + userTel.substring(7);
        List<String> publicationNos = userPatentsDOMapper.selectPublicationNosClassCodeByPrefix(prefix);
        return queryPatents(list,publicationNos);
    }

    /**
     * 根据勾选的节点获取该节点下用户的专利(非底层节点，根据节点号前缀查)
     * @param classCode
     * @return
     */
    public List<PatentBriefDO> getUserPatentsByClassIdPrefix(String classCode) {
        List<PatentBriefDO> list = new ArrayList<>();
        List<String> publicationNos = userPatentsDOMapper.selectPublicationNosClassCodeByPrefix(classCode);
        return queryPatents(list,publicationNos);
    }

    /**
     * 根据勾选的节点获取该节点下用户的专利(底层节点直接根据节点号查)
     * @param classCode
     * @return
     */
    public List<PatentBriefDO> getUserPatentsByClassId(String classCode) {
        List<PatentBriefDO> list = new ArrayList<>();
        List<String> publicationNos = userPatentsDOMapper.selectPublicationNosByClassCode(classCode);
        return queryPatents(list,publicationNos);
    }

    /**
     * 添加推荐的专利到自己的专题库
     * @param rootClassCode
     * @param patentName
     * @param publicationNo
     * @return
     */
    public boolean addToMySubject(String rootClassCode, String patentName, String publicationNo) {
        UserPatentsDO userPatentsDO = new UserPatentsDO();
        userPatentsDO.setClassCode(rootClassCode);
        userPatentsDO.setPatentName(patentName);
        userPatentsDO.setPublicationNo(publicationNo);
        int res = userPatentsDOMapper.insert(userPatentsDO);
        return res == 1;
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

    /**
     * 判断当前构建的是第几个专题库
     * @return
     */
    private String judgeNum() {
        UserInfoDO userInfoDO = userInfoDOMapper.selectByTelephone(userTel);
        String prefix = "";
        if (!userInfoDO.getUserSubject1().equals("") && userInfoDO.getSubject1Name().equals("")) {
            prefix = "01";
        } else if (!userInfoDO.getUserSubject2().equals("") && userInfoDO.getSubject2Name().equals("")) {
            prefix = "02";
        } else if (!userInfoDO.getUserSubject3().equals("") && userInfoDO.getSubject3Name().equals("")) {
            prefix = "03";
        } else if (!userInfoDO.getUserSubject4().equals("") && userInfoDO.getSubject4Name().equals("")) {
            prefix = "04";
        } else if (!userInfoDO.getUserSubject5().equals("") && userInfoDO.getSubject5Name().equals("")) {
            prefix = "05";
        }
        return prefix;
    }

    /**
     * 判断当前获取的是第几个专题库
     * @return
     */
    private String judgeNum2() {
        UserInfoDO userInfoDO = userInfoDOMapper.selectByTelephone(userTel);
        String prefix = "";
        if (!userInfoDO.getUserSubject1().equals("") && userInfoDO.getUserSubject2().equals("")) {
            prefix = "01";
        } else if (!userInfoDO.getUserSubject2().equals("") && userInfoDO.getUserSubject3().equals("")) {
            prefix = "02";
        } else if (!userInfoDO.getUserSubject3().equals("") && userInfoDO.getUserSubject4().equals("")) {
            prefix = "03";
        } else if (!userInfoDO.getUserSubject4().equals("") && userInfoDO.getUserSubject5().equals("")) {
            prefix = "04";
        } else if (!userInfoDO.getUserSubject5().equals("")) {
            prefix = "05";
        }
        return prefix;
    }

    /**
     *
     * @return
     */
    public List<PatentBriefDO> queryPatents(List<PatentBriefDO> list, List<String> publicationNos) {
        Set<String> uniquePublicationNos = new HashSet<>(publicationNos);
        for (String publicationNo : uniquePublicationNos) {
            String sql = "select * from patent where publication_no = '" + publicationNo + "'";
            // 暂时用jdbcTemplate查。根据公开号查专利
            Map<String, Object> patent = jdbcTemplate.queryForMap(sql);
            String name = (String) patent.get("name");
            String inventors = (String) patent.get("inventors");
            String applicant = (String) patent.get("applicant");
            String patentAbstract = (String) patent.get("abstract");
            String publicationNO = (String) patent.get("publication_no");

            PatentBriefDO patentBriefDO = new PatentBriefDO();
            patentBriefDO.setName(name);
            patentBriefDO.setInventors(inventors);
            patentBriefDO.setApplicant(applicant);
            patentBriefDO.setPatentAbstract(patentAbstract);
            patentBriefDO.setPublicationNO(publicationNO);
            list.add(patentBriefDO);
        }
        return list;
    }
}
