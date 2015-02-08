package com.huawei.agilete.northinterface.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.huawei.agilete.base.common.ReadConfig;

public class RrdtoolManager {
	ReadConfig readConfig = new ReadConfig();
	StringBuffer filePath = readConfig.getPathForClass();
	StringBuffer rrdExePath=new StringBuffer(filePath).append("/com/huawei/plugins/rrdtool/tool/");//rrdtool的exe地址
	StringBuffer htmlPath=new StringBuffer(filePath.toString().replace("/WEB-INF/classes", "")).append("/statisticsPage/html/");//html的地址
	StringBuffer rrdPath=new StringBuffer(filePath.toString().replace("/WEB-INF/classes", "")).append("/statisticsPage/run/");//rrd数据库文件地址

	/**
	 * 运行rrdtool生成图片语句
	 * @param id
	 * @return
	 */
	private String RrdGraph(String id){
		
		StringBuffer cdcmd = new StringBuffer("cd \"");
		cdcmd.append(rrdPath).append("\"");
		Runtime run = Runtime.getRuntime();
		try {
			Process p =run.exec("cmd");
			BufferedOutputStream out = new BufferedOutputStream(p.getOutputStream());
			BufferedInputStream in=new BufferedInputStream(p.getInputStream()); 
			out.write(cdcmd.toString().getBytes());
			out.write("\r\n".getBytes());
			out.flush();
			out.write(RrdGraphDay(id).getBytes());
			out.write("\r\n".getBytes());
			out.flush();
			out.write(RrdGraphWeek(id).getBytes());
			out.write("\r\n".getBytes());
			out.flush();
			out.write(RrdGraphMon(id).getBytes());
			out.write("\r\n".getBytes());
			out.flush();
			out.write(RrdGraphYear(id).getBytes());
			out.write("\r\n".getBytes());
			out.flush();
			out.close();
			BufferedReader brz=new BufferedReader(new InputStreamReader(in));
			StringBuffer systemPrint = new StringBuffer();
			String linez=null;  
			while((linez=brz.readLine())!=null){
				systemPrint.append(linez).append("\n");
			} 
			System.out.println(systemPrint);
			p.destroy();

		} catch (IOException e) {
			e.printStackTrace();
			return "Error!";
		}
		return "OK";
	}
	/**
	 * 生成日星期图片
	 * @param id
	 * @return
	 */
	private String RrdGraphDay(String id){
		String small = new String(id);
		small = small.toLowerCase();
		StringBuffer returnString = new StringBuffer("\"").append(rrdExePath);
		returnString.append("rrdtool\" graph \"").append(htmlPath).append(id)
					.append("_day.png\" --start -1d --end now --width 700 --height 350 DEF:ds0=")//其中-1d 和now代表前一天到现在
					.append(small).append(".rrd:ds0:AVERAGE LINE2:ds0#FF0000 VDEF:ds0max=ds0,MAXIMUM VDEF:ds0avg=ds0,AVERAGE ")
					.append("VDEF:ds0min=ds0,MINIMUM VDEF:ds0last=ds0,LAST COMMENT:\"                Max                      ")
					.append("AVERAGE                    Min                      Now\" GPRINT:ds0max:\"            %6.3lf ")
					.append("%Sbps\"  GPRINT:ds0avg:\"            %6.3lf %Sbps\"  GPRINT:ds0min:\"            %6.3lf %Sbps\" ")
					.append("GPRINT:ds0last:\"            %6.3lf %Sbps\"");
//		returnString.get
		return returnString.toString();
	}
	/**
	 * 生成星期月图片
	 * @param id
	 * @return
	 */
	private String RrdGraphWeek(String id){
		String small = new String(id);
		small = small.toLowerCase();
		StringBuffer returnString = new StringBuffer("\"").append(rrdExePath);
		returnString.append("rrdtool\" graph \"").append(htmlPath).append(id)
					.append("_week.png\" --start -1w --end now --width 700 --height 350 DEF:ds0=")//其中-1w 和now代表前一星期到现在
					.append(small).append(".rrd:ds0:AVERAGE LINE2:ds0#FF0000 VDEF:ds0max=ds0,MAXIMUM VDEF:ds0avg=ds0,AVERAGE ")
					.append("VDEF:ds0min=ds0,MINIMUM VDEF:ds0last=ds0,LAST COMMENT:\"                Max                      ")
					.append("AVERAGE                    Min                      Now\" GPRINT:ds0max:\"            %6.3lf ")
					.append("%Sbps\"  GPRINT:ds0avg:\"            %6.3lf %Sbps\"  GPRINT:ds0min:\"            %6.3lf %Sbps\" ")
					.append("GPRINT:ds0last:\"            %6.3lf %Sbps\"");
//		returnString.get
		return returnString.toString();
	}
	/**
	 * 生成月数据图片
	 * @param id
	 * @return
	 */
	private String RrdGraphMon(String id){
		String small = new String(id);
		small = small.toLowerCase();
		StringBuffer returnString = new StringBuffer("\"").append(rrdExePath);
		returnString.append("rrdtool\" graph \"").append(htmlPath).append(id)
					.append("_month.png\" --start -1m --end now --width 700 --height 350 DEF:ds0=")//其中-1m 和now代表前一月到现在
					.append(small).append(".rrd:ds0:AVERAGE LINE2:ds0#FF0000 VDEF:ds0max=ds0,MAXIMUM VDEF:ds0avg=ds0,AVERAGE ")
					.append("VDEF:ds0min=ds0,MINIMUM VDEF:ds0last=ds0,LAST COMMENT:\"                Max                      ")
					.append("AVERAGE                    Min                      Now\" GPRINT:ds0max:\"            %6.3lf ")
					.append("%Sbps\"  GPRINT:ds0avg:\"            %6.3lf %Sbps\"  GPRINT:ds0min:\"            %6.3lf %Sbps\" ")
					.append("GPRINT:ds0last:\"            %6.3lf %Sbps\"");
//		returnString.get
		return returnString.toString();
	}
	/**
	 * 生成年数据语句
	 * @param id
	 * @return
	 */
	private String RrdGraphYear(String id){
		String small = new String(id);
		small = small.toLowerCase();
		StringBuffer returnString = new StringBuffer("\"").append(rrdExePath);
		returnString.append("rrdtool\" graph \"").append(htmlPath).append(id)
					.append("_year.png\" --start -1y --end now --width 700 --height 350 DEF:ds0=")//其中-1y 和now代表前一年到现在
					.append(small).append(".rrd:ds0:AVERAGE LINE2:ds0#FF0000 VDEF:ds0max=ds0,MAXIMUM VDEF:ds0avg=ds0,AVERAGE ")
					.append("VDEF:ds0min=ds0,MINIMUM VDEF:ds0last=ds0,LAST COMMENT:\"                Max                      ")
					.append("AVERAGE                    Min                      Now\" GPRINT:ds0max:\"            %6.3lf ")
					.append("%Sbps\"  GPRINT:ds0avg:\"            %6.3lf %Sbps\"  GPRINT:ds0min:\"            %6.3lf %Sbps\" ")
					.append("GPRINT:ds0last:\"            %6.3lf %Sbps\"");
//		returnString.get
		return returnString.toString();
	}
	
