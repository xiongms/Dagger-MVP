package com.xiongms.libcore.bean;

/**
 * 
 * @author xiongms
 * @time 2018-08-24 9:23
 */
public class PushMessageBean {
    /**
     * 1：普通通知 2：语音播报
     */
    private int type;
    /**
     * 消息编号 或 交易编号
     */
    private String code;
    private String title;
    private String content;
    private String router;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }
}
