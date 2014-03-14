package com.blackpointstraffic.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class JSONParser {
	public static JSONArray getJsonFromUrl(String url) {
		InputStream inputStream  = null;
		JSONArray jsonArr = null;
		
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			StatusLine statusLine = httpResponse.getStatusLine();

			// get json ok
			if (statusLine.getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null) {
					inputStream = httpEntity.getContent();
				}
			}

			// read json
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, HTTP.UTF_8), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			inputStream.close();

			jsonArr = new JSONArray(sb.toString());

		} catch (HttpHostConnectException e) {
			Log.e("HTTP ERROR", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e("UnsupportedEncoding", e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e("ClientProtocolException", e.getMessage());
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		} catch (JSONException e) {
			Log.e("JSON PARSER", "Error parsing data:" + e.getMessage());
		} catch (Exception e) {
			Log.e("ERROR", e.getMessage());
		}
		return jsonArr;
	}
}
