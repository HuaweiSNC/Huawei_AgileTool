package com.huawei.agilete.northinterface.dao;

import java.util.ArrayList;
import java.util.List;

import com.huawei.agilete.base.servlet.MyBfd;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTBfd;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTBfdDAO implements IOTDao{

    private static OTBfdDAO single = null;
    private OpsRestCaller client = null;
    private OTBfdDAO(){

    }
    public synchronized  static OTBfdDAO getInstance() {
        if (single == null) {  
            single = new OTBfdDAO();
        }  
        return single;
    }


    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.GET)){
            result = getByTunnelName(apiPath);
        }else if(restType.equals(MyData.RestType.DELETE)){
            result = del(apiPath);
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else{

        }



        return result;
    }

    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        //        MyRollback myRollback = new MyRollback(client);
        //        RetRpc result = myRollback.getPoints(null);
        //        String rollBackXML = result.getContent();
        //        result = new RetRpc();
        //        if(result.getStatusCode()!=200){
        //            result.setContent("Back is error!");
        //            return result;
        //        }
        OTBfd oTBfd_ = new OTBfd(content);
        for(OTBfd oTBfd:oTBfd_.getBfds()){
            String bodyPath = oTBfd.getOpsMessage();
            MyBfd myBfd  =new MyBfd(client);
            myBfd.body=bodyPath;
            result = myBfd.create(null);
            if(result.getStatusCode()!=200){
                //                String commitId = myRollback.getXMLToLastCommitId(rollBackXML);
                //                String[] commitIds = {commitId};
                //                myRollback.rollbackByCommitId(commitIds);
                return result;
            }
        }
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        OTBfd oTBfd_ = new OTBfd(content);
        for(OTBfd oTBfd:oTBfd_.getBfds()){
            if(null == oTBfd.getName() || "".equals(oTBfd.getName())){
                if(null != oTBfd.getTunnelName() || !"".equals(oTBfd.getTunnelName())){
                    //按tunnelname下发开关
                    result = editByTunnelName(oTBfd);
                }else{
                    result.setStatusCode(500);
                    result.setContent("name can not null");
                }
            }else{
                if(null != oTBfd.getTunnelName() && !"".equals(oTBfd.getTunnelName())){
                    result = del(oTBfd.getName());
                    if(result.getStatusCode()==200){
                        result = add(content);
                    }
                }else{
                    //按name下发开关
                    String bodyPath = oTBfd.getOpsMessage();
                    MyBfd myBfd  =new MyBfd(client);
                    myBfd.body=bodyPath;
                    result = myBfd.create(null);
                }
            }
            if(result.getStatusCode()!=200){
                return result;
            }
        }
        return result;
    }

    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        if(null != apiPath){
            MyBfd myBfd  =new MyBfd(client);
            result = myBfd.delete(new String[]{apiPath});
        }else{
            result.setStatusCode(500);
            result.setContent("ApiPath is null!");
        }
        return result;
    }
    public RetRpc get(String apiPath){
        String result = "";
        MyBfd myBfd  =new MyBfd(client);
        String[] name = null;
        if(!"".equals(apiPath)){
            name = new String[]{apiPath};
        }
        RetRpc opsresult = myBfd.get(name);
        if(opsresult.getStatusCode()==200){
            OTBfd oTBfd = new OTBfd();
            List<OTBfd> list = oTBfd.parseOpsToUi(opsresult.getContent());
            result = oTBfd.getUiMessage(list);
            opsresult.setContent(result);
        }
        return opsresult;
    }
    public RetRpc getByTunnelName(String apiPath){
        RetRpc opsresult = new RetRpc();
        List<OTBfd> list = getListByTunnelName(apiPath);
        if(null != list && list.size() != 0){
            OTBfd oTBfd = new OTBfd();
            String result = oTBfd.getUiMessage(list);
            opsresult.setContent(result);
        }else{
            opsresult.setContent("");
        }
        return opsresult;
    }

    public List<OTBfd> getListByTunnelName(String apiPath){
        List<OTBfd> list = null;
        MyBfd myBfd  =new MyBfd(client);
        RetRpc opsresult = myBfd.get(null);
        if(opsresult.getStatusCode()==200){
            OTBfd oTBfd = new OTBfd();
            list = oTBfd.parseOpsToUi(opsresult.getContent());
            if(!"".equals(apiPath)){
                List<OTBfd> flag = new ArrayList<OTBfd>();
                for(int i=0;i<list.size();i++){
                    OTBfd bfd = list.get(i);
                    if(apiPath.equals(bfd.getTunnelName())){
                        flag.add(bfd);
                    }
                }
                return flag;
            }
        }
        return list;
    }

    public RetRpc editByTunnelName(OTBfd oTBfd0){
        RetRpc result = new RetRpc();
        OTBfd oTBfd = oTBfd0;
        if(null == oTBfd.getTunnelName() || "".equals(oTBfd.getTunnelName()) || null == oTBfd.getShutdown() || "".equals(oTBfd.getShutdown())){
            result.setStatusCode(500);
            result.setContent("tunnelname or admindown can not be null");
            return result;
        }

        List<OTBfd> list = getListByTunnelName(oTBfd.getTunnelName());
        if(null != list){
            MyBfd myBfd  =new MyBfd(client);
            for(int i=0;i<list.size();i++){
                OTBfd bfd = list.get(i);
                bfd.setTunnelName("");
                bfd.setShutdown(oTBfd.getShutdown());
                myBfd.body=bfd.getOpsMessage();
                result = myBfd.modify(null);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        //String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
        ////System.out.println(OTBfdDAO.getInstance().getByTunnelName("1", "5", ""));
        ////System.out.println(OTBfdDAO.getInstance().add("1", "4", "<bfds><bfd><name>Flow13453452</name><discLocal>101</discLocal><discRemote>101</discRemote><tunnelName>Tunnel24</tunnelName><minTxInterval>100</minTxInterval><minRxInterval>100</minRxInterval><wtrTimerInt>100</wtrTimerInt><teBackup>true</teBackup></bfd></bfds>"));
        ////System.out.println(OTBfdDAO.getInstance().del("1", "5", "Flow1346"));
        ////System.out.println(OTBfdDAO.getInstance().edit("1", "4", "<bfds><bfd><name>main</name><adminDown>true</adminDown></bfd></bfds>"));


    }
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }


}
