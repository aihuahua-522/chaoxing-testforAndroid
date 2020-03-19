package com.huahua.chaoxing.bean;

import java.io.Serializable;

public class ClassBean implements Serializable {
    private String url;
    private String courseId;
    private String classId;
    /**
     * 老师
     */
    private String teacher;
    /**
     * 课程名
     */
    private String className;
    /**
     * 班级名
     */
    private String classmate;

    private String workUrl;

    public String getWorkUrl() {
        return workUrl;
    }

    public void setWorkUrl(String workUrl) {
        this.workUrl = workUrl;
    }

    @Override
    public String toString() {
        return "classBean{" +
                "url='" + url + '\'' +
                ", courseId='" + courseId + '\'' +
                ", classId='" + classId + '\'' +
                ", teacher='" + teacher + '\'' +
                ", className='" + className + '\'' +
                ", classmate='" + classmate + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassmate() {
        return classmate;
    }

    public void setClassmate(String classmate) {
        this.classmate = classmate;
    }
}
