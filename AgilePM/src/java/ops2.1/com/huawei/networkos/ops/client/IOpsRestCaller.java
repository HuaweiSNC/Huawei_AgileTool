package com.huawei.networkos.ops.client;

import com.huawei.networkos.ops.response.RetRpc;

public interface IOpsRestCaller {

	public static final String ACTION_GET = "GET";
	public static final String ACTION_POST = "POST";
	public static final String ACTION_PUT = "PUT";
	public static final String ACTION_DELETE = "DELETE";
	
	/***
	 * 获取get方法
	 * @param url
	 * @return
	 */
	public RetRpc get(String url);

	/****
	 * 获取post方法
	 * @param url
	 * @param data
	 * @return
	 */
	public RetRpc post(String url, String data);

	/***
	 * 获取put方法
	 * @param url
	 * @param data
	 * @return
	 */
	public RetRpc put(String url, String data);

	/****
	 * 获取delete方法
	 * @param url
	 * @return
	 */
	public RetRpc delete(String url);
	
	/***
	 * 获取restcall 方法
	 * @param path
	 * @param data
	 * @param action
	 * @return
	 */
	public RetRpc restcall(String path, String data, String action);
}
