package com.huawei.agilete.northinterface.bean;

public class MappingPort {
    private int mappingVid = -1;
    private int mappingVidOld = -1;
    private String outerVlansNew = "";
    private String outerVlansOld = ""; 
    private String internalVlansNew = "";
    private String internalVlansOld = "";
    
    public MappingPort(){
        
    }
    public int getMappingVid() {
        return mappingVid;
    }
    public void setMappingVid(int mappingVid) {
        this.mappingVid = mappingVid;
    }
    public int getMappingVidOld() {
        return mappingVidOld;
    }
    public void setMappingVidOld(int mappingVidOld) {
        this.mappingVidOld = mappingVidOld;
    }
    public String getOuterVlansNew() {
        return outerVlansNew;
    }
    public void setOuterVlansNew(String outerVlansNew) {
        this.outerVlansNew = outerVlansNew;
    }
    public String getOuterVlansOld() {
        return outerVlansOld;
    }
    public void setOuterVlansOld(String outerVlansOld) {
        this.outerVlansOld = outerVlansOld;
    }
    public String getInternalVlansNew() {
        return internalVlansNew;
    }
    public void setInternalVlansNew(String internalVlansNew) {
        this.internalVlansNew = internalVlansNew;
    }
    public String getInternalVlansOld() {
        return internalVlansOld;
    }
    public void setInternalVlansOld(String internalVlansOld) {
        this.internalVlansOld = internalVlansOld;
    }
}
