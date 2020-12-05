package com.dqj.knowledgenavi.dao;

import com.dqj.knowledgenavi.dataobject.UserInfoDO;

public interface UserInfoDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Tue Nov 17 11:14:56 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Tue Nov 17 11:14:56 CST 2020
     */
    int insert(UserInfoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Tue Nov 17 11:14:56 CST 2020
     */
    int insertSelective(UserInfoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Tue Nov 17 11:14:56 CST 2020
     */
    UserInfoDO selectByPrimaryKey(Integer id);

    UserInfoDO selectByTelephone(String telephone);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Tue Nov 17 11:14:56 CST 2020
     */
    int updateByPrimaryKeySelective(UserInfoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Tue Nov 17 11:14:56 CST 2020
     */
    int updateByPrimaryKey(UserInfoDO record);
}