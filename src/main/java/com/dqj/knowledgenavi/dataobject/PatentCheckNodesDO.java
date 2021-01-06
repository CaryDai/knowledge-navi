package com.dqj.knowledgenavi.dataobject;

import java.util.Arrays;

/**
 * @Author dqj
 * @Date 2021/1/6
 * @Version 1.0
 * @Description
 */
public class PatentCheckNodesDO {
    private PatentBriefDO patentBriefDO;
    private NodeDO[] nodeDOS;

    public PatentBriefDO getPatentBriefDO() {
        return patentBriefDO;
    }

    public void setPatentBriefDO(PatentBriefDO patentBriefDO) {
        this.patentBriefDO = patentBriefDO;
    }

    public NodeDO[] getNodeDOS() {
        return nodeDOS;
    }

    public void setNodeDOS(NodeDO[] nodeDOS) {
        this.nodeDOS = nodeDOS;
    }

    @Override
    public String toString() {
        return "PatentCheckNodesDO{" +
                "patentBriefDO=" + patentBriefDO +
                ", nodeDOS=" + Arrays.toString(nodeDOS) +
                '}';
    }
}
