package com.huawei.plugins.rrdtool.manage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;

public final class RrdTool {

    private static RrdTool single = null;
    private RrdTool(){

    }
    public synchronized  static RrdTool getInstance() {
        if (single == null) {  
            single = new RrdTool();
        }  
        return single;
    }

    //    rrdtool create test.rrd  --start 1386147058  --step 3  DS:Bandwidth:COUNTER:600:U:U  RRA:AVERAGE:0.5:1:24
    //    
    //    rrdtool update test.rrd 1386147060:9376074752 1386147063:9376074852 1386147066:9376074952 
    //    
    //    rrdtool fetch test.rrd AVERAGE --start 1386147060  --end 1386147072
    private String rrdExe;
    private String rrdInput;
    public String sGraphTime;

    /**
     * @param rrdTime second
     */
    public void createRrd(String rrdName,String rrdTime){
        sGraphTime = rrdTime;
        rrdName =getRrdInput()+rrdName+".rrd";
        rrdName = "\""+rrdName+"\"";
        runRrd("rrdtool create "+rrdName+" --start "+rrdTime+"  --step 3  DS:Bandwidth:COUNTER:600:U:U  RRA:AVERAGE:0.5:1:28800");
    }

    public void updateRrd(String rrdName,String rrdTime,String content){
        rrdName =getRrdInput()+rrdName+".rrd";
        runRrd("rrdtool update \""+rrdName+"\" "+rrdTime+":"+content);
    }

    public String fetchRrd(String rrdName,String startTime,String endTime){
        String result = "";
        rrdName =getRrdInput()+rrdName+".rrd";
        rrdName = "\""+rrdName+"\"";
        result = runRrd("rrdtool fetch "+rrdName+" "+ "AVERAGE --start "+startTime+"  --end "+endTime);
        return result;
    }

    public String updateFetchRrd(String rrdName,String rrdTime,String content){
        long rTime = Long.parseLong(rrdTime)/1000;
        rrdTime = String.format("%d", rTime);

        String result = "";
        //rrdName = System.getProperty("user.dir")+"\\conf\\log\\"+rrdName+".rrd";
        File file = new File(getRrdInput()+rrdName+".rrd");
        if(!file.exists()){
            createRrd(rrdName, rrdTime);
        }
        if(null == sGraphTime){
            sGraphTime = rrdTime;
        }

        updateRrd(rrdName, rrdTime, content);
        long time = Long.valueOf(rrdTime);
        time = time-5;
        rrdTime = String.format("%d", time);

        result = fetchRrd(rrdName, rrdTime, rrdTime);
        String rrdBandwidth = "";
        if(true && !"".equals(result)){
            result = result.replace(" ", "");
            try{
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(Long.valueOf(result.split(":")[0]+"000"));
                if("-1.#IND000000e+000".equals(result.split(":")[1])){
                    rrdBandwidth = "NA";
                }else if ("0.0000000000e+000".equals(result.split(":")[1])){
                    rrdBandwidth = "0";
                }else{
                    String flag = result.split(":")[1].replace(".", "");
                    int flagLength = flag.charAt(15)-48;//Integer.valueOf(String.format("%c", flag.charAt(15)));
                    rrdBandwidth = String.format("%d", Long.valueOf(flag.substring(0, flagLength+1))*8);
                }
            }catch(Exception e){
                rrdBandwidth = "NA";
            }
        }else{
            rrdBandwidth = "NA";
        }
        //graphRrd(rrdName);
        return rrdBandwidth;
    }


    public void graphRrd(String rrdName){
        //rrdtool graph test3.png  --start 1386292325 --end 1386292355  --vertical-label  DEF:myspeed=test3.rrd:Bandwidth:AVERAGE  LINE2:myspeed#FF0000
        if(Calendar.getInstance().getTimeInMillis()-Long.valueOf(sGraphTime)*1000>15000/*24*60*60*1000*/){
            String result = "";
            String ctime = String.format("%d", Calendar.getInstance().getTimeInMillis()/1000);
            String rrdPngName = rrdName+"_"+sGraphTime+"-"+ctime+".png";
            rrdName = System.getProperty("user.dir")+"\\conf\\log\\"+rrdName;

            result = runRrd(getRrdExe()+" graph "+rrdPngName+" "+ " --start "+sGraphTime+"  --end "+ctime+" --vertical-label  DEF:myspeed="+rrdName+".rrd:Bandwidth:AVERAGE  LINE2:myspeed#FF0000");
            sGraphTime = ctime;

        }
    }

    public String runRrd(String sentence){
        String result = "";
        Runtime rt = Runtime.getRuntime();
        try {
            Process process = rt.exec("cmd");
            BufferedOutputStream out = new BufferedOutputStream(process.getOutputStream());
            BufferedInputStream in=new BufferedInputStream(process.getInputStream()); 
            out.write(getRrdExe().getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.write(sentence.getBytes());
            out.write("\r\n".getBytes());
            out.flush();
            out.close();
            BufferedReader brz=new BufferedReader(new InputStreamReader(in));
            String linez=null;  
            HashMap<Integer,String> line = new HashMap<Integer,String>();
            int lineCount = 0;
            while((linez=brz.readLine())!=null){  
                //                //System.out.println(linez);  
                line.put(lineCount++, linez);
            } 
            result = line.get(line.size()-3);

            process.destroy();

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return result;
    }

    public String runRrd2(String sentence){
        String result = "";

        StringBuffer cmd = new StringBuffer();
        cmd.append("cmd.exe /c ");
        cmd.append("\"");
        cmd.append(getRrdExe());
        cmd.append(sentence);
        cmd.append("\"");
        //        if(!sentence.contains("fetch")){
        //        //System.out.println(sentence);
        //        }
        Runtime run = Runtime.getRuntime();
        try {
            //Process p =run.exec("cmd.exe /c \"d:/rrdtool.exe fetch d:/test.rrd AVERAGE --start 920804400 --end 920805000\"");
            Process p =run.exec(cmd.toString());
            BufferedReader bread = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = bread.readLine();
            while(bread.readLine() != null){
                result = bread.readLine();
                ////System.out.println(result);
            }
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;



    }



    public String getRrdExe() {
        if(null == rrdExe || "".equals(rrdExe)){
            String path = this.getClass().getResource("").getPath();
            path = path.substring(1).replace("%20", " ");
            path = path.replace("/manage/", "/tool/");
            rrdExe = "cd \""+path+"\"";
        }
        return rrdExe;
    }
    public void setRrdExe(String rrdExe) {
        this.rrdExe = rrdExe;
    }
    public String getRrdInput() {
        if(null == rrdInput || "".equals(rrdInput)){
            String path = this.getClass().getResource("").getPath();
            path = path.substring(1).replace("%20", " ");
            path = path.replace("/manage/", "/input/");
            rrdInput = path;
        }
        return rrdInput;
    }
    public void setRrdInput(String rrdInput) {
        this.rrdInput = rrdInput;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        //        RrdTool.getInstance().getRrdInput();
        //        RrdTool.getInstance().getRrdExe();
        //        //System.out.println(System.currentTimeMillis());
        String aa =RrdTool.getInstance().updateFetchRrd("ifmflux", String.valueOf(System.currentTimeMillis()), "38");
        //System.out.println("rrdtool="+aa);


    }

}
