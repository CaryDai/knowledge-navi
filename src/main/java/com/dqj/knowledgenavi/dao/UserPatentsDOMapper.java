package com.dqj.knowledgenavi.dao;

import com.dqj.knowledgenavi.dataobject.UserPatentsDO;

public interface UserPatentsDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_patents
     *
     * @mbg.generated Wed Jan 06 13:45:20 CST 2021
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_patents
     *
     * @mbg.generated Wed Jan 06 13:45:20 CST 2021
     */
    int insert(UserPatentsDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_patents
     *
     * @mbg.generated Wed Jan 06 13:45:20 CST 2021
     */
    int insertSelective(UserPatentsDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_patents
     *
     * @mbg.generated Wed Jan 06 13:45:20 CST 2021
     */
    UserPatentsDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_patents
     *
     * @mbg.generated Wed Jan 06 13:45:20 CST 2021
     */
    int updateByPrimaryKeySelective(UserPatentsDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_patents
     *
     * @mbg.generated Wed Jan 06 13:45:20 CST 2021
     */
    int updateByPrimaryKey(UserPatentsDO record);
}