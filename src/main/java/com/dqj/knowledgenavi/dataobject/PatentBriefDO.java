package com.dqj.knowledgenavi.dataobject;

/**
 * @Author dqj
 * @Date 2020/10/26
 * @Version 1.0
 * @Description 专利简要数据对象
 */
public class PatentBriefDO {
    private String name;
    private String inventors; // 发明人
    private String applicant;   // 申请人
    private String patentAbstract;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInventors() {
        return inventors;
    }

    public void setInventors(String inventors) {
        this.inventors = inventors;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getPatentAbstract() {
        return patentAbstract;
    }

    public void setPatentAbstract(String patentAbstract) {
        this.patentAbstract = patentAbstract;
    }
}
