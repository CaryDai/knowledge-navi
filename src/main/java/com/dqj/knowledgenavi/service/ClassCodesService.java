package com.dqj.knowledgenavi.service;

import com.dqj.knowledgenavi.dataobject.ClassCodesDO;
import com.dqj.knowledgenavi.dataobject.PatentBriefDO;
import com.dqj.knowledgenavi.dataobject.PatentDetailDO;
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
    private HashMap<String,Integer> startRowMap;
    // 存储数字与专利类型的对应关系
    private HashMap<Integer, String> patentTypeMap;

    public ClassCodesService() {
        recordPage = new HashMap<>();
        recordPagePrefix = new HashMap<>();
        patentTypeMap = new HashMap<>();
        // 第1页是从第0条开始查
        recordPage.put(1,0);
        recordPagePrefix.put(1,0);
        startRowMap = new HashMap<>();
        startRowMap.put("A",1);
        startRowMap.put("B",1335686);
        startRowMap.put("C",16124307);
        startRowMap.put("D",262170);
        startRowMap.put("E",15245900);
        startRowMap.put("I",16975401);

        patentTypeMap.put(1,"发明专利");
        patentTypeMap.put(2,"实用新型");
        patentTypeMap.put(3,"外观设计");
        patentTypeMap.put(8,"中国国家阶段的PCT发明专利申请");
        patentTypeMap.put(9,"中国国家阶段的PCT实用新型专利申请");
        patentTypeMap.put(4,"其他");
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
            String sql;
            if (pageNo == 1 && classId.length() == 1) {
                int startRow = startRowMap.get(classId);
                sql = "select id,publication_no from patent_classId where class_id = '" + classId + "' and id >= " + startRow + " limit " + num + ", " + queryNum;
            } else {
                sql = "select id,publication_no from patent_classId where class_id = '" + classId + "' and id >= " + lastQueryRow + " limit " + num + ", " + queryNum;
            }
            List<Map<String,Object>> publicationNos = jdbcTemplate.queryForList(sql);
            // 没有专利了
            if (publicationNos.size() == 0) {
                break;
            }
            int tmp = lastQueryRow;
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
            if (lastQueryRow == tmp) {
                lastQueryRow += queryNum;
            }
        }
        recordPage.put(pageNo+1,lastQueryRow);
        return res;
    }

    /**
     * 不是最下层的节点，根据分类号的前缀去查
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
            String sql;
            if (pageNo == 1 && classId.length() == 1) {
                int startRow = startRowMap.get(classId);
                sql = "select id,publication_no from patent_classId where class_id LIKE '" + classId + "%' and id >= " + startRow + " limit " + num + ", " + queryNum;
            } else {
                sql = "select id,publication_no from patent_classId where class_id LIKE '" + classId + "%' and id >= " + lastQueryRowPrefix + " limit " + num + ", " + queryNum;
            }
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
                    String publicationNO = (String) patent.get("publication_no");

                    PatentBriefDO patentBriefDO = new PatentBriefDO();
                    patentBriefDO.setName(name);
                    patentBriefDO.setInventors(inventors);
                    patentBriefDO.setApplicant(applicant);
                    patentBriefDO.setPatentAbstract(patentAbstract);
                    patentBriefDO.setPublicationNO(publicationNO);
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
     * 根据公开号获取专利详细信息
     * @param publicationNO
     * @return
     */
    public PatentDetailDO queryPatent(String publicationNO) {
        String sql = "select * from patent where publication_no = '" + publicationNO + "'";
        Map<String, Object> patent = jdbcTemplate.queryForMap(sql);
        String name = (String) patent.get("name");
        String inventors = (String) patent.get("inventors");
        String applicant = (String) patent.get("applicant");
        String abstractCh = (String) patent.get("abstract");
        String applicationNo = (String) patent.get("application_no");
        String mainClassificationNo = (String) patent.get("main_classification_no");
        int patentTypeNum = Integer.parseInt((String)patent.get("patent_type"));
        String patentType = patentTypeMap.get(patentTypeNum);
        String applicationDate = (String) patent.get("application_date");
        String publicationDate = (String) patent.get("publication_date");
        String classificationNo = (String) patent.get("classification_no");
        String applicantAddress = (String) patent.get("applicant_address");
        String sovereignty = (String) patent.get("sovereignty");

        PatentDetailDO patentDetailDO = new PatentDetailDO();
        patentDetailDO.setName(name);
        patentDetailDO.setInventors(inventors);
        patentDetailDO.setApplicant(applicant);
        patentDetailDO.setAbstractCh(abstractCh);
        patentDetailDO.setApplicationNo(applicationNo);
        patentDetailDO.setPublicationNo(publicationNO);
        patentDetailDO.setMainClassificationNo(mainClassificationNo);
        patentDetailDO.setPatentType(patentType);
        patentDetailDO.setApplicationDate(applicationDate);
        patentDetailDO.setPublicationDate(publicationDate);
        patentDetailDO.setClassificationNo(classificationNo);
        patentDetailDO.setApplicantAddress(applicantAddress);
        patentDetailDO.setSovereignty(sovereignty);

        return patentDetailDO;
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
