package com.huawei.agilete.northinterface.beanfactory;

import java.util.LinkedHashMap;
import java.util.Map;

import com.huawei.agilete.northinterface.dao.IOTDao;
import com.huawei.agilete.northinterface.dao.OTFlowDAO;
import com.huawei.agilete.northinterface.dao.OTFluxDAO;
import com.huawei.agilete.northinterface.dao.OTFluxNowDAO;
import com.huawei.agilete.northinterface.dao.OTIfmDAO;
import com.huawei.agilete.northinterface.dao.OTNqaDAO;
import com.huawei.agilete.northinterface.dao.OTNqaNowDAO;
import com.huawei.agilete.northinterface.dao.OTPolicyDAO;
import com.huawei.agilete.northinterface.dao.OTTfDAO;
import com.huawei.agilete.northinterface.dao.OTTunnelDAO;

public class OTDaoFactory {

	private static final Map<String , IOTDao> daoMap = new LinkedHashMap<String, IOTDao>();

	static {
		daoMap.put("policys", OTPolicyDAO.getInstance());
		daoMap.put("tunnels", OTTunnelDAO.getInstance());
		daoMap.put("ifms", OTIfmDAO.getInstance());
		daoMap.put("flows", OTFlowDAO.getInstance());
		daoMap.put("nqa", OTNqaDAO.getInstance());
		daoMap.put("nqasoon", OTNqaNowDAO.getInstance());
		daoMap.put("tfs", OTTfDAO.getInstance());
		daoMap.put("flux", OTFluxDAO.getInstance());
		daoMap.put("fluxsoon", OTFluxNowDAO.getInstance());
	}

	public static synchronized  IOTDao getRestClient(String serviceType)
	{
		IOTDao dao = daoMap.get(serviceType);
		return dao;
	}



}
