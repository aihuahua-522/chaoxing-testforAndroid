package com.huahua.chaoxing.bean;

import java.io.Serializable;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/18.
 * 作用：
 * PS: Not easy to write code, please indicate.
 */

public class PicBean implements Serializable {

    /**
     * result : true
     * msg : success
     * puid : 82674419
     * data : {"filetype":"","extinfo":"","thumbnail":"http://pan-yz.chaoxing.com/thumbnail/origin/5e0e687e0c20bd5158271eb32d869f53?type=img","creator":82674419,"modifyDate":1584544632624,"resTypeValue":3,"sort":0,"suffix":"jpeg","resid":447175861323890688,"topsort":0,"restype":"RES_TYPE_NORMAL","duration":0,"pantype":"USER_PAN","puid":82674419,"size":200448,"uploadDate":1584544632624,"filepath":"","crc":"65a6eae1bd93449ebb78cebbfd4c103c","isfile":true,"name":"IMG_46BA723DC19761638AA62FF6B593DB62.jpeg","residstr":"447175861323890688","objectId":"5e0e687e0c20bd5158271eb32d869f53"}
     * crc : 65a6eae1bd93449ebb78cebbfd4c103c
     * resid : 447175861323890688
     * objectId : 5e0e687e0c20bd5158271eb32d869f53
     */

    private boolean result;
    private String msg;
    private int puid;
    private DataBean data;
    private String crc;
    private long resid;
    private String objectId;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPuid() {
        return puid;
    }

    public void setPuid(int puid) {
        this.puid = puid;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public long getResid() {
        return resid;
    }

    public void setResid(long resid) {
        this.resid = resid;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public static class DataBean implements Serializable {
        /**
         * filetype :
         * extinfo :
         * thumbnail : http://pan-yz.chaoxing.com/thumbnail/origin/5e0e687e0c20bd5158271eb32d869f53?type=img
         * creator : 82674419
         * modifyDate : 1584544632624
         * resTypeValue : 3
         * sort : 0
         * suffix : jpeg
         * resid : 447175861323890688
         * topsort : 0
         * restype : RES_TYPE_NORMAL
         * duration : 0
         * pantype : USER_PAN
         * puid : 82674419
         * size : 200448
         * uploadDate : 1584544632624
         * filepath :
         * crc : 65a6eae1bd93449ebb78cebbfd4c103c
         * isfile : true
         * name : IMG_46BA723DC19761638AA62FF6B593DB62.jpeg
         * residstr : 447175861323890688
         * objectId : 5e0e687e0c20bd5158271eb32d869f53
         */

        private String filetype;
        private String extinfo;
        private String thumbnail;
        private int creator;
        private long modifyDate;
        private int resTypeValue;
        private int sort;
        private String suffix;
        private long resid;
        private int topsort;
        private String restype;
        private int duration;
        private String pantype;
        private int puid;
        private int size;
        private long uploadDate;
        private String filepath;
        private String crc;
        private boolean isfile;
        private String name;
        private String residstr;
        private String objectId;

        public String getFiletype() {
            return filetype;
        }

        public void setFiletype(String filetype) {
            this.filetype = filetype;
        }

        public String getExtinfo() {
            return extinfo;
        }

        public void setExtinfo(String extinfo) {
            this.extinfo = extinfo;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public int getCreator() {
            return creator;
        }

        public void setCreator(int creator) {
            this.creator = creator;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }

        public int getResTypeValue() {
            return resTypeValue;
        }

        public void setResTypeValue(int resTypeValue) {
            this.resTypeValue = resTypeValue;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public long getResid() {
            return resid;
        }

        public void setResid(long resid) {
            this.resid = resid;
        }

        public int getTopsort() {
            return topsort;
        }

        public void setTopsort(int topsort) {
            this.topsort = topsort;
        }

        public String getRestype() {
            return restype;
        }

        public void setRestype(String restype) {
            this.restype = restype;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getPantype() {
            return pantype;
        }

        public void setPantype(String pantype) {
            this.pantype = pantype;
        }

        public int getPuid() {
            return puid;
        }

        public void setPuid(int puid) {
            this.puid = puid;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getUploadDate() {
            return uploadDate;
        }

        public void setUploadDate(long uploadDate) {
            this.uploadDate = uploadDate;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public String getCrc() {
            return crc;
        }

        public void setCrc(String crc) {
            this.crc = crc;
        }

        public boolean isIsfile() {
            return isfile;
        }

        public void setIsfile(boolean isfile) {
            this.isfile = isfile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getResidstr() {
            return residstr;
        }

        public void setResidstr(String residstr) {
            this.residstr = residstr;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }
    }
}
