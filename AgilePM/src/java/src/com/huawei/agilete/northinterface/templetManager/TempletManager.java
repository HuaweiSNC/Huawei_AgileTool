package com.huawei.agilete.northinterface.templetManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.agilete.northinterface.bean.TempletModel;

/**
 * 模板处理类
 * @author xWX202247
 *
 */
public class TempletManager {
	ReadConfig readConfig = new ReadConfig();
	StringBuffer filePath = readConfig.getPathForClass();
//	StringBuffer filePath = new StringBuffer();

	public String parameterToTemplet(String xml,Map<String, TempletModel> map){
		Iterator<?> keys = map.keySet().iterator();
		//遍历Map
		while (keys.hasNext()) {
			String key = keys.next().toString();
			TempletModel tem = map.get(key);
			//将配置里面的值取出来
			xml = xml.replace("${"+key+"}", tem.getValue());
		}
		//将没有替换的key去掉
		xml = xml.replaceAll("\\$\\{\\w*\\}", "");
		xml = xml.replace("@{classLoaderPath}", filePath.toString());
		return xml;
	}
	
	public String getTempletXml(){
		String xml = "";
		StringBuffer xmlfile = new StringBuffer(filePath).append("/com/huawei/plugins/mrtg/run/template/template.xml");
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(xmlfile.toString()),"UTF-8"));
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
	 * @param xml 处理过的xml文本
	 * @param templateId 前台传入的模板id号码
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,TempletModel> getTempletToMapBean(String xml,String templateId){
		Map<String,TempletModel> teModelMap = new HashMap<String,TempletModel>();
		SAXReader reader = new SAXReader();
		try {
			//读取String的xml文本并转换成bean
			Document document = reader.read(new StringReader(xml));
//			Element rootElement = document.getRootElement();
			List<Element> elementList = document.selectNodes("//template[@id='"+templateId+"']/elements/element");
			if(elementList.size()==0){
				return teModelMap;
			}
//			List<Element> teList = elementList.get(0).;
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
				teModelMap.put(name, model);
			}
//			Element element = elementList.get(0);
//			System.out.println(element.asXML());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return teModelMap;
	}
	
	/**
	 * 处理后的配置文件bean集合
	 * @param map
	 * @return
	 */
	public List<TempletModel> getTempletToBeanList(Map<String,TempletModel> map){
		List<TempletModel> templeList = new ArrayList<TempletModel>();
		Iterator<?> keyList = map.keySet().iterator();
		//遍历Map
		while (keyList.hasNext()) {
			String key = keyList.next().toString();
			TempletModel model = map.get(key);
			Pattern p = Pattern.compile("\\@\\{[\\w\\.]*\\}");
			Matcher m = p.matcher(model.getValue());
//			while(m.find()){
//			if(m.find()){
				recursionTemplet(map,model);
//			}
//			}
				templeList.add(model);
//			System.out.println("**************");
		}
		
		return templeList;
	}
	
	/**
	 * 替换的递归方法
	 * @param map
	 * @param model
	 */
	public void recursionTemplet(Map<String,TempletModel> map,TempletModel model){
		if(null==model){
			return;
		}
		if(model.getName().equals("xmlTarget")){
			System.out.println("aasjkdhfasjkldhfkajsdfh");
		}
		Pattern p = Pattern.compile("\\@\\{[\\w\\.]*\\}");
		Matcher m = p.matcher(model.getValue());
		while(m.find()){
			String key_m = m.group().replace("@{","").replace("}","");
			TempletModel model_ = map.get(key_m);
			if(key_m.contains("replaceurl")){
				key_m = key_m.replace("replaceurl.", "");
				model_ = map.get(key_m);
//				model_.setValue(model_.getValue().replace("/", "%2F"));
				recursionTemplet(map,model_);
				model.setValue(model.getValue().replace(m.group(), model_.getValue().replace("/", "%2F")));

			}else{
			
				recursionTemplet(map,model_);
				model.setValue(model.getValue().replace(m.group(), model_.getValue()));
			}
		}
//		if(!m.find()){
//			return;
//		}
//		String value = model.getValue();
//		model.setValue(model.getValue().replace(m.group(), model.getValue()));
		
		
//		IF(P.MATCHER(MODEL.GETVALUE()).FIND()){
//			RECURSIONTEMPLET(MAP,MODEL);
//		}ELSE{
//			MODEL.SETVALUE(MODEL.GETVALUE().REPLACE(M.GROUP(), MODEL.GETVALUE()));
////		SYSTEM.OUT.PRINTLN("*");
////		MODEL.SETVALUE(VALUE);
////		M = P.MATCHER(MODEL.GETVALUE());
//		}
		
	}
	
	
	/**
	 * 获取模板的bean对象
	 * @param map
	 * @param templateId
	 * @return
	 */
	public List<TempletModel> getBeanInTemplet(Map<String,TempletModel> map,String templateId){
		return getTempletToBeanList(getTempletToMapBean(parameterToTemplet(getTempletXml(),map),templateId));
	}
	public static void main(String[] args){
		TempletManager te = new TempletManager();
		te.filePath=new StringBuffer("D:/huawei_home/AgilePM/algorithm");
//		Map map =new HashMap();
////		map.put("a", "sdfsd");
////		map.put("b", "sdfsd");
//		TempletModel temodel = new TempletModel();
//		temodel.setValue("************************************");
//		map.put("deviceTarget", temodel);
//		System.out.println(te.getTempletToBeanList(te.getTempletToMapBean(te.parameterToTemplet(te.getTempletXml(),map),"traffic")));
//		String aaa= "@{deviceTarget}/ifm/interfaces/interface?ifName=,ifDynamicInfo&ifStatistics?receiveByte,sendByte";
//		Pattern p = Pattern.compile("\\@\\{\\w*\\}");
//		Matcher m = p.matcher(aaa);
//		System.out.println(m.find());
//		System.out.println(aaa.matches("\\@\\{\\w*\\}"));
		
		
		
		
		SAXReader reader = new SAXReader();
		try {
			//读取String的xml文本并转换成bean
			Document document = reader.read(new StringReader(te.getTempletXml()));
//			Element rootElement = document.getRootElement();
			List<DefaultAttribute> elementList = document.selectNodes("/templates/template[@id='']/elements/@id");
			for(DefaultAttribute e:elementList){
				System.out.print(e.getText());
			}
			System.out.print(elementList);
//			Element element = elementList.get(0);
//			System.out.println(element.asXML());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
