package com.huawei.agilete.northinterface.bean;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.cql3.Operation.SetValue;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.huawei.networkos.templet.VMTempletManager;

public class OTFlux {
	
	
    private String time = "";
    private String rttAverage = "";
    private String loss = "";
    
    private String receibveByte = "";
    private String sendByte = "";

    public OTFlux(){

    }

    public OTFlux(String content){
    	if(content==null||content.trim().equals("")){
    		setTime("0");
    		setRttAverage("0");
    		setReceibveByte("0");
    		setSendByte("0");
    	}else{
	        try {
	            Document doc = DocumentHelper.parseText(content);
	            Node schedule = doc.selectSingleNode("/nqa/data/schedule/text()");
	            Node receibveByte = doc.selectSingleNode("/nqa/data/receibveByte/text()");
	            Node sendByte = doc.selectSingleNode("/nqa/data/sendByte/text()");
	            Node value1 = doc.selectSingleNode("/nqa/data/value1/text()");
	            setRttAverage(value1.getText());
        		setTime(schedule.getText());
        		setReceibveByte(receibveByte.getText());
        		setSendByte(sendByte.getText());
	        }catch (DocumentException e) {
	            e.printStackTrace();
	        }
    	}
    }


    public String getUiMessage(List<OTFlux> list){
        String result = "";
        if(null == list || list.size() == 0){
            return result;
        }
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_flux", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = new StringWriter();
        try{
            write = templet.process("UiTempleFluxNow.tpl", mapContext, templetPath);
        }catch (Exception e) {
            e.printStackTrace();
        }
        result = write.toString();
        return result;
    }


    public String getRttAverage() {
        return rttAverage;
    }
    public void setRttAverage(String rttAverage) {
        this.rttAverage = rttAverage;
    }
    public String getLoss() {
        return loss;
    }
    public void setLoss(String loss) {
        this.loss = loss;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

	public String getReceibveByte() {
		return receibveByte;
	}

	public void setReceibveByte(String receibveByte) {
		this.receibveByte = receibveByte;
	}

	public String getSendByte() {
		return sendByte;
	}

	public void setSendByte(String sendByte) {
		this.sendByte = sendByte;
	}
//	public static void main(String[] args) {
//		String a = "<nqa>  <data>    <schedule>1402913170556</schedule>    <value1>24678132</value1>    <value2/>    <receibveByte>2760320</receibveByte>    <sendByte>1722131390298</sendByte>  </data></nqa>";
//		OTFlux otFlux = new OTFlux(a);
//		System.out.println(otFlux.getReceibveByte());
//	}

}
