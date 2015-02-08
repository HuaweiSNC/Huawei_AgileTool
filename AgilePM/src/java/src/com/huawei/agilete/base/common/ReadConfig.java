package com.huawei.agilete.base.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.huawei.agilete.data.MyData;

/**
 * 
 * @author lWX200287
 *
 */
public class ReadConfig {
	private Properties properties = null;
	private StringBuffer path;

	public ReadConfig(){

	}



	public Properties get(){
		if(null == properties){
			StringBuffer filePah=new StringBuffer(getPath());
			filePah.append("conf\\my.ini");
			File file = new File(filePah.toString());
			if(file.isFile()&&file.exists()){
				InputStreamReader read;
				try {
					read = new InputStreamReader(new FileInputStream(file),"UTF-8");
					properties = new Properties();
					properties.load(read);
					read.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return properties;
			}
			return null;
		}else{
			return properties;
		}
	}

	public String getTemple(String fileName){
		String readContent = "";
		StringBuffer filePah=new StringBuffer(getPath());
		filePah.append("conf/schemaTemple/");
		filePah.append(fileName);
		File file=new File(filePah.toString());
		try {
			readContent = FileUtils.readFileToString(file,"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readContent;
	}

	public StringBuffer getPath() {
		if(null == path || "".equals(path.toString())){
			path = new StringBuffer();
			String filePah=ReadConfig.class.getResource("/").getPath();
			filePah = filePah.replace("%20", " ");
			if(filePah.contains("WEB-INF/classes/")){
				path.append(filePah.substring(1, filePah.lastIndexOf("WEB-INF/classes/")));
			}else if(filePah.contains("build/classes/")){
				path.append(filePah.substring(1, filePah.lastIndexOf("build/classes/")));
				path.append("/WebContent/");
			}
		}
		return path;
	}
	
	public StringBuffer getPathForClass() {
		StringBuffer systemFile = new StringBuffer();
		String classFile = this.getClass().getClassLoader().getResource("").getPath();
		classFile = classFile.substring(1, classFile.length()-1).replace("%20", " ");
		systemFile.append(classFile);
		return systemFile;
	}
	


	public static void main(String[] args) {
		ReadConfig aaaa= new ReadConfig();
		System.out.println(aaaa.getPathForClass());
	}
}
