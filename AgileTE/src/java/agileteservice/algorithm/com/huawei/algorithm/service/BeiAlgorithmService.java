package com.huawei.algorithm.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * @author xWX202247
 * 算法运行方法
 */
public class BeiAlgorithmService {

    /**
     * 算法读取文件与输出文件的目录（只填写工程名后地址）
     * 算法输入输出文件必须在算法的目录下的input和output文件夹下！
     */
////    private String systemFile = System.getProperty("user.dir");
//    private String systemFile = this.getClass().getClassLoader().getResource("/").getPath();
//    private String inputNodeFile = systemFile + "\\algorithm\\com\\huawei\\manage\\beiyan\\input\\node.xls";//input节点
//    private String inputLinkFile = systemFile + "\\algorithm\\com\\huawei\\manage\\beiyan\\input\\link.xls";//input线
//    private String inputDemandFile = systemFile + "\\algorithm\\com\\huawei\\manage\\beiyan\\input\\demand.xls";//input需求
//    private String outputResultsFile = systemFile + "\\algorithm\\com\\huawei\\manage\\beiyan\\output\\results.xls";//结果输出文件地址
//    private String beiAlgorithmFile = "cd algorithm\\com\\huawei\\manage\\beiyan";//结果输出文件地址
//    private String beiAlgorithmEXE = "cspf.exe";//结果输出文件地址
    private String systemFile;
    private String inputNodeFile;//input节点
    private String inputLinkFile;//input线
    private String inputDemandFile;//input需求
    private String outputResultsFile;//结果输出文件地址
    private String beiAlgorithmFile;//结果输出文件地址
    private String beiAlgorithmEXE;//结果输出文件地址
    private String beiAlgorithmInputFile;//输入文件存放目录
    private String beiAlgorithmOutputFile;//输出文件存放目录
    public BeiAlgorithmService(){
        systemFile = this.getClass().getClassLoader().getResource("/").getPath();
        systemFile = systemFile.substring(1, systemFile.length()-1).replace("/", "\\").replace("%20", " ");
        inputNodeFile = systemFile + "\\com\\huawei\\manage\\beiyan\\input\\node.xls";//input节点
        inputLinkFile = systemFile + "\\com\\huawei\\manage\\beiyan\\input\\link.xls";//input线
        inputDemandFile = systemFile + "\\com\\huawei\\manage\\beiyan\\input\\demand.xls";//input需求
        outputResultsFile = systemFile + "\\com\\huawei\\manage\\beiyan\\output\\results.xls";//结果输出文件地址
        beiAlgorithmFile = "cd \""+systemFile+"\\com\\huawei\\manage\\beiyan\"";//结果输出文件地址
        beiAlgorithmEXE = "cspf.exe";//结果输出文件地址
        beiAlgorithmInputFile = systemFile + "\\com\\huawei\\manage\\beiyan\\input";
        beiAlgorithmOutputFile = systemFile + "\\com\\huawei\\manage\\beiyan\\output";
    }
    
    /**
     * @param xml
     * @return
     */
    public synchronized String getBeiAlgorithm(String xml){
    	deleteFile();
        String status = "";
        String[] returnString = xmlToString(xml);
        if(returnString != null && returnString[3] != null && returnString[3].equals("true")){
            status = createInput(returnString);
        }else{
            //System.out.println(returnString[3]);
            return "XML is error!";
        }
        if(!status.equals("true")){
            return "Create input error!";
        }
        runBeiAlgorithmExe();
        return readOutput();
//        return "";
    }
    
