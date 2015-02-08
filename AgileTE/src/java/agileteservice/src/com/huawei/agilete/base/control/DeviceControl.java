package com.huawei.agilete.base.control;

 

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

import com.huawei.agilete.base.bean.Device;

public final class DeviceControl {

    private static DeviceControl deviceManager = null;
    private Map<Integer, Device> devices = new LinkedHashMap<Integer, Device>();
    
    private DeviceControl()
    {
 
    }
    
    /***
     * 获取设备管理类的实例
     * @return
     */
    public static synchronized DeviceControl getInstance()
    {
        if (null == deviceManager)
        {
            deviceManager = new DeviceControl();
        }
        return deviceManager;
    }
    
    /***
     * 实发始化设备连接
     */
    public void initConnectDevice()
    {
        
        
        
    }
    
    /***
     * 获取device处理类
     * @param deviceId
     * @return
     */
    public Device getDevice(String strDeviceId)
    {
        if (strDeviceId == null || strDeviceId.length() <= 0)
        {
            return null;
        }
        
        Integer deviceId = null;
        try {
            deviceId = Integer.valueOf(strDeviceId);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return getDevice(deviceId);    
    }
    
    /***
     * 获取device处理类
     * @param deviceId
     * @return
     */
    public Device getDevice(int deviceId)
    {
        Device retDevice = null; 
        retDevice = devices.get(deviceId);
        return retDevice;    
    }
    
    /***
     * 增加device设备 
     * @param item
     */
    public void addDevice(Device device)
    {
        //Device device = new Device(item);
        if (device != null)
        {
            //device.initConnect();
            this.devices.put(device.getId(), device);
        }
    }
    
    /***
     * 修改deivce设备 
     * @param item
     */
    public void modifyDevice(Device item)
    {
        Integer id = item.getId();
        delDevice(id);
        addDevice(item);
    }
    
    /***
     * 删除设备
     * @param id
     */
    public void delDevice(Integer id)
    {
        Device item = devices.remove(id);
        if (item != null)
        {
            item = null;
        }
         
    }
    
    /***
     * 判断设备的连接是否正常 
     */
    public void detectConnect()
    {
        
        /*Device item = null;
        while (true)
        {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            for(Integer itemId : devices.keySet())
            {
                item = devices.get(itemId);
                if (item != null)
                {
                    item.detectConnect();
                }
            }
        }*/
        
    }
    
    /***
     * 获取指定设备ID的设备描述信息
     * @param devid 输入设备 的ID号
     * @return 设备的描述信息
     */
    public JSONObject getDeviceInfoById(int devid)
    {
        /*Device item = devices.get(devid);
        if (item == null)
        {
            return null;
            
        }
        
        String deviceStatus = "normal";
        if (item.getStatus() == false)
        {
            deviceStatus = "sick";
        }
        
        DeviceBean bean = item.getDeviceBean();
        
        JSONObject jsonArray = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            jsonArray.put("device", json);
            json.put("id", bean.getId());
            json.put("status", deviceStatus);
            json.put("devicename", bean.getDevicename());
            json.put("connectstatus", item.getConnectStatus());
            json.put("ip", bean.getIp());
            json.put("username", bean.getUsername());
            json.put("passwd", "");
            json.put("productType", bean.getProductType());
            json.put("version", bean.getVersion());
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
        return null;
    }
    
    /***
     * 获取所有的设备信息
     * @return 取得设备信息 
     */
    public JSONObject getAllDeviceInfo()
    {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonroot = new JSONObject();
        JSONObject jsonOne = null;
    /*    
        try {
            jsonroot.put("devices", jsonArray);
            
            for(Integer itemId : devices.keySet())
            {
                jsonOne = getDeviceInfoById(itemId);
                if (jsonOne != null)
                {
                    jsonArray.add(jsonOne);
                }
                 
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        return jsonroot;
    }
    
    /***
     * 获取当前设备信息
     * @return 
     */
    public List<Device> getAllDevices()
    {
        List<Device> lstDeviceBean = new ArrayList<Device> ();
         
        Device bean = null;
        for(Integer key : devices.keySet())
        {
            bean = devices.get(key);
            if (null != bean)
            {
                lstDeviceBean.add(bean);
            }
        }
        
        return lstDeviceBean;
    }

    
}
