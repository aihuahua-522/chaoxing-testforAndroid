package com.huahua.chaoxing.bean;

public class ScanningBean {

    /**
     * uid : 82659775
     * nickname : 郭梁
     * mes : 已扫描
     * type : 4
     * status : false
     */

    private String uid;
    private String nickname;
    private String mes;
    private String type;
    private boolean status;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