	/**
	 * 创建html文件和写入数据
	 * @param id
	 * @return
	 */
	private String createHtml(String id){
		try {
			StringBuffer fileName = new StringBuffer(htmlPath);
			fileName.append(id)
					.append(".html");
			File myFile = new File(fileName.toString());
			//查看html文件是否存在
			if (!myFile.exists()){
				//不存在创建
                myFile.createNewFile();
			}
//			FileWriter resultFile = new FileWriter(myFile);
//			PrintWriter myNewFile = new PrintWriter(resultFile);
//			myNewFile.println(htmlData(id));
//			resultFile.close();
			
			
			FileOutputStream fis=new FileOutputStream(myFile);
			OutputStreamWriter osw=new OutputStreamWriter(fis,"UTF-8");
			osw.write(htmlData(id));
			osw.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error!";
		}
		return "OK";
	}
	/**
	 * 获取html数据
	 * @param id
	 * @return
	 */
	private String htmlData(String id){
		String[] ids = id.split("_", 2);
		ListenerManager lm = new ListenerManager();
		Map<String,String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		try {
			//读取String的xml文本并转换成bean
			Document document = reader.read(new StringReader(lm.getMrtgXml()));
			List<Element> nodeElements = document.selectNodes("//template[@id='"+ids[0]+"']/elements[@id='"+ids[1]+"']/element");
			if(nodeElements.size()>0){
				for(Element element:nodeElements){
					String name = element.element("name").getText().trim();
					String value = element.element("value").getText().trim();
					map.put(name, value);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		Date dt=new Date();
		SimpleDateFormat matter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(matter.format(dt));
		StringBuffer htmlData = new StringBuffer();
		htmlData.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/dtd/xhtml11.dtd\">\n")
				.append("<html>\n	<head>\n		<title>")
				.append(map.get("Title"))
				.append("</title>\n		<meta http-equiv=\"refresh\" content=\"300\" />\n")
				.append("		<meta http-equiv=\"pragma\" content=\"no-cache\" />\n		<meta http-equiv=\"cache-control\" content=\"no-cache\" />\n")
				.append("		<meta http-equiv=\"expires\" content=\"300\" />\n		<meta http-equiv=\"generator\" content=\"AgilePM 1.0\" />\n")
				.append("		<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />\n")
				.append("		<style type=\"text/css\">\n			body {\n				background-color: #ffffff;\n			}\n			div {\n")
				.append("				border-bottom: 2px solid #aaa;\n				padding-bottom: 10px;\n				margin-bottom: 5px;\n")
				.append("			}\n			div h2 {\n				font-size: 1.2em;\n			}\n			div.graph img {\n				margin: 5px 0;\n")
				.append("			}\n		</style>\n	</head>\n<body>\n")
				.append("<H1>")
				.append(map.get("PageTop"))
				.append("</H1><p>最后统计更新时间： <strong>")
				.append(matter.format(dt))
				.append("</strong></p>\n")
				.append("		<div class=\"graph\">\n			<h2>每日 图表 (5 分钟 平均)</h2>\n			<img src=\""+id+"_day.png\" title=\"day\" alt=\"day\" />\n")
				.append("		</div>\n		<div class=\"graph\">\n			<h2>每周 图表 (30 分钟 平均)</h2>\n")
				.append("			<img src=\""+id+"_week.png\" title=\"week\" alt=\"week\" />\n		</div>\n		<div class=\"graph\">\n")
				.append("			<h2>每月 图表 (2 小时 平均)</h2>\n			<img src=\""+id+"_month.png\" title=\"month\" alt=\"month\" />\n")
				.append("		</div>\n		<div class=\"graph\">\n			<h2>每年 图表 (1 天 平均)</h2>\n")
				.append("			<img src=\""+id+"_year.png\" title=\"year\" alt=\"year\" />\n		</div>\n	</body>\n</html>\n");
		return htmlData.toString();
	}
	public String refurbishData(String id){
		RrdGraph(id);
		createHtml(id);
		return "OK";
	}
	public static void main(String[] args){
	}
}
