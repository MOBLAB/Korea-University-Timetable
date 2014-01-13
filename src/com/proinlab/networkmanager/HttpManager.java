package com.proinlab.networkmanager;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpManager {

	/**
	 * POST 방식으로 전송하여 받은 데이터를 반환한다
	 * 
	 * @param url
	 *            : POST 방식으로 전송할 경로
	 * @param PostData
	 *            : ArrayList<ArrayList<String>> 내부 ArrayList .................
	 *            . 0: Parameter, 1: Value .....................................
	 * @param incoding
	 *            : HTTP.values
	 * @return 받은 데이터 String
	 */
	public String POST_DATA(DefaultHttpClient httpclient, String url,
			ArrayList<ArrayList<String>> PostData, String incoding) {

		String loginsource = null;
		ResponseHandler<String> responsehandler = new BasicResponseHandler();
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		for (int i = 0; i < PostData.size(); i++)
			nvps.add(new BasicNameValuePair(PostData.get(i).get(0), PostData
					.get(i).get(1)));

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, incoding));
			loginsource = httpclient.execute(httpost, responsehandler);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return loginsource;
	}

	/**
	 * POST 방식으로 전송하여 받은 데이터를 반환한다
	 * 
	 * @param addr
	 * @param incoding
	 *            : HTTP.values
	 * @return String
	 */
	public String GET_DATA(DefaultHttpClient httpclient, String addr,
			String incoding) {
		String htmlSource = null;
		try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(addr));
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			htmlSource = EntityUtils.toString(entity, incoding);
		} catch (Exception e) {
		}
		return htmlSource;
	}
}
