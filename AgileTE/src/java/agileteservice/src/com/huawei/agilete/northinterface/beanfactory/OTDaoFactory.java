package com.huawei.agilete.northinterface.beanfactory;

import java.util.LinkedHashMap;
import java.util.Map;

import com.huawei.agilete.northinterface.dao.IOTDao;
import com.huawei.agilete.northinterface.dao.OTBfdDAO;
import com.huawei.agilete.northinterface.dao.OTFlowDAO;
import com.huawei.agilete.northinterface.dao.OTFluxDAO;
import com.huawei.agilete.northinterface.dao.OTFluxNowDAO;
import com.huawei.agilete.northinterface.dao.OTHandPolicyDAO;
import com.huawei.agilete.northinterface.dao.OTIfmDAO;
import com.huawei.agilete.northinterface.dao.OTLinkDAO;
import com.huawei.agilete.northinterface.dao.OTMainStandbyDAO;
import com.huawei.agilete.northinterface.dao.OTNqaDAO;
import com.huawei.agilete.northinterface.dao.OTNqaNowDAO;
import com.huawei.agilete.northinterface.dao.OTPolicyDAO;
import com.huawei.agilete.northinterface.dao.OTQosDAO;
import com.huawei.agilete.northinterface.dao.OTQosVlanDsDAO;
import com.huawei.agilete.northinterface.dao.OTRestPingDAO;
import com.huawei.agilete.northinterface.dao.OTTaskDAO;
import com.huawei.agilete.northinterface.dao.OTTfDAO;
import com.huawei.agilete.northinterface.dao.OTTopoLocationDAO;
import com.huawei.agilete.northinterface.dao.OTTunnelDAO;
import com.huawei.agilete.northinterface.dao.OTVlanDAO;
import com.huawei.agilete.northinterface.dao.OTVlanMapping1To1DAO;
import com.huawei.agilete.northinterface.dao.OTVlanMappingDAO;
import com.huawei.agilete.northinterface.dao.OTVlanRangeDAO;
import com.huawei.agilete.northinterface.dao.OTWtrDAO;

public class OTDaoFactory {

    private static final Map<String , IOTDao> daoMap = new LinkedHashMap<String, IOTDao>();

    static {
        daoMap.put("policys", OTPolicyDAO.getInstance());
        daoMap.put("tunnels", OTTunnelDAO.getInstance());
        daoMap.put("ifms", OTIfmDAO.getInstance());
        daoMap.put("flows", OTFlowDAO.getInstance());
        daoMap.put("vlans", OTVlanDAO.getInstance());
        daoMap.put("vlanmappings", OTVlanMappingDAO.getInstance());
        daoMap.put("vlanmappings1To1", OTVlanMapping1To1DAO.getInstance());
        daoMap.put("mainStandby", OTMainStandbyDAO.getInstance());
        daoMap.put("tunnelpolicyhand", OTHandPolicyDAO.getInstance());
        daoMap.put("bfds", OTBfdDAO.getInstance());
        daoMap.put("qosVlanDs", OTQosVlanDsDAO.getInstance());
        daoMap.put("qoss", OTQosDAO.getInstance());
        daoMap.put("tasks", OTTaskDAO.getInstance());
        daoMap.put("nqa", OTNqaDAO.getInstance());
        daoMap.put("nqasoon", OTNqaNowDAO.getInstance());
        daoMap.put("deviceping", OTRestPingDAO.getInstance());
        daoMap.put("links", OTLinkDAO.getInstance());
        daoMap.put("tfs", OTTfDAO.getInstance());
        daoMap.put("topolocation", OTTopoLocationDAO.getInstance());
        daoMap.put("flux", OTFluxDAO.getInstance());
        daoMap.put("fluxsoon", OTFluxNowDAO.getInstance());
        daoMap.put("wtrs", OTWtrDAO.getInstance());
        daoMap.put("vlanranges", OTVlanRangeDAO.getInstance());
    }

    public static synchronized  IOTDao getRestClient(String serviceType)
    {
        IOTDao dao = daoMap.get(serviceType);
        return dao;
    }



}
