package com.huawei.agilete.northinterface.bean;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.agilete.base.common.StringEncrypt;

public class OTRegister {


    private String userId = "";
    private String userName = "";
    private String userPasswd = "";
    private String registTime = "";
    private String desc = "";

    //密码使用SHA256加密
    @SuppressWarnings("unchecked")
    public OTRegister(String content){
        try {
            StringBuffer result = new StringBuffer();
            result.append("<user>");
            result.append(content);
            result.append("</user>");
            Document doc = DocumentHelper.parseText(result.toString());
            Element el = doc.getRootElement();
        
            
            
            for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    if(null != domain.elementText("userId") && !"".equals(domain.elementText("userId"))){
                        setUserId(domain.elementText("userId"));
                    }else{
                        setUserId(System.currentTimeMillis()+"");
                    }
                    if(null != domain.elementText("userName") && !"".equals(domain.elementText("userName"))){
                        setUserName(domain.elementText("userName"));
                    }
                    if(null != domain.elementText("userPasswd") && !"".equals(domain.elementText("userPasswd"))){
                        //密码使用SHA256加密
                        setUserPasswd(StringEncrypt.encrypt(domain.elementText("userPasswd")));
                    }
                    if(null != domain.elementText("registTime") && !"".equals(domain.elementText("registTime"))){
                        setRegistTime(domain.elementText("registTime"));
                    }
                    if(null != domain.elementText("desc") && !"".equals(domain.elementText("desc"))){
                        setDesc(domain.elementText("desc"));
                    }
        }
        /*    for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                Element domain = i.next();
                if(null != domain.getQName(userId) && !"".equals(domain.getQName("userId"))){
                    setUserId(domain.getText());
                }
                if(null != domain.getQName("userName") && !"".equals(domain.getQName("userName"))){
                    setUserName(domain.getText());
                }
                if(null != domain.getQName("userPasswd") && !"".equals(domain.getQName("userPasswd"))){
                    //密码使用SHA256加密
                    setUserPasswd(StringEncrypt.Encrypt(domain.getText(),""));
                }
                if(null != domain.getQName("registTime") && !"".equals(domain.getQName("registTime"))){
                    setRegistTime(domain.getText());
                }
            }*/
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
    }

    public String getRegistInfo(){
        StringBuffer result = new StringBuffer();
        result.append("<user>");
        result.append("<userId>").append(getUserId()).append("</userId>");
        result.append("<userName>").append(getUserName()).append("</userName>");
        result.append("<userPasswd>").append(getUserPasswd()).append("</userPasswd>");
        result.append("<registTime>").append(getRegistTime()).append("</registTime>");
        result.append("<desc>").append(getDesc()).append("</desc>");
        result.append("</user>");
        return result.toString();
    }

    
    
    
    
    //
    public static User parseContentToUser(String content){
        if(null==content&&"".equals(content)){
            return null;
        }
        User user = new User();
        StringBuffer result = new StringBuffer();
        result.append("<user>");
        result.append(content);
        result.append("</user>");
        try {
            Document doc = DocumentHelper.parseText(result.toString());
            Element el = doc.getRootElement();
            for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                Element domain = i.next();
                if(null != domain.elementText("userId") && !"".equals(domain.elementText("userId"))){
                    user.setUserId(domain.elementText("userId"));
                }
                if(null != domain.elementText("userName") && !"".equals(domain.elementText("userName"))){
                    user.setUserName(domain.elementText("userName"));
                }
                if(null != domain.elementText("userPasswd") && !"".equals(domain.elementText("userPasswd"))){
                    user.setUserPasswd(domain.elementText("userPasswd"));
                }
                if(null != domain.elementText("registTime") && !"".equals(domain.elementText("registTime"))){
                    user.setRegistTime(domain.elementText("registTime"));
                }
                if(null != domain.elementText("clientBs") && !"".equals(domain.elementText("clientBs"))){
                    user.setClientBs(domain.elementText("clientBs"));
                }
                if(null != domain.elementText("verifyCode") && !"".equals(domain.elementText("verifyCode"))){
                    user.setVerifyCode(domain.elementText("verifyCode"));
                }
                if(null != domain.elementText("desc") && !"".equals(domain.elementText("desc"))){
                    user.setDesc(domain.elementText("desc"));
                }
                if(null != domain.elementText("newPassWd") && !"".equals(domain.elementText("newPassWd"))){
                    user.setNewPassWd(domain.elementText("newPassWd"));
                }
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return user;
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

    
}
