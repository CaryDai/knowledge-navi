package com.dqj.knowledgenavi.dataobject;

import java.util.Arrays;

/**
 * @Author dqj
 * @Date 2021/1/4
 * @Version 1.0
 * @Description 用户编辑的节点对象
 */
public class EditNodeDO {
    private String classCode;
    private String parentCode;
    private String title;
    private EditNodeDO[] children;

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

    public EditNodeDO[] getChildren() {
        return children;
    }

    public void setChildren(EditNodeDO[] children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "EditNodeDO{" +
                "classCode='" + classCode + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", title='" + title + '\'' +
                ", children=" + Arrays.toString(children) +
                '}';
    }
}
