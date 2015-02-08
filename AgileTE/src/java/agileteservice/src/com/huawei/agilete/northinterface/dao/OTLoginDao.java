package com.huawei.agilete.northinterface.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.common.StringEncrypt;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.northinterface.bean.LoginInfo;
import com.huawei.agilete.northinterface.bean.OTRegister;
import com.huawei.agilete.northinterface.bean.User;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTLoginDao {

    private static OTLoginDao single = null;
    private DBAction db = new DBAction();

    private OTLoginDao() {

    }

    public synchronized static OTLoginDao getInstance() {
        if (single == null) {
            single = new OTLoginDao();
        }
        return single;
    }

    
    
    // 登陆方法

    public RetRpc login(String content) {
        RetRpc result = new RetRpc();
        User user = OTRegister.parseContentToUser(content);
        String userName = user.getUserName();
        String pw = user.getUserPasswd();
        long spareTime = 30*1000;
        int number = 0;
        long time = 0;
        LoginInfo info = this.getInfo(userName);
        number = info.getNumber();
        time = info.getTime();
        //System.out.println("登陆次数："+number);
        
        //登陆次数小于三
        if(number>=3&&number<5){
            String clientBs = user.getClientBs();
            String verifyCode = user.getVerifyCode();
            //第五次登陆加上锁定时间
            if(number==4){
                LoginInfo info2 = this.getInfo(userName);
                MyData.getClientLoginInfo().put(userName, info2.getNumber()+":"+System.currentTimeMillis());
            }
            
            boolean flag = isVerifyCode(clientBs,verifyCode);
            if(flag==false){
                LoginInfo info2 = this.getInfo(userName);
                result.setStatusCode(200);
                result.setContent("<login><result>verifyCodeFail</result><number>"+info2.getNumber()+"</number><time>"+info2.getTime()+"</time></login>");
                return result;
            }
        }else if(number>=5){//登陆次数大于5
            LoginInfo info2 = this.getInfo(userName);
            long time2 = System.currentTimeMillis();
            long time3 =time2-info2.getTime();
            long time4 =spareTime-time3;
            //锁定时间内不能登录
            if(time3<=spareTime){
                result.setStatusCode(200);
                result.setContent("<login><result>fail</result><number>"+info2.getNumber()+"</number><time>"+time4+"</time></login>");
                return result;
            }else{
                MyData.getClientLoginInfo().put(userName,"0:0");
            }
        }
        String key = isAuth(userName, pw);
        if (null!= key&&!"".equals(key)) {
            MyData.getClientLoginInfo().put(userName,"0:0");
            LoginInfo info2 = this.getInfo(userName);
            result.setStatusCode(200);
            result.setContent("<login><result>success</result><number>"+info2.getNumber()+"</number><time>"+info2.getTime()+"</time></login>");
        } else {
            LoginInfo info2 = this.getInfo(userName);
            int number2 = info2.getNumber()+1;
            MyData.getClientLoginInfo().put(userName, number2+":"+info2.getTime());
            result.setStatusCode(200);
            if(info2.getNumber()==4){
                result.setContent("<login><result>fail</result><number>"+number2+"</number><time>"+spareTime+"</time></login>");
            }else{
                result.setContent("<login><result>fail</result><number>"+number2+"</number><time>"+info2.getTime()+"</time></login>");
            }
        }
        return result;
    }


    // 验证用户名密码是否存在或者正确,返回key值（失败原因不做详细区分）

    public String isAuth(String userName, String passWd) {
        String key = null;
        List<String[]> list = db.getAll("user", "user");
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String content = list.get(i)[1];
                User user = OTRegister.parseContentToUser(content);
                if (null != user) {
                    if (userName.equals(user.getUserName())
                            && StringEncrypt.verify(passWd, user.getUserPasswd())) {
                        key = list.get(i)[0];
                        break;
                    }
                }
            }
        }
        return key;
    }

    
    // 验证验证码是否正确

    public boolean isVerifyCode(String clientBs,String verifyCode) {
        boolean flag = false;
        if(null!=clientBs&&null!=verifyCode){
        Map<String, String> map = MyData.getClientVerifyCode();
        String code = map.get(clientBs);
        if(code!=null){
            if(verifyCode.equalsIgnoreCase(code)){
                flag = true;
                return flag;
            }
        }
        MyData.getClientVerifyCode().remove(clientBs);
    }
        return flag;
    }
    
    
    
    //修改密码
    
    public RetRpc modifyPw(String content){
        RetRpc ret = new RetRpc();
        boolean flag = false;
        User user = OTRegister.parseContentToUser(content);
        String newPw =StringEncrypt.encrypt(user.getNewPassWd()) ;
        String key = isAuth(user.getUserName(),user.getUserPasswd());
        if (null== key||"".equals(key)) {
            ret.setStatusCode(200);
            ret.setContent("<modifyPw>authFail</modifyPw>");
            return ret;
        }
        String value = db.get("user", "user", key);
        User user2 = OTRegister.parseContentToUser(value);
        if(null!=user2){
            StringBuffer result = new StringBuffer();
            result.append("<user>");
            result.append("<userId>").append(user2.getUserId()).append("</userId>");
            result.append("<userName>").append(user2.getUserName()).append("</userName>");
            result.append("<userPasswd>").append(newPw).append("</userPasswd>");
            result.append("<registTime>").append(user2.getRegistTime()).append("</registTime>");
            result.append("<desc>").append(user2.getDesc()).append("</desc>");
            result.append("</user>");
            flag = db.insert("user", "user", key,result.toString());
        }
        if(flag){
            ret.setStatusCode(200);
            ret.setContent("<modifyPw>success</modifyPw>");
        }else{
            ret.setStatusCode(200);
            ret.setContent("<modifyPw>fail</modifyPw>");
        }
        return ret;
    }

    
    
    //获取用户登陆次数 和锁定时间
    public LoginInfo getInfo(String userName){
        LoginInfo info2 = new LoginInfo();
        HashMap<String, String> map = MyData.getClientLoginInfo();
        if(null!=map){
            //判断是否是第一次登陆
            if(null==map.get(userName)||"".equals(map.get(userName))){
                MyData.getClientLoginInfo().put(userName, "0:0");
            }
        }
        String s = MyData.getClientLoginInfo().get(userName);
        String[] num =s.split(":"); 
        int number1 = Integer.parseInt(num[0]);
        info2.setNumber(number1);
        long time1 = Long.parseLong(num[1]);
        info2.setTime(time1);
        return info2;
    }
    
    public static void main(String[] args) {
        String content = "<user><userId>00001</userId><userName>wzw123</userName><userPasswd>123456</userPasswd><newPassWd>123456</newPassWd></user>";
        
        OTLoginDao dao = new OTLoginDao();
        RetRpc result = dao.login(content);
        ////System.out.println(dao.modifyPw(content));
        //System.out.println("登陆:" + result.getStatusCode() + "%%%%%"+result.getContent());
    }

}
