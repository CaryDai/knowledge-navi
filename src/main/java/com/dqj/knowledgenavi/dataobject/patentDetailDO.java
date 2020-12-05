package com.dqj.knowledgenavi.dataobject;

/**
 * @Author dqj
 * @Date 2020/11/20
 * @Version 1.0
 * @Description
 */
public class patentDetailDO {
    /**
     * 专利名
     */
    private String name;
    /**
     * 发明人
     */
    private String inventors;
    /**
     * 申请人
     */
    private String applicant;
    /**
     * 摘要
     */
    private String abstractCh;
    /**
     * 申请号
     */
    private String applicationNo;
    /**
     * 公开号
     */
    private String publicationNo;
    /**
     * 主分类号
     */
    private String mainClassificationNo;
    /**
     * 专利类型
     */
    private String patentType;
    /**
     * 申请日期
     */
    private String applicationDate;
    /**
     * 公开日期
     */
    private String publicationDate;
    /**
     * 分类号
     */
    private String classificationNo;
    /**
     * 申请人地址
     */
    private String applicantAddress;

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

    public String getAbstractCh() {
        return abstractCh;
    }

    public void setAbstractCh(String abstractCh) {
        this.abstractCh = abstractCh;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getPublicationNo() {
        return publicationNo;
    }

    public void setPublicationNo(String publicationNo) {
        this.publicationNo = publicationNo;
    }

    public String getMainClassificationNo() {
        return mainClassificationNo;
    }

    public void setMainClassificationNo(String mainClassificationNo) {
        this.mainClassificationNo = mainClassificationNo;
    }

    public String getPatentType() {
        return patentType;
    }

    public void setPatentType(String patentType) {
        this.patentType = patentType;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getClassificationNo() {
        return classificationNo;
    }

    public void setClassificationNo(String classificationNo) {
        this.classificationNo = classificationNo;
    }

    public String getApplicantAddress() {
        return applicantAddress;
    }

    public void setApplicantAddress(String applicantAddress) {
        this.applicantAddress = applicantAddress;
    }
}
