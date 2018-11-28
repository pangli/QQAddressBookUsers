package com.tencent.qq.test;

/**
 * Created by pangli on 2018/11/28 17:48
 * 备注：
 */
public class Content {
    /**
     * id : 3
     * sendMessage : http://testapi.myshengqian.com/niuniu-app-api/h5/activity?activityId=96
     * pictureUrl :
     * createTime : 1543330106000
     * updateTime : 1543330108000
     * del : 0
     */

    private int id;
    private String sendMessage;
    private String pictureUrl;
    private long createTime;
    private long updateTime;
    private int del;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }
}
