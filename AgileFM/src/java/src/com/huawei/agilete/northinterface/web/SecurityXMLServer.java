package com.huawei.agilete.northinterface.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SecurityXMLServer implements Runnable {

	private ServerSocket server;
	private BufferedReader reader;
	private BufferedWriter writer;
	private static final String xml = "<cross-domain-policy> "
			+ "<allow-access-from domain=\"*\" to-ports=\"1025-9999\"/>"
			+ "</cross-domain-policy> ";
	private int port = 843;

	public SecurityXMLServer() {

	}

	public SecurityXMLServer(int port) {

		this.port = port;

	}

	/***
	 * 绑定服务器
	 * @return
	 */
	public Boolean createServerSocket() {
		
		Boolean result = true;
		try {
			server = new ServerSocket(this.port);
			
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	// 启动服务器线程
	public void run() {
		
		
		while (true) {
			Socket client = null;
			try {
				// 接收客户端的连接
				client = server.accept();

				InputStreamReader input = new InputStreamReader(
						client.getInputStream(), "UTF-8");
				reader = new BufferedReader(input);
				OutputStreamWriter output = new OutputStreamWriter(
						client.getOutputStream(), "UTF-8");
				writer = new BufferedWriter(output);

				// 读取客户端发送的数据
				StringBuilder data = new StringBuilder();
				int c = 0;
				while ((c = reader.read()) != -1) {
					if (c != '\0')
						data.append((char) c);
					else
						break;
				}
				String info = data.toString();

				// 接收到客户端的请求之后，将策略文件发送出去
				if (info.indexOf("<policy-file-request/>") >= 0) {
					writer.write(xml + "\0");
					writer.flush();
				} else {
					writer.write("Can not dete\0");
					writer.flush();
				}
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					// 发现异常关闭连接
					if (client != null) {
						client.close();
						client = null;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					// 调用垃圾收集方法
					System.gc();
				}
			}
		}
	}

}
