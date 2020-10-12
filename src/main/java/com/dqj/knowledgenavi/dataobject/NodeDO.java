package com.dqj.knowledgenavi.dataobject;

/**
 * @Author dqj
 * @Date 2020/6/30
 * @Version 1.0
 * @Description
 */
public class NodeDO {
    String classCode;
    String parentCode;
    String title;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NodeDO(String classCode, String parentCode, String title) {
        this.classCode = classCode;
        this.parentCode = parentCode;
        this.title = title;
    }
}
