package com.huahua.chaoxing.bean;

import java.io.Serializable;
import java.math.BigInteger;

public class CourseBean implements Serializable {

    private int cid;
    private BigInteger tel;
    /**
     * 任务地址
     */
    private String signUrl;
    /**
     * 课程id
     */
    private String courseId;
    private String className;
    private String courseName;
    /**
     * 班级id
     */
    private String classId;
    /**
     * 老师
     */
    private String teacher;
    /**
     * 作业地址
     */
    private String workUrl;

    public CourseBean(BigInteger tel, String signUrl, String courseId, String className, String courseName, String classId, String teacher, String workUrl) {
        this.tel = tel;
        this.signUrl = signUrl;
        this.courseId = courseId;
        this.className = className;
        this.courseName = courseName;
        this.classId = classId;
        this.teacher = teacher;
        this.workUrl = workUrl;
    }

    @Override
    public String toString() {
        return "CourseBean{" +
                "cid=" + cid +
                ", tel=" + tel +
                ", signUrl='" + signUrl + '\'' +
                ", courseId='" + courseId + '\'' +
                ", className='" + className + '\'' +
                ", courseName='" + courseName + '\'' +
                ", classId='" + classId + '\'' +
                ", teacher='" + teacher + '\'' +
                ", workUrl='" + workUrl + '\'' +
                '}';
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public BigInteger getTel() {
        return tel;
    }

    public void setTel(BigInteger tel) {
        this.tel = tel;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSignUrl() {
        return signUrl;
    }

    public void setSignUrl(String signUrl) {
        this.signUrl = signUrl;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getWorkUrl() {
        return workUrl;
    }

    public void setWorkUrl(String workUrl) {
        this.workUrl = workUrl;
    }

}
