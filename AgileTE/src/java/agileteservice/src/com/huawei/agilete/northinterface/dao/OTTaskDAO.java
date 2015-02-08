package com.huawei.agilete.northinterface.dao;

import java.util.ArrayList;
import java.util.List;

import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTTask;
import com.huawei.agilete.task.TaskAllNet;
import com.huawei.agilete.task.TaskFlux;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.networkos.ops.util.threadpool.WorkerTaskBean;
import com.huawei.networkos.ops.util.threadpool.WorkerThread;


public final class OTTaskDAO  implements IOTDao{

    private static OTTaskDAO single = null;
    private OpsRestCaller client = null;
    public String domainId;
    private OTTaskDAO(){

    }
    public synchronized  static OTTaskDAO getInstance() {
        if (single == null) {  
            single = new OTTaskDAO();
        }  
        return single;
    }

     public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
            RetRpc result = new RetRpc();
            this.domainId = domainId;
            client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
        }else if(restType.equals(MyData.RestType.DELETE)){
            result  = del(apiPath);
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else if(restType.equals(MyData.RestType.PUT)){
            result  = edit(content);
        }else{

        }

        return result;
    }
         
    public RetRpc add(String content){
//        MyData.TaskMainControl = new BuildController();
//        MyData.TaskMainControl.resume();
//        
//        MyData.TaskChildControl = new BuildController();
//        MyData.TaskChildControl.resume();
        
        RetRpc result = new RetRpc();
        OTTask oTTask = new OTTask(content);
        if(oTTask.getType().contains("nqa_")){
            String[] type = oTTask.getType().split("_");
            TaskAllNet wrapper = new TaskAllNet(domainId);
            long t = 10;
            try{
                t = Long.parseLong(oTTask.getTime());
            }catch(Exception e){
            }
            wrapper.setInterval(t);
            wrapper.setTitle("NQA"+"_"+domainId+"_"+type[1]); //NQA CE12800_A TUNNEL1
            //wrapper.body = OTNqaDAO.getInstance().getOpsBody(type[1], oTTask.getBody(),wrapper.getTitle());
            wrapper.setoTTask(oTTask);
            wrapper.type = type[1];
            MyData.TaskMainControl.addTask(wrapper);
            result.setStatusCode(200);
            result.setContent("true");
        }else if(oTTask.getType().contains("flux_")){
            String[] type = oTTask.getType().split("_");
            TaskFlux wrapper = new TaskFlux(domainId);
            long t = 1000;
            try{
                t = Long.parseLong(oTTask.getTime());
            }catch(Exception e){
            }
            wrapper.setInterval(t);
            wrapper.setTitle("FLUX"+"_"+domainId+"_"+type[1]); //NQA CE12800_A TUNNEL1
            //wrapper.body = OTNqaDAO.getInstance().getOpsBody(type[1], oTTask.getBody(),wrapper.getTitle());
            wrapper.setoTTask(oTTask);
            wrapper.type = type[1];
            MyData.TaskMainControl.addTask(wrapper);
            result.setStatusCode(200);
            result.setContent("true");
        }else{
            result.setStatusCode(403);
            result.setContent("resolve type failure");
        }
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc();


        return result;
    }

    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        String taskTitle = "";
        if(apiPath.contains("nqa_")){
            String[] type = apiPath.split("_");
            taskTitle = "NQA"+"_"+domainId+"_"+type[1];
        }else if(apiPath.contains("flux_")){
            String[] type = apiPath.split("_");
            taskTitle = "FLUX"+"_"+domainId+"_"+type[1];
        }else{
            result.setStatusCode(403);
            result.setContent("resolve type failure");
        }
        Boolean flag = MyData.TaskMainControl.removeTask(domainId, taskTitle);
        result.setContent(String.valueOf(flag));
        return result;
    }

    public RetRpc get(String apiPath){
        RetRpc opsresult = new RetRpc();
        if(null != MyData.TaskMainControl){
            List<WorkerThread> list = MyData.TaskMainControl.getTasks();
            List<OTTask> taskList = new ArrayList<OTTask>();
            for(WorkerThread wt : list){
            	
          
            	if (wt instanceof TaskFlux)
            	{
                    OTTask task = ((TaskFlux) wt).getoTTask();
                    task.setState("true");
                    taskList.add(task);
            	}
            	if (wt instanceof TaskAllNet)
            	{
            	      OTTask task = ((TaskAllNet) wt).getoTTask();
                      task.setState("true");
                      taskList.add(task);
            	}
   
            }
            OTTask oTTask = new OTTask();
            String flag = oTTask.getUiMessage(taskList);
            opsresult.setContent(flag);
        }else{
            opsresult.setStatusCode(500);
        }
        return opsresult;
    }
    
    

    public static void main(String[] args) {
//        String content = "<tasks><task><sc><name>flux-tunnel</name><time></time><type>flux-tunnel</type></sc><request></request></task></tasks>";
//        OTTaskDAO.getInstance().control("2", "60", "", MyData.RestType.POST, content);
    
    }
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }
    


}
