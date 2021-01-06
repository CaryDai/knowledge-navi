package com.dqj.knowledgenavi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dqj.knowledgenavi.dao.UserSubjectsDOMapper;
import com.dqj.knowledgenavi.dataobject.ClassCodesDO;
import com.dqj.knowledgenavi.dataobject.NodeDO;
import com.dqj.knowledgenavi.dataobject.UserSubjectsDO;
import com.dqj.knowledgenavi.utils.TreeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dqj.knowledgenavi.utils.TreeUtil.listToTree;

@SpringBootTest
public class KnowledgeNaviApplicationTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private UserSubjectsDOMapper userSubjectsDOMapper;

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
        System.out.println(JSON.toJSONString(result));
//        File f = new File("D:\\论文\\知识导航平台构建\\view-ui-project-4.0\\static\\ipc_h.json");
//        FileOutputStream fop = new FileOutputStream(f);
//        OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
//        writer.append(JSON.toJSONString(result));
//        writer.close();
//        fop.close();
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

    /**
     * 添加patent_classID表信息，专利的分类号和公开号
     * @throws IOException
     */
    @Test
    public void addClassId() throws IOException {
//        FileInputStream inputStream = new FileInputStream("D:\\论文\\知识导航平台构建\\专利分类\\专利\\基础科学\\A005_3.txt");
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        String str;
//        while ((str = bufferedReader.readLine()) != null) {
//            System.out.println(str);
//        }
//        inputStream.close();
//        bufferedReader.close();
        String path = "D:\\论文\\知识导航平台构建\\专利分类\\专利\\informationscience";
        File dir = new File(path);
        String[] files = dir.list();
        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;
        for (String f : files) {
            inputStream = new FileInputStream(path + "\\" + f);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            String classId = f.substring(0,f.indexOf("."));
            while ((str = bufferedReader.readLine()) != null) {
                String sql = "INSERT INTO `patent_classId` (`class_id`, `publication_no`) VALUES (?, ?);";
                jdbcTemplate.update(sql, classId, str);
            }
            System.out.println(classId + "添加完毕！");
        }
        inputStream.close();
        bufferedReader.close();
    }

    /**
     * 测试查询专利信息
     */
    @Test
    public void testGetPatentsByClassId() {
        int num = 0;
        while (num < 10) {
            // 获取专利公开号
            String sql = "select publication_no from patent_classId where class_id = 'A005_3' limit 10";
            List<Map<String, Object>> publicationNos = jdbcTemplate.queryForList(sql);
            for (Map<String,Object> map : publicationNos) {
//                System.out.println(map.get("publication_no"));
                String publicationNo = (String) map.get("publication_no");
                // 根据公开号获取专利名等专利详细信息
                try {
                    String sql2 = "select name from patent where publication_no = '" + publicationNo + "'";
                    Map<String, Object> patent = jdbcTemplate.queryForMap(sql2);
                    System.out.println(patent.get("name"));
                    num++;
                    if (num == 10) {
                        break;
                    }
                } catch (EmptyResultDataAccessException ignored) {}
            }
        }
    }

    /**
     * 更新patent_classId表的专利名
     */
    @Test
    public void updatePatentName() {
        String sql1 = "select * from patent_classId limit 17000000, 1000000";
        List<Map<String, Object>> publicationNos = jdbcTemplate.queryForList(sql1);
        for (Map<String,Object> map : publicationNos) {
            String publicationNo = (String) map.get("publication_no");
            try {
                String sql2 = "select name from patent where publication_no = '" + publicationNo + "'";
                Map<String, Object> patent = jdbcTemplate.queryForMap(sql2);
                String patentName = (String) patent.get("name");
                if (patentName != null) {
                    System.out.println(publicationNo);
                    System.out.println(patentName);
                    String sql3 = "update patent_classId set name = '" + patentName + "' where publication_no = '" + publicationNo + "'";
                    jdbcTemplate.update(sql3);
                }
            } catch (Exception ignored) {}
        }
    }

    @Test
    void constructUserSubjectTree(){
        List<NodeDO> list = new ArrayList<>();
        List<UserSubjectsDO> userSubjectsDOS = userSubjectsDOMapper.selectRecodesByPrefix("15957158337".substring(7));
        for (UserSubjectsDO userSubjectsDO : userSubjectsDOS) {
            String classCode = userSubjectsDO.getClassCode();
            String parentCode = userSubjectsDO.getParentCode();
            String className = userSubjectsDO.getClassName();
            NodeDO node = new NodeDO(classCode,parentCode,className);
            list.add(node);
        }
        JSONArray result = TreeUtil.listToTree(list, "class_code", "parent_code", "children");
        System.out.println(JSON.toJSONString(result));
    }
}
