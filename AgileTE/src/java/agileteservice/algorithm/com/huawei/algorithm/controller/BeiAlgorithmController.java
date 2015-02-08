package com.huawei.algorithm.controller;

import com.huawei.agilete.base.common.MyIO;
import com.huawei.algorithm.service.BeiAlgorithmService;
import com.huawei.networkos.ops.response.RetRpc;

public class BeiAlgorithmController {

    BeiAlgorithmService BeiAlgorithmService = new BeiAlgorithmService();
    
    public RetRpc getBeiAlgorithm(String xml){
        try{
            xml = MyIO.characterFormat(xml);
            xml = xml.replace(" ", "");
        }catch (Exception e) {
        }
        
        RetRpc retRpc = new RetRpc();
        String returnxml = BeiAlgorithmService.getBeiAlgorithm(xml);
        retRpc.setStatusCode(200);
        retRpc.setContent(returnxml);
        return retRpc;
    }
}
