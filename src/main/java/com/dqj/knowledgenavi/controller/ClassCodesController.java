package com.dqj.knowledgenavi.controller;

import com.dqj.knowledgenavi.dataobject.ClassCodesDO;
import com.dqj.knowledgenavi.dataobject.PatentBriefDO;
import com.dqj.knowledgenavi.dataobject.PatentDetailDO;
import com.dqj.knowledgenavi.service.ClassCodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author dqj
 * @Date 2020/6/24
 * @Version 1.0
 * @Description
 */
@Controller("classCodes")
@RequestMapping("/classcodes")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")    // 处理跨域请求
public class ClassCodesController {

    @Autowired
    private ClassCodesService classCodesService;

    @RequestMapping(value = "/getClassCodes", method = {RequestMethod.GET})
    @ResponseBody
    public List<ClassCodesDO> getClassCodes(@RequestParam(value = "table") String table) {
        List<ClassCodesDO> classCodes = classCodesService.getClassCodes(table);
        return classCodes;
    }

    @RequestMapping(value = "/getByParentCode", method = {RequestMethod.GET})
    @ResponseBody
    public List<ClassCodesDO> getByParentCode(@RequestParam(value = "table") String table,
                                              @RequestParam(value = "parentCode") String parentCode) {
        List<ClassCodesDO> classCodes = classCodesService.getByParentCode(table, parentCode);
        return classCodes;
    }

    /**
     * 最下层的分类直接根据classId查
     * @param classId
     * @return
     */
    @RequestMapping(value = "/getPatentsByClassId", method = {RequestMethod.GET})
    @ResponseBody
    public List<PatentBriefDO> getPatentsByClassId(@RequestParam(value = "classId") String classId,
                                                   @RequestParam(value = "pageNo") int pageNo,
                                                   @RequestParam(value = "queryNum") int queryNum) {
        List<PatentBriefDO> patents = classCodesService.getPatentsByClassId(classId, pageNo, queryNum);
        return patents;
    }

    /**
     * 不是最下层的分类需要根据前缀去数据库中查
     * @param classId
     * @return
     */
    @RequestMapping(value = "/getPatentsByClassIdPrefix", method = {RequestMethod.GET})
    @ResponseBody
    public List<PatentBriefDO> getPatentsByClassIdPrefix(@RequestParam(value = "classId") String classId,
                                                         @RequestParam(value = "pageNo") int pageNo,
                                                         @RequestParam(value = "queryNum") int queryNum) {
        List<PatentBriefDO> patents = classCodesService.getPatentsByClassIdPrefix(classId, pageNo, queryNum);
        return patents;
    }

    @RequestMapping(value = "/getPatentDetail", method = {RequestMethod.GET})
    @ResponseBody
    public PatentDetailDO getPatentDetail(@RequestParam(value = "publicationNO") String publicationNO) {
        PatentDetailDO patentDetail = classCodesService.queryPatent(publicationNO);
        return patentDetail;
    }
}
