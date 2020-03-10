package com.huahua.chaoxing.bean;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/10.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class SignBean {
    private String signClass;
    private String signName;
    private String signTime;
    private String signState;

    public String getSignClass() {
        return signClass;
    }

    public void setSignClass(String signClass) {
        this.signClass = signClass;
    }

    @Override
    public String toString() {
        return "signBean{" +
                "signClass='" + signClass + '\'' +
                ", signName='" + signName + '\'' +
                ", signTime='" + signTime + '\'' +
                ", signState='" + signState + '\'' +
                '}';
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getSignState() {
        return signState;
    }

    public void setSignState(String signState) {
        this.signState = signState;
    }
}
