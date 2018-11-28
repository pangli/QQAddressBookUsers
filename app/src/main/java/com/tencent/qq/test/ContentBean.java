package com.tencent.qq.test;

/**
 * Created by bona.xiao on 2018/11/28.
 * 备注：
 */
public class ContentBean {
    /**
     * code : 200
     * message : 成功
     * content : {"id":3,"sendMessage":"http://testapi.myshengqian.com/niuniu-app-api/h5/activity?activityId=96",
     * "pictureUrl":"","createTime":1543330106000,"updateTime":1543330108000,"del":0}
     */

    private int code;
    private String message;
    private Content content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

}
