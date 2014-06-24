package p5_gyazo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import p5_gyazo.utils.PImageUtils;
import processing.core.PApplet;
import processing.core.PImage;

public class Gyazo implements Runnable{
	protected PApplet papplet;
	protected String gyazo_url;

	protected Thread thread;
	protected PImage pimage;
	
	protected Method method_finished;
	protected Method method_error;
	
	protected String id_str;
	protected static final String id_str_filename = "id.txt";
	
	public Gyazo(PApplet papplet) {
		this.papplet = papplet;
		this.gyazo_url = "http://gyazo.com/upload.cgi"; // default
		setupMethods();
		loadID();
	}
	
	public Gyazo(PApplet papplet, String url) {
		this.papplet = papplet;
		this.gyazo_url = url;
		setupMethods();
		loadID();
	}

	protected void setupMethods() {
		@SuppressWarnings("rawtypes")
		Class [] obj_args = new Class[1];
		obj_args[0] = String.class;

		try {
			method_finished = papplet.getClass().getMethod("onGyazoUploadFinished", obj_args);
		} catch (Exception e) {
		}
		
		try{
			method_error = papplet.getClass().getMethod("onGyazoUploadError", obj_args);
		} catch (Exception e) {
		}
	}
	
	public void upload() {
		if (papplet == null) {
			callOnGyazoUploadError("Gyazo : upload() : papplet is null...");		
			return;
		}

		upload(papplet.get());
	}
	
	public void upload(PImage pimage) {
		if (pimage == null) {
			callOnGyazoUploadError("Gyazo : upload() : image is null...");
			return;
		}
		
		synchronized(this) {
			if (thread != null) {
				callOnGyazoUploadError("Gyazo : Please wait until the end of the data upload.");
				return;
			}

			pimage.updatePixels();
			try {
				this.pimage = (PImage) pimage.clone();
			} catch (CloneNotSupportedException e) {
			}

			this.thread = new Thread(this);
			this.thread.start();
		}
	}
		
	@Override
	public void run() {
		byte[] png_data = PImageUtils.toPngByteArray(this.pimage);
		if (png_data == null) {
			callOnGyazoUploadError("Gyazo : PImageUtils.toPngByteArray() failed...");
			return;
		}

		InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(png_data), "image.png");
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(gyazo_url);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create(); 
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		if (id_str != null) {
			builder.addPart("id", new StringBody(id_str, ContentType.TEXT_PLAIN));
		}
		
		builder.addPart("imagedata", inputStreamBody); 
		
		post.setEntity(builder.build());

		HttpResponse res = null;
		try {
			res = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			callOnGyazoUploadError("Gyazo : ClientProtocolException");
			clearThread();
			return;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			callOnGyazoUploadError("Gyazo : UnknownHostException...gyazo_url=" + gyazo_url);
			clearThread();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			callOnGyazoUploadError("Gyazo : IOException...");
			clearThread();
			return;
		}

		if (res == null) {
			callOnGyazoUploadError("Gyazo : HTTP POST failed...");
			clearThread();
			return;
		}

		int status_code = res.getStatusLine().getStatusCode();
		if (status_code != 200) {
			callOnGyazoUploadError("Gyazo : HTTP POST failed...status_code=" + status_code);
			clearThread();
			return;
		}
		
		try {
			this.id_str = res.getHeaders("X-Gyazo-Id")[0].getValue();
			saveID();
		}
		catch(Exception e) {
			// nothing to do...
		}
		
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = null;
		try {
			body = handler.handleResponse(res);
		} catch (Exception e) {
			callOnGyazoUploadError("Gyazo : handler.handleResponse(res) failed...e=" + e.toString());
			clearThread();
			return;
		}
		
		callOnGyazoUploadFinished(body);
		
		clearThread();
	}

	protected void loadID() {
		if (this.papplet == null) {
			callOnGyazoUploadError("Gyazo : saveID() : papplet is null...");
			return;
		}
		
		String data_path = papplet.dataPath(id_str_filename);
		File f = new File(data_path);
		if (f.exists()) {			
			String [] l = papplet.loadStrings(data_path);
			if (l == null) return;		
			
			id_str = l[0];
			System.out.println("Gyazo : loading id...id_str=" + id_str);
		}
	}
	
	protected void saveID() {
		if (this.papplet == null) {
			callOnGyazoUploadError("Gyazo : saveID() : papplet is null...");
			return;
		}

		if (this.id_str == null) {
			callOnGyazoUploadError("Gyazo : saveID() : id_str is null...");
			return;
		}

		String data_path = papplet.dataPath(id_str_filename);

		String [] l = new String[1];
		l[0] = this.id_str;
		papplet.saveStrings(data_path, l);
		
		System.out.println("Gyazo : saved new id...id_str=" + id_str);
	}
	
	protected void clearThread() {
		synchronized(this) {
			thread = null;
			pimage = null;
		}
	}
	
	protected void callOnGyazoUploadFinished(String result_url) {
		System.out.println("Gyazo.callOnGyazoUploadFinished() : image upload success...url=" + result_url);
		
		if (papplet == null) callOnGyazoUploadError("papplet is null...");		
		
		if (method_finished != null) {
			Object [] args = new Object[1];
			args[0] = result_url;
			try {
				method_finished.invoke(papplet, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void callOnGyazoUploadError(String message) {
		System.err.println("Gyazo.callOnGyazoUploadError() : message = " + message);
		if (method_error != null) {
			Object [] args = new Object[1];
			args[0] = message;
			try {
				method_error.invoke(papplet, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
