package com.dqj.knowledgenavi.service;

import com.dqj.knowledgenavi.dataobject.ClassCodesDO;
import com.dqj.knowledgenavi.dataobject.PatentBriefDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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

    // 用于在getPatentsByClassId方法中记录上次查询表patent_classId的行
    private int lastQueryRow;
    private int lastQueryRowPrefix;
    // 用哈希表来记录传入的页码是否已被用户点击过，若已被点击，则说明这一页展示的数据已被查询过。
    // key记录页码，value记录对应的lastQueryRow
    private HashMap<Integer,Integer> recordPage;
    private HashMap<Integer,Integer> recordPagePrefix;

    public ClassCodesService() {
        recordPage = new HashMap<>();
        recordPagePrefix = new HashMap<>();
        // 第1页是从第0条开始查
        recordPage.put(1,0);
        recordPagePrefix.put(1,0);
    }

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
     * 根据classId先查patent_classId表得到publication_no，随后根据publication_no查patent表得到专利详细数据
     * 每次查10条专利
     * @param classId
     * @param pageNo
     * @return
     */
    public List<PatentBriefDO> getPatentsByClassId(String classId, int pageNo, int queryNum) {
        int num = 0;
        List<PatentBriefDO> res = new ArrayList<>();
        // 该页的内容之前已经查询过了
        if (recordPage.containsKey(pageNo)) {
            lastQueryRow = recordPage.get(pageNo);
        }
        // 有些公开号对应的专利可能不存在，所以要通过while循环来不断地查
        while (num < queryNum) {
            // 获取专利公开号
            String sql = "select id,publication_no from patent_classId where class_id = '" + classId + "' limit " + lastQueryRow + ", " + queryNum;
            List<Map<String,Object>> publicationNos = jdbcTemplate.queryForList(sql);
            // 没有专利了
            if (publicationNos.size() == 0) {
                break;
            }
            for (Map<String,Object> map : publicationNos) {
                String publicationNo = (String) map.get("publication_no");
                // 根据公开号获取专利名等专利详细信息
                try {
                    String sql2 = "select * from patent where publication_no = '" + publicationNo + "'";
                    Map<String, Object> patent = jdbcTemplate.queryForMap(sql2);
                    String name = (String) patent.get("name");
                    String inventors = (String) patent.get("inventors");
                    String applicant = (String) patent.get("applicant");
                    String patentAbstract = (String) patent.get("abstract");

                    PatentBriefDO patentBriefDO = new PatentBriefDO();
                    patentBriefDO.setName(name);
                    patentBriefDO.setInventors(inventors);
                    patentBriefDO.setApplicant(applicant);
                    patentBriefDO.setPatentAbstract(patentAbstract);
                    res.add(patentBriefDO);

                    num++;
                    lastQueryRow = (int) map.get("id");
                    if (num == queryNum) {
                        break;
                    }
                } catch (EmptyResultDataAccessException ignored) {}
            }
        }
        recordPage.put(pageNo+1,lastQueryRow);
        return res;
    }

    /**
     * 对于不是最下层的节点，根据分类号的前缀去查
     * @param classId
     * @param pageNo
     * @return
     */
    public List<PatentBriefDO> getPatentsByClassIdPrefix(String classId, int pageNo, int queryNum) {
        int num = 0;
        List<PatentBriefDO> res = new ArrayList<>();
        if (recordPagePrefix.containsKey(pageNo)) {
            lastQueryRowPrefix = recordPagePrefix.get(pageNo);
        }
        // 有些公开号对应的专利可能不存在，所以要通过while循环来不断地查
        while (num < queryNum) {
            // 获取专利公开号
            String sql = "select id,publication_no from patent_classId where class_id LIKE '" + classId + "%' limit " + lastQueryRowPrefix + ", " + queryNum;
            List<Map<String,Object>> publicationNos = jdbcTemplate.queryForList(sql);
            // 没有专利了
            if (publicationNos.size() == 0) {
                break;
            }
            int tmp = lastQueryRowPrefix;
            // 遍历公开号
            for (Map<String,Object> map : publicationNos) {
                String publicationNo = (String) map.get("publication_no");
                // 根据公开号获取专利名等专利详细信息
                try {
                    String sql2 = "select * from patent where publication_no = '" + publicationNo + "'";
                    Map<String, Object> patent = jdbcTemplate.queryForMap(sql2);
                    String name = (String) patent.get("name");
                    String inventors = (String) patent.get("inventors");
                    String applicant = (String) patent.get("applicant");
                    String patentAbstract = (String) patent.get("abstract");

                    PatentBriefDO patentBriefDO = new PatentBriefDO();
                    patentBriefDO.setName(name);
                    patentBriefDO.setInventors(inventors);
                    patentBriefDO.setApplicant(applicant);
                    patentBriefDO.setPatentAbstract(patentAbstract);
                    res.add(patentBriefDO);

                    num++;
                    lastQueryRowPrefix = (int) map.get("id");
                    if (num == queryNum) {
                        break;
                    }
                } catch (EmptyResultDataAccessException ignored) {}
            }
            if (lastQueryRowPrefix == tmp) {
                lastQueryRowPrefix += queryNum;
            }
        }
        recordPagePrefix.put(pageNo+1,lastQueryRowPrefix);
        return res;
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