    private String runBeiAlgorithmExe(){
        Runtime rt = Runtime.getRuntime();
        try {
            Process process = rt.exec("cmd");
            
            
            BufferedOutputStream out = new BufferedOutputStream(process.getOutputStream());
            BufferedInputStream in=new BufferedInputStream(process.getInputStream()); 
            out.write(beiAlgorithmFile.getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.write(beiAlgorithmEXE.getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.write("s".getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.write("q".getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.write("exit".getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.close();
            BufferedReader brz=new BufferedReader(new InputStreamReader(in));
            String linez=null;  
            while((linez=brz.readLine())!=null){  
//                if(linez.indexOf(">")!=-1) break;  
                //System.out.println(linez);  
            } 
            process.destroy();
            
            
//            DataOutputStream dataOutputStream = new DataOutputStream(is);
////            is.write("s\n".getBytes());
////            is.write("q\n".getBytes());
////            is.flush();
////            is.close();
//            dataOutputStream.writeBytes("s");
//            dataOutputStream.writeBytes("\r\n");
//            dataOutputStream.flush();
//            dataOutputStream.writeBytes("q");
//            dataOutputStream.close();
//            InputStream zz = process.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(zz));
//            String line;
//            StringBuffer cmdout = new StringBuffer(); 
//            while ((line = br.readLine()) != null) { 
//                cmdout.append(line); 
//            }
//            //System.out.println(cmdout.toString());
//            OutputStream zx = process.getOutputStream();
//            zx.write("s\n".getBytes());
//            zx.write("q\n".getBytes());
//            zx.flush();
//            zx.close();
//            
//            InputStream qq = process.getInputStream();
//            BufferedReader ww = new BufferedReader(new InputStreamReader(qq));
//            cmdout = new StringBuffer(); 
//            while ((line = ww.readLine()) != null) { 
//                cmdout.append(line); 
//            }
//            //System.out.println(cmdout.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return "BeiAlgorithmEXE is error!";
        }
        return "true";
    }
    
    /** 读取结果文档并转换为xml返回
     * @return
     */
    private String readOutput(){
        String readLineString;
        String outputTxt = "";
        StringBuffer returnXml = new StringBuffer("<paths>\n");
        try {
            File outputFile = new File(outputResultsFile);
            for(int i=0;i<40;i++){
                if(outputFile.exists()){//判断文件是否创建成功
                    break;
                }else{
                    Thread.currentThread();//未创建成功演示50毫秒执行
                    Thread.sleep(50);
                }
                if(i==39){
                    return "Create output error!";
                }
            }
            FileReader fileReader = new FileReader(outputFile);
            BufferedReader input = new BufferedReader(fileReader);
            while((readLineString = input.readLine())!=null){
                outputTxt = outputTxt + readLineString;
            }
            String[] outputPaths = outputTxt.split("test iter_dijkstra");//输出文件每一个开头都有此语句，用来分组
            for(String outputPath:outputPaths){
                Long thistime = System.currentTimeMillis();
                String timeId = Integer.toHexString(thistime.toString().hashCode()).toUpperCase();
                if(outputPath==null||outputPath.trim().equals("")){//去除第一个为空的
                    continue;
                }
                returnXml.append("    <path>\n");
                outputPath = outputPath.replaceAll("<[a-zA-Z\\s,0-9]*>", "");//正在表达式去除"<>"的内容
                String[] temps1 = outputPath.split(",");//分组用来获取起末点
                if(temps1.length!=2){//如果非组出现非两个结果抛出异常
                    return "Output error!";
                }
                String road = temps1[0];//
                road = road.replace("Results of routing from ", "");//去除多余内容
                String[] startendnode = road.split("to");
                if(startendnode.length!=2){
                    return "Output error!";
                }
                returnXml.append("        <Source>")
                .append(startendnode[0].trim())
                .append("</Source>\n")
                .append("        <Destination>")
                .append(startendnode[1].trim())
                .append("</Destination>\n");
                
                outputPath = temps1[1];//结果信息分析
                String[] temps2 = outputPath.split("Work path:");
                if(temps2.length!=2){
                    return "Output error!";
                }
                outputPath = temps2[1];
                String[] nodeListString = outputPath.split("Backup path:");
                if(nodeListString.length!=2){
                    return "Output error!";
                }
                String workPath = nodeListString[0];
                String backupPath = nodeListString[1];
                workPath = workPath.replaceAll("Work [a-zA-Z\\s]*+:+[\\s0-9]*", "");
                backupPath = backupPath.replaceAll("Backup [a-zA-Z\\s]*+:+[\\s0-9]*", "");
                String[] workPathNodes = workPath.split("->");
                if(workPathNodes.length<2){
                    returnXml.append("        <wrong>")
                    .append(workPath)
                    .append("</wrong>\n        <result>\n        <Primary name=\"\">\n        </Primary>\n")
                    .append("        <Backup name=\"\">\n        </Backup>\n        </result>\n    </path>\n<paths>\n");
                    return returnXml.toString();
                }
                returnXml.append("        <wrong></wrong>\n        <result>\n        <Primary name=\"Primary_"+timeId+"\">\n");
                for(String workPathNode:workPathNodes){
                    returnXml.append("            <node>")
                    .append(workPathNode.trim())
                    .append("</node>\n");
                }
                String[] backupPathNodes = backupPath.split("->");
                returnXml.append("        </Primary>\n        <Backup name=\"Backup_"+timeId+"\">\n");
                for(String backupPathNode:backupPathNodes){
                    returnXml.append("            <node>")
                    .append(backupPathNode.trim())
                    .append("</node>\n");
                }
                returnXml.append("        </Backup>\n        </result>\n    </path>\n");
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "File error!";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Error!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Read output error!";
        }
        returnXml.append("</paths>\n");
        deleteFile();
        return returnXml.toString();
    }
    /**
     * 创建input的文件
     * @param returnString
     * @return
     */
    private String createInput(String[] returnString){
        File nodeFile = new File(inputNodeFile);
        File linkFile = new File(inputLinkFile);
        File demandFile = new File(inputDemandFile);
        File inputFile = new File(beiAlgorithmInputFile);
        File outputFile = new File(beiAlgorithmOutputFile);
        if(!inputFile.exists()){
            inputFile.mkdirs();
        }
        if(!outputFile.exists()){
            outputFile.mkdirs();
        }
        if(!nodeFile.exists()){ //不存在则创建 
            try {
                nodeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "Create NewFile error!";
            }
        }
        if(!linkFile.exists()){ //不存在则创建 
            try {
                linkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "Create NewFile error!";
            }
        }
        if(!demandFile.exists()){ //不存在则创建 
            try {
                demandFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "Create NewFile error!";
            }
        }
        try {
            BufferedWriter nodeInput = new BufferedWriter(new FileWriter(nodeFile));
            BufferedWriter linkInput = new BufferedWriter(new FileWriter(linkFile));
            BufferedWriter demandInput = new BufferedWriter(new FileWriter(demandFile));
            nodeInput.write(returnString[0]);
            linkInput.write(returnString[1]);
            demandInput.write(returnString[2]);
            nodeInput.close();
            linkInput.close();
            demandInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "FileWriter error!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Write error!";
        }
        return "true";
    }
    
    /**
     * 对传入的xml处理成报错的String类型
     * @param xml
     * @return
     */
    @SuppressWarnings("unchecked")
    private String[] xmlToString(String xml){
        //声明返回字段，returnString[3]为true表示没有错误，否则则为错误信息
        String[] returnString = new String[4];
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new StringReader(xml));//读取String的xml文本并转换
//            Element root = doc.getRootElement();
            //获取所有的节点信息
            List<Element> nodeElements = doc.selectNodes("/topodata/topo/toponodes/toponode");
            if(nodeElements==null){
                returnString[3]="Node is null!";
                return returnString;
            }
            /*
             * 规避算法bug 开始1
             */
            Map<Integer,String[]> nodemap = new HashMap<Integer, String[]>();
            Integer[] nodeids = new Integer[nodeElements.size()];
            Integer num = 0;
            /*
             * 规避算法bug 结束1
             */
            StringBuffer nodeString = new StringBuffer();
            nodeString.append("NodeID	NodeLevel	BelongRingID\n");
            for(Element e:nodeElements){
                String NodeID = e.elementText("nodeID");
                if(NodeID==null||NodeID.equals("")){
                    returnString[3]="NodeId is null!";
                    return returnString;
                }
                String NodeLevel = e.elementText("NodeLevel");
                if(NodeLevel==null||NodeLevel.equals("")){
                    NodeLevel = "1";
                }
                String BelongRingID = e.elementText("BelongRingID");
                if(BelongRingID==null||BelongRingID.equals("")){
                    BelongRingID = "1";
                }
                
                /*
                 * 规避算法bug 开始2
                 */
                nodeids[num] = Integer.parseInt(NodeID);
                String[] nodem = new String[2];
                nodem[0] = NodeLevel;
                nodem[1] = BelongRingID;
                nodemap.put(Integer.parseInt(NodeID), nodem);
                num++;
                /*
                 * 规避算法bug 结束2
                 */
                
            }
            Arrays.sort(nodeids);
            for(Integer nodeInt:nodeids){
                String[] nodem = nodemap.get(nodeInt);
                String BelongRingID = nodem[1];
                String NodeLevel = nodem[0];
                Integer NodeID = nodeInt;
                nodeString.append(NodeID)
                .append("	")
                .append(NodeLevel)
                .append("	")
                .append(BelongRingID)
                .append("\n");
            }
            
            //获取所有线的信息
            List<Element> linkElements = doc.selectNodes("/topodata/topo/topoLinks/topoLink");
            if(linkElements==null){
                returnString[3]="Link is null!";
                return returnString;
            }
            StringBuffer linkString = new StringBuffer();
            linkString.append("Source	Sink	LinkMetric	Capacity\n");
            for(Element e:linkElements){
                String Source = e.elementText("Source");
                if(Source==null||Source.equals("")){
                    returnString[3]="Link's Source is null!";
                    return returnString;
                }
                String Sink = e.elementText("Sink");
                if(Sink==null||Sink.equals("")){
                    returnString[3]="Link's Sink is null!";
                    return returnString;
                }
                String LinkMetric = e.elementText("LinkMetric");
                if(LinkMetric==null||LinkMetric.equals("")){
                    LinkMetric = "1";
                }
                String Capacity = e.elementText("Capacity");
                if(Capacity==null||Capacity.equals("")){
                    Capacity="0";
                }
                linkString.append(Source)
                .append("	")
                .append(Sink)
                .append("	")
                .append(LinkMetric)
                .append("	")
                .append(Capacity)
                .append("\n");
            }
            //所需计算的路线
            List<Element> demandElements = doc.selectNodes("/topodata/demands/demand");
            if(demandElements==null){
                returnString[3]="Demand is null!";
                return returnString;
            }
            StringBuffer demandString = new StringBuffer();
            demandString.append("Source	Destination	DisjointType	Bandwidth	PrimaryMustPassNodesSet	BackupMustPassNodesSet\n");
            for(Element e:demandElements){
                String Source = e.elementText("Source");
                if(Source==null||Source.equals("")){
                    returnString[3]="Demand's Source is null!";
                    return returnString;
                }
                String Destination = e.elementText("Destination");
                if(Destination==null||Destination.equals("")){
                    returnString[3]="Demand's Destination is null!";
                    return returnString;
                }
                String DisjointType = e.elementText("DisjointType");
                if(DisjointType==null||DisjointType.equals("")){
                    DisjointType="1";
                }
                String Bandwidth = e.elementText("Bandwidth");
                if(Bandwidth==null||Bandwidth.equals("")){
                    returnString[3]="Demand's Bandwidth is null!";
                    return returnString;
                }
                String PrimaryMustPassNodesSet = e.elementText("PrimaryMustPassNodesSet");
                if(PrimaryMustPassNodesSet == null||PrimaryMustPassNodesSet.equals("")){
                    PrimaryMustPassNodesSet="N";
                }
                String BackupMustPassNodesSet = e.elementText("BackupMustPassNodesSet");
                if(BackupMustPassNodesSet == null||BackupMustPassNodesSet.equals("")){
                    BackupMustPassNodesSet="N";
                }
                demandString.append(Source)
                .append("	")
                .append(Destination)
                .append("	")
                .append(DisjointType)
                .append("	")
                .append(Bandwidth)
                .append("	")
                .append(PrimaryMustPassNodesSet)
                .append("	")
                .append(BackupMustPassNodesSet)
                .append("\n");
            }
            returnString[0]=nodeString.toString();
            returnString[1]=linkString.toString();
            returnString[2]=demandString.toString();
            
        } catch (DocumentException e) {
            e.printStackTrace();
            returnString[3] = "Error!";
            return returnString; 
        }
        returnString[3] = "true";
        return returnString; 
    }
    
    /**
     *删除产生的文件
     */
    private void deleteFile(){
    	try{
    		File deleteFile = new File(inputNodeFile);
    		deleteFile.delete();
    		deleteFile = new File(inputLinkFile);
    		deleteFile.delete();
    		deleteFile = new File(inputDemandFile);
    		deleteFile.delete();
    		deleteFile = new File(outputResultsFile);
    		deleteFile.delete();
    		
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
//        String aaa="asdfasdfasdfasdfasdf";
//        String[] a=aaa.split("--");
//        //System.out.println(a.length);
        BeiAlgorithmService beiAlgorithm = new BeiAlgorithmService();
        String returnString = beiAlgorithm.getBeiAlgorithm(getXml());
        //System.out.println(returnString);
    }
    private static String getXml(){
//        return "<topodata><topo><toponodes><toponode><NodeID>1</NodeID><nodeType>TOR</nodeType><systemName>tor1</systemName></toponode><toponode><NodeID>2</NodeID><nodeType>AGG</nodeType><systemName>agg1</systemName></toponode><toponode><NodeID>3</NodeID><nodeType>CORE</nodeType><systemName>core1</systemName></toponode><toponode><NodeID>4</NodeID><nodeType>TOR</nodeType><systemName>tor2</systemName></toponode><toponode><NodeID>5</NodeID><nodeType>TOR</nodeType><systemName>tor3</systemName></toponode><toponode><NodeID>6</NodeID><nodeType>TOR</nodeType><systemName>tor4</systemName></toponode><toponode><NodeID>7</NodeID><nodeType>TOR</nodeType><systemName>tor5</systemName></toponode></toponodes><topoLinks><topoLink><Source>1</Source><Sink>2</Sink><cost>1</cost><Capacity>10</Capacity></topoLink><topoLink><Source>2</Source><Sink>4</Sink><cost>1</cost><Capacity>10</Capacity></topoLink><topoLink><Source>3</Source><Sink>4</Sink><cost>1</cost><Capacity>10</Capacity></topoLink><topoLink><Source>1</Source><Sink>3</Sink><cost>1</cost><Capacity>10</Capacity></topoLink><topoLink><Source>1</Source><Sink>4</Sink><cost>10</cost><Capacity>10</Capacity></topoLink><topoLink><Source>2</Source><Sink>3</Sink><cost>10</cost><Capacity>10</Capacity></topoLink><topoLink><Source>1</Source><Sink>5</Sink><cost>10</cost><Capacity>10</Capacity></topoLink><topoLink><Source>1</Source><Sink>6</Sink><cost>1</cost><Capacity>10</Capacity></topoLink><topoLink><Source>3</Source><Sink>5</Sink><cost>1</cost><Capacity>10</Capacity></topoLink><topoLink><Source>3</Source><Sink>6</Sink><cost>10</cost><Capacity>10</Capacity></topoLink><topoLink><Source>2</Source><Sink>7</Sink><cost>1</cost><Capacity>10</Capacity></topoLink><topoLink><Source>4</Source><Sink>7</Sink><cost>10</cost><Capacity>10</Capacity></topoLink></topoLinks></topo><userLinks><userLink><Source>1</Source><Destination>5</Destination><Bandwidth>10</Bandwidth></userLink><userLink><Source>2</Source><Destination>7</Destination><Bandwidth>10</Bandwidth></userLink></userLinks></topodata>";
        return "<topodata><topo><toponodes><toponode><NodeID>51</NodeID></toponode><toponode><NodeID>52</NodeID></toponode><toponode><NodeID>53</NodeID></toponode><toponode><NodeID>54</NodeID></toponode></toponodes><topoLinks><topoLink><Source>51</Source><Sink>53</Sink><Capacity>20</Capacity></topoLink><topoLink><Source>51</Source><Sink>54</Sink><Capacity>20</Capacity></topoLink><topoLink><Source>54</Source><Sink>53</Sink><Capacity>20</Capacity></topoLink><topoLink><Source>51</Source><Sink>52</Sink><Capacity>20</Capacity></topoLink><topoLink><Source>52</Source><Sink>53</Sink><Capacity>20</Capacity></topoLink></topoLinks></topo><demands><demand><Source>54</Source><Destination>52</Destination><Bandwidth>10</Bandwidth></demand></demands></topodata>";
    }
}
