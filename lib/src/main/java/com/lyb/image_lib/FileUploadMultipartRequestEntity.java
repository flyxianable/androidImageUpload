package com.lyb.image_lib;

import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUploadMultipartRequestEntity extends MultipartRequestEntity {

	private ProgressListener listener;
	
	public FileUploadMultipartRequestEntity(Part[] parts,
                                            HttpMethodParams params, ProgressListener listener) {
		super(parts, params);
		this.listener = listener;
	}

	@Override
	public void writeRequest(OutputStream out) throws IOException {
		super.writeRequest(new FileListenerOutputStream(out, listener));
	}
	
	public interface ProgressListener{
		void uploadProgress(long progress);
		void size(long totleSize);
		void sucess(String response);
		void fail();
	}
	
	class FileListenerOutputStream extends FilterOutputStream {

		private ProgressListener listener;
		private long currProgress;
		
		
		public FileListenerOutputStream(OutputStream out, ProgressListener listener) {
			super(out);
			this.listener = listener;
		}
		
		@Override
		public void write(byte[] buffer, int offset, int length) throws IOException {
			out.write(buffer, offset, length);
			currProgress+=length;
			listener.uploadProgress(currProgress);
		}

		@Override
		public void write(int oneByte) throws IOException {
			out.write(oneByte);
			currProgress++;
			listener.uploadProgress(currProgress);
		}

		
	}
	
	

}
