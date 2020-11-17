package com.dqj.knowledgenavi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author dqj
 * @Date 2020/6/30
 * @Version 1.0
 * @Description
 */
public class TreeUtil {

    @Autowired
    static JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        List<Node> list = new ArrayList<>();

        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from basic_science");
        for (Map<String, Object> record : mapList) {
            String classCode = (String) record.get("class_code");
            String parentCode = (String) record.get("parent_code");
            String className = (String) record.get("class_name");
            Node node = new Node(classCode, parentCode, className);
            list.add(node);
        }

        JSONArray result = listToTree(list, "class_code", "parent_code", "children");
        System.out.println(JSON.toJSONString(result));
    }

    public static JSONArray listToTree(List<?> list, String idStr, String pidStr, String childrenStr) {
        JSONArray arr = JSONArray.parseArray(JSON.toJSONString(list));
        JSONArray r = new JSONArray();
        JSONObject hash = new JSONObject();
        //将数组转为Object的形式，key为数组中的id
        arr.forEach(x -> {
            JSONObject json = (JSONObject) x;
            hash.put(json.getString(idStr), json);
        });
        //遍历结果集
        arr.forEach(x -> {
            //单条记录
            JSONObject aVal = (JSONObject) x;
            //在hash中取出key为单条记录中pid的值
            JSONObject hashValueParent = (JSONObject) hash.get(aVal.get(pidStr).toString());
            //如果记录的pid存在，则说明它有父节点，将她添加到孩子节点的集合中
            if (hashValueParent != null) {
                //检查是否有child属性
                if (hashValueParent.get(childrenStr) != null) {
                    //已经存在子节点
                    JSONArray children = (JSONArray) hashValueParent.get(childrenStr);
                    children.add(aVal);
                    hashValueParent.put(childrenStr, children);
                } else {
                    //创建一个子节点
                    JSONArray children = new JSONArray();
                    children.add(aVal);
                    hashValueParent.put(childrenStr, children);
                }
            } else {
                r.add(aVal);
            }
        });
        return r;
    }

    @Data
    @AllArgsConstructor
    private static class Node {
        private String classCode;
        private String parentCode;
        private String title;
    }
}
