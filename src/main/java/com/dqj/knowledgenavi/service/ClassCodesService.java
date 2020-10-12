package com.dqj.knowledgenavi.service;

import com.dqj.knowledgenavi.dataobject.ClassCodesDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author dqj
 * @Date 2020/6/24
 * @Version 1.0
 * @Description
 */
@Service
public class ClassCodesService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 根据表名从数据库中取出节点信息
     * @param table
     * @return
     */
    public List<ClassCodesDO> getClassCodes(String table) {
        List<ClassCodesDO> resList = new ArrayList<>();
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from " + table);
//        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from " + table + " limit 5");
        return this.transferDataToList(resList, mapList);
    }

    /**
     * 根据表名和parentCode从数据库中取出节点信息
     * @param table
     * @param parentCode
     * @return
     */
    public List<ClassCodesDO> getByParentCode(String table, String parentCode) {
        List<ClassCodesDO> resList = new ArrayList<>();
        List<Map<String, Object>> mapList =
                jdbcTemplate.queryForList("select * from " + table + " where parent_code = '" + parentCode + "'");
        return this.transferDataToList(resList, mapList);
    }

    /**
     * 工具方法，将select * 的结果都转化成list
     * @param resList
     * @param mapList
     * @return
     */
    private List<ClassCodesDO> transferDataToList(List<ClassCodesDO> resList, List<Map<String, Object>> mapList) {
        for (Map<String, Object> record : mapList) {
            ClassCodesDO classCodesDO = new ClassCodesDO();

            int id = (int) record.get("id");
            String className = (String) record.get("class_name");
            String classCode = (String) record.get("class_code");
            String parentCode = (String) record.get("parent_code");
            int itemLevel = (int) record.get("item_level");

            classCodesDO.setId(id);
            classCodesDO.setClassName(className);
            classCodesDO.setClassCode(classCode);
            classCodesDO.setParentCode(parentCode);
            classCodesDO.setItemLevel(itemLevel);

            resList.add(classCodesDO);
        }
        return resList;
    }
}
