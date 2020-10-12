package com.dqj.knowledgenavi.dataobject;

/**
 * @Author dqj
 * @Date 2020/6/24
 * @Version 1.0
 * @Description
 */
public class ClassCodesDO {
    private int Id;
    private String className;
    private String classCode;
    private String parentCode;
    private int itemLevel;

    public ClassCodesDO() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

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

    public int getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(int itemLevel) {
        this.itemLevel = itemLevel;
    }

    @Override
    public String toString() {
        return "ClassCodesDO{" +
                "Id=" + Id +
                ", className='" + className + '\'' +
                ", classCode='" + classCode + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", itemLevel=" + itemLevel +
                '}';
    }
}
