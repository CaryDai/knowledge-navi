package com.dqj.knowledgenavi.controller;

import com.dqj.knowledgenavi.dataobject.ClassCodesDO;
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
}
