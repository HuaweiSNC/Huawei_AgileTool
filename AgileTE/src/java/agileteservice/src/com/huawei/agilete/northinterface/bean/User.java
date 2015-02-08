package com.huawei.agilete.northinterface.bean;

public class User {
    private String userId;
    private String userName; 
    private String userPasswd;
    private String registTime;
    private String desc;
    
    private String clientBs;
    private String verifyCode;
    private String newPassWd;
    
    public String getVerifyCode() {
        return verifyCode;
    }
    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPasswd() {
        return userPasswd;
    }
    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }
    public String getRegistTime() {
        return registTime;
    }
    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getClientBs() {
        return clientBs;
    }
    public void setClientBs(String clientBs) {
        this.clientBs = clientBs;
    }
    public String getNewPassWd() {
        return newPassWd;
    }
    public void setNewPassWd(String newPassWd) {
        this.newPassWd = newPassWd;
    }
    
    
}
