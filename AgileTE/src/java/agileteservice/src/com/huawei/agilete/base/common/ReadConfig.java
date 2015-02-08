package com.huawei.agilete.base.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

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
            filePah.append("conf\\config.xml");
            File file = new File(filePah.toString());
            if(file.isFile()&&file.exists()){
                //InputStreamReader read;
                InputStream read;
                try {
                    //read = new InputStreamReader(new FileInputStream(file),"UTF-8");
                    read = new FileInputStream(file);
                    properties = new Properties();
                    //properties.load(read);
                    properties.loadFromXML(read);
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

    
    public String getIps(String fileName){
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
    		String filePah = ReadConfig.class.getResource("/").getPath();
    		filePah = filePah.replace("%20", " ");
    		path.append(filePah.substring(1,filePah.lastIndexOf("classes/")));
        }
        return path;
    }



    public static void main(String[] args) {

    }
}
