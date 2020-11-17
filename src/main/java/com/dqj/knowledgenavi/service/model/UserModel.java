package com.dqj.knowledgenavi.service.model;

/**
 * @Author dqj
 * @Date 2020/11/17
 * @Version 1.0
 * @Description 用于业务逻辑处理的用户领域模型
 */
public class UserModel {
    private Integer id;
    private String userName;
    private String telephone;
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
