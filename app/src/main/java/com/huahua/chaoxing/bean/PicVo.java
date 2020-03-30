package com.huahua.chaoxing.bean;

import java.math.BigInteger;

/**
 * pic
 *
 * @author
 */
public class PicVo {

    private Integer pid;
    private BigInteger tel;
    private String objectid;


    public PicVo(Integer pid, String objectid, BigInteger tel) {
        this.pid = pid;
        this.objectid = objectid;
        this.tel = tel;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public BigInteger getTel() {
        return tel;
    }

    public void setTel(BigInteger tel) {
        this.tel = tel;
    }

}