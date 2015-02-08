package com.huawei.agilete.base.servlet;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyRollback extends OpsInterface {
     public String url = "";
     public String fullUrl = "";
     public String param="commitId";
     
    public MyRollback(IOpsRestCaller restcall){
        super(restcall);
//        super.url = url;
//        super.fullUrl = fullUrl;
        super.param = param;
    }
    
    public RetRpc getPoints(String[] content){
        super.url = "/cfg/checkPointInfos/checkPointInfo?commitId";
        return super.get(content);
    }
    
    public RetRpc rollbackByCommitId(String[] content){
        if(null != content){
            super.url = "/cfg/rollbackByCommitId";
            super.body="<cfg><rollbackByCommitId><commitId>"+content[0]+"</commitId></rollbackByCommitId></cfg>";
            return super.create(null);
        }else{
            return new RetRpc(500);
        }
    }
    public String getXMLToLastCommitId(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element checkPointInfos = el.element("checkPointInfos");
            for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                Element domain = i.next();
                String commitId = domain.element("checkPointInfo").elementText("commitId");
                return commitId;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * @param args
     */
    /*
    public static void main(String[] args) {
        MyRollback myRollback = new MyRollback(new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer("4"), "48"));
//        myRollback.getXMLToLastCommitId(myRollback.getPoints(null).getContent());
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
//        String aa = "<sessName>backupSpring1</sessName><createType>SESS_STATIC</createType><localDiscr>1302</localDiscr><remoteDiscr>1402</remoteDiscr><linkType>TE_TUNNEL</linkType><tunnelName>Tunnel1</tunnelName><detectMulti>3</detectMulti><minTxInt/><minRxInt/>";
//        String deleteaa = "backupSpring1";
//        
//        e.body = aa;
        //System.out.println(myRollback.getXMLToLastCommitId(myRollback.getPoints(null).getContent()));
    }*/

}
