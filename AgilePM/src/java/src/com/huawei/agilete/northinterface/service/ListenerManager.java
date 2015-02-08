package com.huawei.agilete.northinterface.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.agilete.northinterface.bean.MrtgModel;
import com.huawei.agilete.northinterface.bean.TempletModel;
import com.huawei.agilete.northinterface.templetManager.TempletManager;
import com.huawei.agilete.northinterface.util.ProcessManager;
import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.networkos.templet.VMTempletManager;

public class ListenerManager {
	ReadConfig readConfig = new ReadConfig();
	StringBuffer filePath = readConfig.getPathForClass();
//	StringBuffer filePath = new StringBuffer();
//	
//	public RetRpc create(String xml){
//		RetRpc retRpc = new RetRpc();
//		MrtgModel mrtgModel = new MrtgModel(xml);
//		createCfg(mrtgModel);
////		runMrtg(mrtgModel);
//		return retRpc;
//	}
//	
//	public String createCfg(MrtgModel mrtgModel){
//		StringBuffer filePah=new StringBuffer(readConfig.getPathForClass());
//		StringBuffer filePah_cfg=new StringBuffer();
//		filePah_cfg.append(filePah)
//			.append("/com/huawei/plugins/mrtg/run/bin/")
//			.append(mrtgModel.getId())
//			.append(".cfg");
//		File file_cfg = new File(filePah_cfg.toString());
//		if (!file_cfg.exists()){
//			try {
//				file_cfg.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//				return "Create cfg is wrong!";
//			}
//		}else{
//			
//		}
//		String writeData = getCfgDataOnInput(mrtgModel).toString();
//		try {
//			// 下面把数据写入创建的文件，首先新建文件名为参数创建FileWriter对象
//			FileWriter resultFile = new FileWriter(file_cfg);
//			// 把该对象包装进PrinterWriter对象
//			PrintWriter myNewFile = new PrintWriter(resultFile);
//			// 再通过PrinterWriter对象的println()方法把字符串数据写入新建文件
//			myNewFile.println(writeData);
//			resultFile.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // 关闭文件写入流
//			
//		return "true";
//	}
//	public StringBuffer getCfgDataOnInput(MrtgModel mrtgModel){
//		StringBuffer writeData=new StringBuffer();
//		String myEx = "["+mrtgModel.getId()+"]: ";
//		writeData.append("# this is a mrtg test setup \n")
//		.append("#im gonna try using some fine examples (hopefully) to make this work.\n")
//		.append("# this is gonna be a DNS graph (when if ever im ready).\n")
//		.append("WorkDir: ").append(readConfig.getPathForClass().toString().replace("/WEB-INF/classes", "")).append("/mrtg/html").append("\n")
//		.append("Interval: ").append(mrtgModel.getInterval()).append("\n")
//		.append("Language: ").append(mrtgModel.getLanguage()).append("\n")
//		.append("RunAsDaemon:yes").append("\n")
//		.append("Title").append(myEx).append(mrtgModel.getTitle()).append("\n")
//		.append("PageTop").append(myEx).append(mrtgModel.getPageTop()).append("\n")
//		.append("MaxBytes").append(myEx).append(mrtgModel.getMaxBytes()).append("\n")
//		.append("Options").append(myEx).append(mrtgModel.getOptions()).append("\n")
//		.append("Target").append(myEx).append("`java -cp \"").append(readConfig.getPathForClass()).append("\" com.huawei.plugins.mrtg.run.bin.MrtgPlugins \"")
//			.append(mrtgModel.getUrl()).append("\" \"").append(mrtgModel.getXPath()).append("\"`").append("\n")
//		.append("WithPeak").append(myEx).append("ymwd").append("\n")
//		.append("\n")
//		.append("\n");
//		return writeData;
//	}
	public String runMrtg(String mrtg){
		Process p=null;
		try {
			StringBuffer file=new StringBuffer(readConfig.getPathForClass())
				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\");
			StringBuffer exec=new StringBuffer("perl \"");
			exec.append(file).append("mrtg\" --logging=\"").append(file).append(mrtg).append(".log\" \"").append(file).append(mrtg).append(".cfg\"");
			StringBuffer file_work=new StringBuffer(readConfig.getPathForClass().toString().replace("/WEB-INF/classes", ""))
				.append("/statisticsPage/run");
			File workfile = new File(file_work.toString());
			p = Runtime.getRuntime().exec(exec.toString(),null,workfile);
			ProcessManager.processMap.put(mrtg, p);
//			p.waitFor()
//			p.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	/**
//	 * 创建监听 01
//	 * @param xml
//	 * @return
//	 */
//	public String createMrtg_(String xml){
//		MrtgModel mrtgModel = new MrtgModel(xml);
//		Properties mrtgProperties = new Properties();
//		StringBuffer propertiesFile=new StringBuffer(readConfig.getPathForClass())
//			.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.properties");
//		try {
//			mrtgProperties.load(new FileInputStream(propertiesFile.toString()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Enumeration<?> keyList = mrtgProperties.propertyNames();
//		Integer cfgSize = mrtgProperties.size();
//		String[] keyListString = new String[cfgSize];
//		Set<String> keyIdSet = new HashSet<String>();
//		Integer num = 0;
//		while (keyList.hasMoreElements()) {
//			keyListString[num] = keyList.nextElement().toString();
//			if(keyListString[num].contains("software")){
//				String keyId = keyListString[num].replace("software[", "").replace("]", "");
//				keyIdSet.add(keyId);
//			}
//			num++;
//		}
//		if(keyIdSet.contains(mrtgModel.getId())){
//			return "This is already!";
//		}
//		
//		StringBuffer writeString = new StringBuffer();
//		writeString.append("software["+mrtgModel.getId()+"]: \n")
//			.append("xpathObject["+mrtgModel.getId()+"]: \n")
//			.append("xpathTarget["+mrtgModel.getId()+"]: \n")
//			.append("url["+mrtgModel.getId()+"]: \n");
//		
//		FileWriter fileWriter = null;
//		try {
//			fileWriter=new FileWriter(propertiesFile.toString(),true);
//			fileWriter.append("\n\n");
//			fileWriter.append(writeString.toString());
//			fileWriter.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			try{
//				fileWriter.close();
//			}catch(Exception e1){
//				e1.printStackTrace();
//				return "Error!";
//			}
//			return "Error!";
//		}
//		
////		String[] listenerIds = keyIdSet.toArray(new String[0]);
//		return "OK";
//	}
//	
//	/**
//	 * 修改配置 02
//	 * @param xml
//	 * @return
//	 */
//	public String putMrtg_(String xml){
//		MrtgModel mrtgModel = new MrtgModel(xml);
//		try{
//			Properties mrtgProperties = new Properties();
//			StringBuffer propertiesFile=new StringBuffer(readConfig.getPathForClass())
//				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.properties");
//			try {
//				mrtgProperties.load(new FileInputStream(propertiesFile.toString()));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Enumeration<?> keyList = mrtgProperties.propertyNames();
//			Integer cfgSize = mrtgProperties.size();
//			String[] keyListString = new String[cfgSize];
//			Set<String> keyIdSet = new HashSet<String>();
//			Integer num = 0;
//			while (keyList.hasMoreElements()) {
//				keyListString[num] = keyList.nextElement().toString();
//				if(keyListString[num].contains("software")){
//					String keyId = keyListString[num].replace("software[", "").replace("]", "");
//					keyIdSet.add(keyId);
//				}
//				num++;
//			}
//			if(!keyIdSet.contains(mrtgModel.getId())){
//				return "This's Id null!";
//			}
//			mrtgProperties.put("software["+mrtgModel.getId()+"]", mrtgModel.getSoftware());
//			mrtgProperties.put("xpathObject["+mrtgModel.getId()+"]", mrtgModel.getXpathObject());
//			mrtgProperties.put("xpathTarget["+mrtgModel.getId()+"]", mrtgModel.getXpathTarget());
//			mrtgProperties.put("url["+mrtgModel.getId()+"]", mrtgModel.getUrl());
//			
//			try {
//	            FileOutputStream fos = new FileOutputStream(propertiesFile.toString());
//	            mrtgProperties.store(fos, "Properties");
//	            fos.close();
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//			
//			Properties mrtgCfg = new Properties();
//			StringBuffer cfgFile=new StringBuffer(readConfig.getPathForClass())
//				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.cfg");
//			mrtgCfg.load(new FileInputStream(cfgFile.toString()));
//			Enumeration<?> keyList1 = mrtgCfg.propertyNames();
//			Integer cfgSize1 = mrtgCfg.size();
//			String[] keyListString1 = new String[cfgSize1];
//			Set<String> keyIdSet1 = new HashSet<String>();
//			Integer num1 = 0;
//			while (keyList1.hasMoreElements()) {
//				keyListString1[num1] = keyList1.nextElement().toString();
//				if(keyListString1[num1].contains("PageTop")){
//					String keyId = keyListString1[num1].replace("PageTop[", "").replace("]", "");
//					keyIdSet1.add(keyId);
//				}
//				num1++;
//			}
////			if(keyIdSet.contains(mrtgModel.getId())){
////				
////			}
//			mrtgCfg.setProperty("Title["+mrtgModel.getId()+"]", mrtgModel.getTitle());
//			mrtgCfg.setProperty("PageTop["+mrtgModel.getId()+"]", mrtgModel.getPageTop());
//			mrtgCfg.setProperty("Options["+mrtgModel.getId()+"]", mrtgModel.getOptions());
//			mrtgCfg.setProperty("MaxBytes["+mrtgModel.getId()+"]", mrtgModel.getMaxBytes());
//			mrtgCfg.setProperty("Target["+mrtgModel.getId()+"]", (new StringBuffer("`java -cp \"").append(readConfig.getPathForClass()).append("\" com.huawei.plugins.mrtg.run.bin.MrtgPlugins \"").append(mrtgModel.getUrl()).append("\" \"").append(mrtgModel.getXpathTarget()).append("\"`")).toString());
//			try {
//	            FileOutputStream fos = new FileOutputStream(cfgFile.toString());
//	            mrtgCfg.store(fos, "mrtg");
//	            fos.close();
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//			
//		}catch(Exception e){
//			
//		}
//		return "OK";
//	}
//	
//	/**
//	 * 获取单个监听对象 03
//	 * @param id
//	 * @return
//	 */
//	public String getListener(String id){
//		
//		MrtgModel mrtgModel = new MrtgModel();
//		try{
//			Properties mrtgCfg = new Properties();
//			StringBuffer cfgFile=new StringBuffer(readConfig.getPathForClass())
//				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.cfg");
//			mrtgCfg.load(new FileInputStream(cfgFile.toString()));
//			Enumeration<?> keyList = mrtgCfg.propertyNames();
//			Integer cfgSize = mrtgCfg.size();
//			String[] keyListString = new String[cfgSize];
//			Set<String> keyIdSet = new HashSet<String>();
//			Integer num = 0;
//			while (keyList.hasMoreElements()) {
//				keyListString[num] = keyList.nextElement().toString();
//				if(keyListString[num].contains("PageTop")){
//					String keyId = keyListString[num].replace("PageTop[", "").replace("]", "");
//					keyIdSet.add(keyId);
//				}
//				num++;
//			}
//			mrtgModel.setId(id);
//			mrtgModel.setInterval(mrtgCfg.getProperty("Interval"));
//			mrtgModel.setLanguage(mrtgCfg.getProperty("Language"));
//			mrtgModel.setTitle(mrtgCfg.getProperty("Title["+id+"]"));
//			mrtgModel.setPageTop(mrtgCfg.getProperty("PageTop["+id+"]"));
//			mrtgModel.setOptions(mrtgCfg.getProperty("Options["+id+"]"));
//			mrtgModel.setMaxBytes(mrtgCfg.getProperty("MaxBytes["+id+"]"));
////			String[] listenerIds = PageTopSet.toArray(new String[0]);
//			
//			
//			Properties mrtgProperties = new Properties();
//			StringBuffer propertiesFile=new StringBuffer(readConfig.getPathForClass())
//				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.properties");
//			mrtgProperties.load(new FileInputStream(propertiesFile.toString()));
//			Enumeration<?> keyPropertiesList = mrtgProperties.propertyNames();
//			Integer propertiesSize = mrtgProperties.size();
//			String[] keyPropertiesListString = new String[propertiesSize];
////			Set<String> softwareSet = new HashSet<String>();
//			Integer num1 = 0;
//			while (keyPropertiesList.hasMoreElements()) {
//				keyPropertiesListString[num1] = keyPropertiesList.nextElement().toString();
//				if(keyPropertiesListString[num1].contains("software")){
//					String keyId = keyPropertiesListString[num1].replace("software[", "").replace("]", "");
//					keyIdSet.add(keyId);
//				}
//				num1++;
//			}
//			if(!keyIdSet.contains(id)){
//				return "undefined";
//			}
//			mrtgModel.setSoftware(mrtgProperties.getProperty("software["+id+"]"));
//			mrtgModel.setXpathObject(mrtgProperties.getProperty("xpathObject["+id+"]"));
//			mrtgModel.setXpathTarget(mrtgProperties.getProperty("xpathTarget["+id+"]"));
//			mrtgModel.setUrl(mrtgProperties.getProperty("url["+id+"]"));
//			
//		}catch(Exception e ){
//			e.printStackTrace();
//		}
//
//		return mrtgModel.getMrtgModelXML();
//	}
//	/**
//	 * 返回监听列表  04
//	 * @return
//	 */
//	public String getListenerList_(){
//		StringBuffer returnListenerIdList = new StringBuffer();
//		try{
//			Properties mrtgCfg = new Properties();
//			StringBuffer cfgFile=new StringBuffer(readConfig.getPathForClass())
//				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.cfg");
//			mrtgCfg.load(new FileInputStream(cfgFile.toString()));
//			Enumeration<?> keyList = mrtgCfg.propertyNames();
//			Integer cfgSize = mrtgCfg.size();
//			String[] keyListString = new String[cfgSize];
//			Set<String> keyIdSet = new HashSet<String>();
//			Integer num = 0;
//			while (keyList.hasMoreElements()) {
//				keyListString[num] = keyList.nextElement().toString();
//				if(keyListString[num].contains("PageTop")){
//					String keyId = keyListString[num].replace("PageTop[", "").replace("]", "");
//					keyIdSet.add(keyId);
//				}
//				num++;
//			}
//			
//			Properties mrtgProperties = new Properties();
//			StringBuffer propertiesFile=new StringBuffer(readConfig.getPathForClass())
//				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.properties");
//			mrtgProperties.load(new FileInputStream(propertiesFile.toString()));
//			Enumeration<?> keyPropertiesList = mrtgProperties.propertyNames();
//			Integer propertiesSize = mrtgProperties.size();
//			String[] keyPropertiesListString = new String[propertiesSize];
////			Set<String> softwareSet = new HashSet<String>();
//			Integer num1 = 0;
//			while (keyPropertiesList.hasMoreElements()) {
//				keyPropertiesListString[num1] = keyPropertiesList.nextElement().toString();
//				if(keyPropertiesListString[num1].contains("software")){
//					String keyId = keyPropertiesListString[num1].replace("software[", "").replace("]", "");
//					keyIdSet.add(keyId);
//				}
//				num1++;
//			}
//			String[] listenerIds = keyIdSet.toArray(new String[0]);
//			
//			returnListenerIdList.append("<listenerList>\n");
//			
//			for(String listenerId:listenerIds){
//				returnListenerIdList.append("	<listenerId>")
//				.append(listenerId).append("</listenerId>\n");
//			}
//			returnListenerIdList.append("</listenerList>");
//			System.out.println(keyListString);
//			System.out.println(keyIdSet);
//		}catch(Exception e ){
//			e.printStackTrace();
//		}
//		return returnListenerIdList.toString();
//	}
//	
	/**
	 * 创建mrtg监听01
	 * @param xml
	 * @return
	 */
	public String createMrtg(String xml,String templateId){
		String mrtgxml = getMrtgXml();
		Map<String,TempletModel> map = getMapInXml(xml);
		String id = map.get("id").getValue();
		TempletManager templetManager = new TempletManager();
		List<TempletModel> TempletModelList = templetManager.getBeanInTemplet(map,templateId);
//		String id = map.get("id").getValue();
		
		SAXReader reader = new SAXReader();
		try {
			//读取String的xml文本并转换成bean
			Document document = reader.read(new StringReader(mrtgxml));
//			Element rootElement = document.getRootElement();
			List<Element> nodeElements = document.selectNodes("//template[@id='"+templateId+"']/elements[@id='"+id+"']");
			if(nodeElements.size()!=0){
				return "This is already!";
			}
			StringBuffer teXml = new StringBuffer("<elements id=\"").append(id).append("\">");
			for(TempletModel teModel:TempletModelList){
				teXml.append("<element><name><![CDATA[")
					.append(teModel.getName())
					.append("]]></name><id><![CDATA[")
					.append(teModel.getId())
					.append("]]></id><value><![CDATA[")
					.append(teModel.getValue())
					.append("]]></value><remark><![CDATA[")
					.append(teModel.getRemark())
					.append("]]></remark><mrtgUse><![CDATA[")
					.append(teModel.getMrtgUse())
					.append("]]></mrtgUse></element>")					;
			}
			teXml.append("</elements>");
			Document tedocument = DocumentHelper.parseText(teXml.toString());
			Element teElement = tedocument.getRootElement();
			List<Element> templateElements = document.selectNodes("//template[@id='"+templateId+"']");
			if(templateElements.size()==1){
				templateElements.get(0).add(teElement);
				mrtgXmlManager(document);
			}else{
				return "Error";
			}
//			nodeElements.add(teElement);
//			document.getRootElement().add(teElement);
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	/**
	 * 修改put方法02
	 * @param xml
	 * @return
	 */
	public String putMrtg(String xml,String templateId){
		
		String mrtgxml = getMrtgXml();
		Map<String,TempletModel> map = getMapInXml(xml);
		String id = map.get("id").getValue();
		TempletManager templetManager = new TempletManager();
		List<TempletModel> TempletModelList = templetManager.getBeanInTemplet(map,templateId);
		
		
		SAXReader reader = new SAXReader();
		try {
			//读取String的xml文本并转换成bean TODO
			Document document = reader.read(new StringReader(mrtgxml));
//			Element rootElement = document.getRootElement();
			List<Element> nodeElements = document.selectNodes("//template[@id='"+templateId+"']/elements[@id='"+id+"']");
//			if(nodeElements.size()!=0){
//				return "This is already!";
//			}
			StringBuffer teXml = new StringBuffer("<elements id=\"").append(id).append("\">");
			for(TempletModel teModel:TempletModelList){
				teXml.append("<element><name><![CDATA[")
					.append(teModel.getName())
					.append("]]></name><id><![CDATA[")
					.append(teModel.getId())
					.append("]]></id><value><![CDATA[")
					.append(teModel.getValue())
					.append("]]></value><remark><![CDATA[")
					.append(teModel.getRemark())
					.append("]]></remark><mrtgUse><![CDATA[")
					.append(teModel.getMrtgUse())
					.append("]]></mrtgUse></element>")					;
			}
			teXml.append("</elements>");
			Document tedocument = DocumentHelper.parseText(teXml.toString());
			Element teElement = tedocument.getRootElement();
			List<Element> templateElements = document.selectNodes("//template[@id='"+templateId+"']/elements[@id='"+id+"']");
			if(templateElements.size()==1){
				Element thiselement = templateElements.get(0);
				List elepar = thiselement.getParent().content();    
				elepar.set(elepar.indexOf(thiselement),teElement);  
//				thiselement. = teElement;
				mrtgXmlManager(document);
				saveAndUpdataMrtg(TempletModelList,id,templateId);
			}else{
				return "Error";
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	/**
	 * 获取列表 04
	 * @return
	 */
	public String getListenerList(String templateId){
		StringBuffer returnListenerList = new StringBuffer("<listenerList>\n");
		SAXReader reader = new SAXReader();
		String selectNode = "";
		if(null==templateId||"".equals(templateId)){
			selectNode="/templates/template/elements/@id";
		}else{
			selectNode = "/templates/template[@id='"+templateId+"']/elements/@id";
		}
		try {
			//读取String的xml文本并转换成bean
			Document document = reader.read(new StringReader(getMrtgXml()));
			List<DefaultAttribute> elementList = document.selectNodes(selectNode);
			for(DefaultAttribute e:elementList){
				returnListenerList.append("	<listenerId>")
					.append(e.getText().trim()).append("</listenerId>\n");
//				System.out.print(e.getText());
			}
			System.out.print(elementList);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		returnListenerList.append("</listenerList>\n");
		return returnListenerList.toString();
	}
	
	/**
	 * 根据前台传入的数据生成map
	 * @param xml
	 * @return
	 */
	public Map<String,TempletModel> getMapInXml(String xml){
		Map<String,TempletModel> map = new HashMap<String, TempletModel>();
		SAXReader reader = new SAXReader();
		try {
			//读取String的xml文本并转换成bean
			Document document = reader.read(new StringReader(xml));
//			Element rootElement = document.getRootElement();
			Iterator<Element> iter = document.getRootElement().elementIterator();
			while (iter.hasNext()) {
				TempletModel model = new TempletModel();
				Element element = iter.next();
				String name = element.getName().trim();
				model.setName(name);
				model.setValue(element.getText().trim());
				map.put(name, model);
			}
//			id = document1.selectNodes("/listenerData/").toString();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 对mrtg.xml进行编辑操作
	 * @return
	 */
	public String mrtgXmlManager(Document document){
		try {
			FileOutputStream fis=new FileOutputStream((new StringBuffer(filePath)).append("/com/huawei/plugins/mrtg/run/bin/mrtg.xml").toString());
			OutputStreamWriter osw=new OutputStreamWriter(fis,"UTF-8");
//			osw.write(htmlData(id));
			document.write(osw);
			osw.close();
			fis.close();
			
//			FileWriter out = new FileWriter((new StringBuffer(filePath)).append("/com/huawei/plugins/mrtg/run/bin/mrtg.xml").toString());
//			document.write(out);
//			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	/**
	 * 获取mrtg.xml文件
	 * @return
	 */
	public String getMrtgXml(){
		String xml = "";
		StringBuffer xmlpath = new StringBuffer(filePath).append("/com/huawei/plugins/mrtg/run/bin/mrtg.xml");
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(xmlpath.toString()),"UTF-8"));
			String str = "";
			while((str=in.readLine())!=null){
				xml = xml+str+"\n";
			};
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}
	
	
	
	/**mrtg中id列表
	 * @return
	 */
	public String[] getMrtglistenerIds(){
		String[] listenerIds=null;
		try{
			Properties properties = new Properties();
			StringBuffer file=new StringBuffer(readConfig.getPathForClass())
				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.cfg");
					
			properties.load(new FileInputStream(file.toString()));
			Enumeration<?> keyList = properties.propertyNames();
			Integer propertiesSize = properties.size();
			String[] keyListString = new String[propertiesSize];
			Set<String> PageTopSet = new HashSet<String>();
			Integer num = 0;
			while (keyList.hasMoreElements()) {
				keyListString[num] = keyList.nextElement().toString();
				if(keyListString[num].contains("PageTop")){
					String PageTop = keyListString[num].replace("PageTop[", "").replace("]", "");
					PageTopSet.add(PageTop);
				}
				num++;
			}
			listenerIds = PageTopSet.toArray(new String[0]);
		}catch(Exception e ){
			e.printStackTrace();
		}
		return listenerIds;
	}
	/**自行配置中id列表
	 * @return
	 */
	public String[] getPropertieslistenerIds(){
		String[] listenerIds=null;
		try{
			Properties properties = new Properties();
			StringBuffer file=new StringBuffer(readConfig.getPathForClass())
				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.properties");
					
			properties.load(new FileInputStream(file.toString()));
			Enumeration<?> keyList = properties.propertyNames();
			Integer propertiesSize = properties.size();
			String[] keyListString = new String[propertiesSize];
			Set<String> PageTopSet = new HashSet<String>();
			Integer num = 0;
			while (keyList.hasMoreElements()) {
				keyListString[num] = keyList.nextElement().toString();
				if(keyListString[num].contains("PageTop")){
					String PageTop = keyListString[num].replace("PageTop[", "").replace("]", "");
					PageTopSet.add(PageTop);
				}
				num++;
			}
			listenerIds = PageTopSet.toArray(new String[0]);
		}catch(Exception e ){
			e.printStackTrace();
		}
		return listenerIds;
	}
	
	public Properties getMrtgCfgProperties(String filePath){
		try{
			Properties properties = new Properties();
			StringBuffer file=new StringBuffer(readConfig.getPathForClass())
				.append("\\com\\huawei\\plugins\\mrtg\\run\\bin\\mrtg.cfg");
			
//			filePath = "D:\\mrtg\\bin\\mrtg.cfg";
			properties.load(new FileInputStream(filePath));
			Enumeration<?> keyList = properties.propertyNames();
			Integer propertiesSize = properties.size();
			String[] keyListString = new String[propertiesSize];
			Set<String> PageTopSet = new HashSet<String>();
			Integer num = 0;
			while (keyList.hasMoreElements()) {
				keyListString[num] = keyList.nextElement().toString();
				if(keyListString[num].contains("PageTop")){
					String PageTop = keyListString[num].replace("PageTop[", "").replace("]", "");
					PageTopSet.add(PageTop);
				}
				num++;
			}
			String[] listenerIds = PageTopSet.toArray(new String[0]);
			for(String listenerId:listenerIds){
				
			}
			System.out.println(keyListString);
			System.out.println(PageTopSet);
		}catch(Exception e ){
			e.printStackTrace();
		}
		
		return null;
		
	}
	public String getTempletXml(){
		String xml = "";
		StringBuffer filePath = readConfig.getPathForClass();
		filePath.append("/com/huawei/plugins/mrtg/run/bin/mrtg.xml");
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toString()),"UTF-8"));
			String str = "";
			while((str=in.readLine())!=null){
				xml = xml+str+"\n";
			};
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}
	/**
	 * 获取单个配置 03
	 */
	public RetRpc getListenerToUi(String id,String templateId){
		RetRpc retRpc = new RetRpc();
		if(null == id || "".equals(id) ){
			retRpc.setStatusCode(500);
			return retRpc;
		}
		String result = "";
		List<TempletModel> list = getListenerFileListById(id,templateId);
		if(null != list && list.size()!=0){
			result = getListenerStringByList(id,list);
		}else if(null==list){//null 表示没有值，没有创建
			result="undefined";
		}else if(list.size()==0){//0表示创建但没有根节点
			result="true";
		}
		retRpc.setContent(result);
		return retRpc;
	}
	
	
	public List<TempletModel> getListenerFileListById(String id,String templateId){
		List<TempletModel> teModelMap = new ArrayList<TempletModel>();
		SAXReader reader = new SAXReader();
		try {
			//读取String的xml文本并转换成bean
			Document document = reader.read(new StringReader(getTempletXml()));
//			Element rootElement = document.getRootElement();
			//List<Element> elementList = document.selectNodes("/templates/template[@id='"+templateId+"']/element");
			List<Element> elementList = document.selectNodes("//template[@id='"+templateId+"']/elements[@id='"+id+"']/element");
			if(elementList.size()==0){
				List<Element> elementList_ = document.selectNodes("//template[@id='"+templateId+"']/elements[@id='"+id+"']");
				if(elementList_.size()!=0){
					return teModelMap;
				}else{
					return null;
				}
			}
			for(Element element:elementList){
				TempletModel model = new TempletModel();
				String name = element.element("name").getText().trim();
				model.setName(name);
				model.setId(element.element("id").getText().trim());
				model.setValue(element.element("value").getText().trim());
				model.setRemark(element.element("remark").getText().trim());
				model.setMrtgUse(element.element("mrtgUse").getText().trim());
//				System.out.println(element.asXML());
//				System.out.println("***************");
				teModelMap.add(model);
			}
//			Element element = elementList.get(0);
//			System.out.println(element.asXML());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return teModelMap;
	}
	
	/**
	 * 根据模板获取get结果
	 * @param templateId
	 * @param list
	 * @return
	 */
	public String getListenerStringByList(String templateId,List<TempletModel> list){
		String result = "";
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = null;
		mapContext = new LinkedHashMap<String, Object>();
		mapContext.put("id", templateId);
		mapContext.put("T_TempletModel", list);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("TempleListener.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}
	
	/**
	 * 保存mrtg
	 * @param templetModelList
	 * @return
	 */
	public String saveAndUpdataMrtg(List<TempletModel> templetModelList,String id,String templateId){
		Properties mrtgCfg = new Properties();
		StringBuffer cfgFile=new StringBuffer(readConfig.getPathForClass())
			.append("/com/huawei/plugins/mrtg/run/bin/mrtg.cfg");
		try {
			mrtgCfg.load(new FileInputStream(cfgFile.toString()));
			
			Enumeration<?> keyList1 = mrtgCfg.propertyNames();
			Integer cfgSize1 = mrtgCfg.size();
			String[] keyListString1 = new String[cfgSize1];
			Set<String> keyIdSet = new HashSet<String>();
			Integer num1 = 0;
			while (keyList1.hasMoreElements()) {
				keyListString1[num1] = keyList1.nextElement().toString();
				if(keyListString1[num1].contains("PageTop")){
					String keyId = keyListString1[num1].replace("PageTop[", "").replace("]", "");
					keyIdSet.add(keyId);
				}
				num1++;
			}
			if(keyIdSet.contains(templateId+"_"+id)){
				//包含
				
			}else{
				//不包含
				StringBuffer stringMrtg = new StringBuffer();
				for(TempletModel templetModel:templetModelList){
					if(null!=templetModel.getMrtgUse()&&templetModel.getMrtgUse().equals("true")){
						stringMrtg.append(templetModel.getName())
							.append("[")
							.append(templateId)
							.append("_")
							.append(id)
							.append("]:")
							.append(templetModel.getValue())
							.append("\n")
							;
					}else if(templetModel.getName().equals("Title")){
						stringMrtg.append(templetModel.getName())
						.append("[")
						.append(templateId)
						.append("_")
						.append(id)
						.append("]:")
						.append("null")
						.append("\n")
						;
					}else if(templetModel.getName().equals("PageTop")){
						stringMrtg.append(templetModel.getName())
						.append("[")
						.append(templateId)
						.append("_")
						.append(id)
						.append("]:")
						.append("null")
						.append("\n")
						;
					}
				}
				stringMrtg.append("\n\n");
				StringBuffer filePah_cfg=new StringBuffer();
				filePah_cfg.append(filePath)
					.append("/com/huawei/plugins/mrtg/run/bin/mrtg.cfg");
				File file_cfg = new File(filePah_cfg.toString());
				try {
					FileOutputStream fis=new FileOutputStream(file_cfg,true);
					OutputStreamWriter osw=new OutputStreamWriter(fis,"UTF-8");
					osw.append("\n\n");
					osw.append(stringMrtg.toString());
//					osw.write(htmlData(id));
					osw.close();
					fis.close();
					
					
					// 下面把数据写入创建的文件，首先新建文件名为参数创建FileWriter对象
//					FileWriter resultFile = new FileWriter(file_cfg,true);
//					// 把该对象包装进PrinterWriter对象
////					PrintWriter myNewFile = new PrintWriter(resultFile);
//					// 再通过PrinterWriter对象的println()方法把字符串数据写入新建文件
//					resultFile.append("\n\n");
//					resultFile.append(stringMrtg.toString());
//					resultFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				} // 关闭文件写入流
				ProcessManager.killProcess("mrtg");
				ListenerManager listenerManager = new ListenerManager();
				listenerManager.runMrtg("mrtg");
				RrdtoolManager rrdtoolManager = new RrdtoolManager();
				rrdtoolManager.refurbishData(templateId+"_"+id);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	public static void main(String[] args){
		ListenerManager listenerManager = new ListenerManager();
		System.out.println(listenerManager.getListenerToUi("1","*"));
	}
}
