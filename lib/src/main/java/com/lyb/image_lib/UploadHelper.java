package com.lyb.image_lib;


import android.util.Log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 图片上传公共基类
 * @author liangyanbin
 *
 */
public abstract class UploadHelper {

	private static final String TAG = "UpLoadHelper";
	
	
	public abstract void uploadSuccess(String response);
	public abstract void uploadFail();
	public abstract void uploadImgProgress(long progress);
	
	FileUploadMultipartRequestEntity.ProgressListener listener = new FileUploadMultipartRequestEntity.ProgressListener() {

		private long totalImgSize;

		@Override
		public void uploadProgress(long progress) {
			// TODO Auto-generated method stub
			uploadImgProgress((int)(progress * 100 / totalImgSize));
		}

		@Override
		public void size(long totleSize) {
			// TODO Auto-generated method stub
			totalImgSize = totleSize;
		}

		@Override
		public void sucess(String response) {
			// TODO Auto-generated method stub
			uploadSuccess(response);
		}

		@Override
		public void fail() {
			// TODO Auto-generated method stub
			uploadFail();
		}
		
	};

	public void upload(String uploadUrl,
                       File file, String charset) {
		Part[] parts = toParts(file ,charset);
		upload(uploadUrl, parts, file, charset);
	}
	
	/**
	 * 上传关键逻辑 - 参数part[]
	 * 
	 * @param uploadUrl
	 * @param parts
	 * @param file
	 * @param charset
	 */	
	public void upload(String uploadUrl, Part[] parts,
                       File file, String charset) {
		PostMethod filePost = new PostMethod(uploadUrl);
		Log.v(TAG, "uploadUrl = " + uploadUrl);
		try {
			long totalBytes = 0l;
			if (null != file) {
				totalBytes = file.length();
			}
			listener.size(totalBytes);
			filePost.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			filePost.setRequestEntity(new FileUploadMultipartRequestEntity(
					parts, filePost.getParams(), listener));
			HttpClient httpClient = new HttpClient();
			httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(5000);

			for (int i = 0; i < 2; i++) {
				int status = httpClient.executeMethod(filePost);
				Log.v("uploadload", "status = " + status);
				if (status == HttpStatus.SC_OK) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(
									filePost.getResponseBodyAsStream()));
					StringBuffer stringBuffer = new StringBuffer();
					String str = "";
					while ((str = reader.readLine()) != null) {
						stringBuffer.append(str);
					}
					String ts = stringBuffer.toString();
					Log.v(TAG, "ts = " + ts);
					JSONObject jsonObject = new JSONObject(ts);
					int code = jsonObject.getInt("id");
					if (code == 1) {
						listener.sucess(ts);
						return;
					}

				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			filePost.releaseConnection();
		}
		listener.fail();
	}

	private Part[] toParts(File file, String charset){
		Part[] parts = null;
		try {
			ArrayList<Part> arrPart = new ArrayList<Part>();
			arrPart.add(new FilePart("file", file));
			parts = arrPart.toArray(new Part[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parts;
	}
	
	
	private Part[] hashToStringParts(HashMap map, File file, String charset){
		Part[] parts = null;
		try {
			Iterator iter = map.entrySet().iterator();
			parts = new Part[map.size()];
			ArrayList<Part> arrPart = new ArrayList<Part>();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
						.next();
				String key = entry.getKey();
				String val = entry.getValue();
				if(key.equals("ticket")){					
					arrPart.add(new StringPart("ticket", val, "GBK"));
				}else{
					arrPart.add(new StringPart(key, val, charset));
				}				
			}

			arrPart.add(new FilePart("file", file));

			parts = arrPart.toArray(new Part[map.size()]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parts;
	}
	
	

}
