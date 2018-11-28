package com.tencent.qq.test;

/**
 * Created by bona.xiao on 2018/11/28.
 * 备注：
 */
public class MessageBean {


    /**
     * code : 200
     * message : 成功
     * content : {"code":200,"message":"成功","content":{"id":3,"sendMessage":"http://testapi.myshengqian
     * .com/niuniu-app-api/h5/activity?activityId=96","pictureUrl":"","createTime":1543330106000,
     * "updateTime":1543330108000,"del":0}}
     */

    private String code;
    private String message;
    private ContentBean content;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }


}
