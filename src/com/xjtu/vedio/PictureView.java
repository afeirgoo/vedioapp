package com.xjtu.vedio;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

public class PictureView extends Activity {
	private int ANDROID_ACCESS_CXF_WEBSERVICES = 001; 
	String imageUrl = "http://113.200.115.170:4101/VedioServer/rest/restService/download/";  
	 Bitmap bmImg;  
	 ImageView imView; 
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picview);//把Activity和布局文件xml绑定。
		imView = (ImageView) findViewById(R.id.imageView);  
		//bmImg = returnBitMap(imageUrl);
        
		Thread accessWebServiceThread = new Thread(new WebServiceHandler());
        accessWebServiceThread.start();
        
        
	}
	class WebServiceHandler implements Runnable{
	    @Override
	    public void run() {
	      Looper.prepare();
	      imageUrl += "567443/";
	      imageUrl += MainActivity.picname;
	      imageUrl += ".jpg";
	      bmImg = returnBitMap(imageUrl);
	      Message message = new Message();  
          message.what = 1;  
          handler.sendMessage(message); 		       
	      Looper.loop();
	    }
	    
	  }
	  
	//定义Handler对象
		private Handler handler =new Handler(){
		@Override
		//当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg){
		super.handleMessage(msg);
		//处理UI
		switch (msg.what){  
            case 1:  
            	imView.setImageBitmap(bmImg);  
                break;  
            } 		
		}
		};
	public Bitmap returnBitMap(String url){
    	URL myFileUrl = null;  
    	Bitmap bitmap = null; 
    	try {  
    		myFileUrl = new URL(url);  
    	} catch (MalformedURLException e) {  
    		e.printStackTrace();  
    	}  
    	try {  
    		HttpURLConnection conn = (HttpURLConnection) myFileUrl  
    		  .openConnection();  
    		conn.setDoInput(true);  
    		conn.connect();  
    		InputStream is = conn.getInputStream();  
    		bitmap = BitmapFactory.decodeStream(is);  
    		is.close();  
    	} catch (IOException e) {  
    		  e.printStackTrace();  
    	}  
    		  return bitmap;  
    }  

}
