package com.huawei.agilete.northinterface.bean;


public class OTTunnelPath {

/*    <pathType>hot_standby</pathType>
    <pathName>backup</pathName>
    <lspState>down</lspState>*/
    
    private String pathType = "";
    private String pathName = "";
    private String id = "";
    private String lspState = "";
    
    public OTTunnelPath(){
        
    }
    
    public String getPathType() {
        return pathType;
    }
    public void setPathType(String pathType) {
        this.pathType = pathType;
    }
    public String getPathName() {
        return pathName;
    }
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
    public String getLspState() {
        return lspState;
    }
    public void setLspState(String lspState) {
        this.lspState = lspState;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
}
