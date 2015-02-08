package com.huawei.agilete.base.common;

public class MyIO {
	
	public static String characterFormat(String content){
		
		//空格问题
		if(null == content){
			return content;
		}
		content = content.replace("\n", "");
		content = content.replace("\t", "");
		content = content.replace("\r", "");
		content = content.replaceAll(">([\\s]*)<", "><");
		return content;
	}
	
	public static void main(String[] args) {
		
	}

}
