package com.dqj.knowledgenavi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dqj.knowledgenavi.dataobject.ClassCodesDO;
import com.dqj.knowledgenavi.dataobject.NodeDO;
import com.dqj.knowledgenavi.utils.TreeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class KnowledgeNaviApplicationTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void testTreeUtils() throws Exception {
        List<NodeDO> list = new ArrayList<>();

        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from ipc_h");
        for (Map<String, Object> record : mapList) {
            String classCode = (String) record.get("ipc_code");
            String parentCode = (String) record.get("parent_code");
            String title = (String) record.get("ipc_name");
            NodeDO node = new NodeDO(classCode, parentCode, title);
            list.add(node);
        }

        JSONArray result = TreeUtil.listToTree(list, "classCode", "parentCode", "children");
//        System.out.println(JSON.toJSONString(result));
        File f = new File("D:\\论文\\知识导航平台构建\\view-ui-project-4.0\\static\\ipc_h.json");
        FileOutputStream fop = new FileOutputStream(f);
        OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
        writer.append(JSON.toJSONString(result));
        writer.close();
        fop.close();
    }

    @Test
    public void getClassCodes() {
        List<ClassCodesDO> resList = new ArrayList<>();
//        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from " + table);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from agriculture_tech limit 5");
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
            System.out.println(classCodesDO);
        }
    }
}
