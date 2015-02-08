package com.huawei.networkos.templet;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * 根据模板和我相应的信息生成对应的模板文件
 *
 */
public class VMTempletManager {

	private static VMTempletManager manager = null;
	private VMTempletManager() {
		
	}
	
	public synchronized static VMTempletManager getInstance()
	{
		if (null == manager)
		{
			manager = new VMTempletManager();
		}
		return manager;
	}

	/****
	 * 开始生成模板文件并返回一个模板文件的数据流
	 * @param temlateFile
	 * @param mapContext
	 * @param templetPath
	 * @return
	 */
	public StringWriter process(String temlateFile,
			Map<String, Object> mapContext, String templetPath) {
		
		Properties properties = new Properties();
		if (StringUtils.isNotBlank(templetPath)) {
			properties.put(Velocity.FILE_RESOURCE_LOADER_PATH, templetPath);
			Velocity.init(properties);
		}

		VelocityContext context = new VelocityContext();
		for (String key : mapContext.keySet()) {
			context.put(key, mapContext.get(key));
		}

		Template template = null;
		template = Velocity.getTemplate(temlateFile);

		StringWriter out = new StringWriter();
		template.merge(context, out);
		out.flush();
		
		return out;
	}
	
	public String getResource(String resourcePath)
	{
		URL resourceDir = getClass().getResource(resourcePath);
		String resourceFileName = null;
		try {
			resourceFileName = resourceDir.toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		File file = new File(resourceFileName);

		return file.getAbsolutePath();
	}
	
	
}
