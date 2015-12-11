package com.shx.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection =null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream inputStream = connection.getInputStream();
					String response = getResponseData(inputStream);
					if (listener!=null) {
						//回调onFinish方法
						listener.onFinish(response);
					}
				} catch (Exception e) {
					if (listener!=null) {
						//回调onError方法
						listener.onError(e);
					}
//					e.printStackTrace();
				}finally{
					if (connection!=null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	public static String getResponseData(InputStream in){
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line=null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
