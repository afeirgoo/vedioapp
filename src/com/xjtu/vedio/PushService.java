package com.xjtu.vedio;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class PushService extends Service {
	private static String TAG = "PushService";

	//private String host = "tcp://192.168.1.101:61613";
	private String host = "tcp://113.200.115.170:4102";
	private String userName = "admin";
	private String passWord = "password";

	private MqttClient client;
	private MqttConnectOptions options;
	private String[] myTopics = { "Topics/xjtu/phoneToServer", "Topics/xjtu/serverToPhone" };
	private int[] myQos = { 2, 2 };
	private ScheduledExecutorService scheduler;
	private NotificationManager mNotificationManager;
	//private Notification baseNF;
	private Notification.Builder builder; //= new Notification.Builder(this);
	private PendingIntent pendingIntent;
	public void onCreate() {
		Log.i(TAG, "---->onCreate");
		builder = new Notification.Builder(this);
		Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://113.200.115.170:4101/VedioServer/rest/restService/download/175.jpg"));
		pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
		init();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "---->onStartCommand");
		startReconnect();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void init() {
		Log.i(TAG, "---->init");
		try {
			// host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯�?标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
			Log.i(TAG, host);
			client = new MqttClient(host, "test", new MemoryPersistence());
			// MQTT的连接设�?
			options = new MqttConnectOptions();
			// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
			options.setCleanSession(true);
			// 设置连接的用户名
			options.setUserName(userName);
			// 设置连接的密�?
			options.setPassword(passWord.toCharArray());
			/*
			 * Sets the connection timeout value. This value, measured in seconds, defines the maximum time interval the client will wait for the network connection to the MQTT server to be established. The default timeout is 30 seconds. A value of 0 disables timeout processing meaning the client will wait until the network connection is made successfully or fails.
			 */
			options.setConnectionTimeout(10);
			/*
			 * Sets the "keep alive" interval. This value, measured in seconds, defines the maximum time interval between messages sent or received. It enables the client to detect if the server is no longer available, without having to wait for the TCP/IP timeout. The client will ensure that at least one message travels across the network within each keep alive period. In the absence of a data-related message during the time period, the client sends a very small "ping" message, which the server will acknowledge. A value of 0 disables keepalive processing in the client.
			 * The default value is 60 seconds
			 */
			options.setKeepAliveInterval(20);
			// 设置回调
			client.setCallback(new MqttCallback() {

				@Override
				public void connectionLost(Throwable cause) {
					// 连接丢失后，�?般在这里面进行重�?
					Log.i(TAG, "connectionLost");
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					// publish后会执行到这�?
					Log.i(TAG, "deliveryComplete---------" + token.isComplete());
				}

				@Override
				public void messageArrived(String topicName, MqttMessage message) throws Exception {
					// subscribe后得到的消息会执行到这里�?
					Log.i(TAG, "messageArrived----------"+message.toString());
					/*Message msg = new Message();
					msg.what = 1;
					msg.obj = topicName + "---" + message.toString();
					Log.i(TAG, msg.toString());*/
					mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					/*
					Notification notification = new Notification();
					notification.flags |= Notification.FLAG_SHOW_LIGHTS;
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notification.defaults = Notification.DEFAULT_ALL;
					notification.icon = R.drawable.icon;
					notification.when = System.currentTimeMillis();
					Intent notificationIntent = new Intent(PushService.this, MainActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(PushService.this, 0, notificationIntent, 0);
					//notification.setLatestEventInfo(getApplicationContext(), "My notification", message.toString(), contentIntent);
					*/
					//Notification.Builder builder = new Notification.Builder(this);
					//Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.csdn.net/itachi85/"));
					//PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
					
					builder.setContentIntent(pendingIntent);
					 builder.setSmallIcon(R.drawable.icon);
					 builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.mail));
					 builder.setAutoCancel(true);
					 builder.setContentTitle("��֪ͨͨ");
					 
					 mNotificationManager.notify(0, builder.build());
					 
					//mNotificationManager.notify(1, baseNF);
				}
			});
			// connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startReconnect() {
		Log.i(TAG, "---->startReconnect");
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Log.i(TAG, "---->startReconnect"+"-------->"+client.isConnected());
				if (!client.isConnected()) {
					connect();
				}
			}
		}, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
	}

	private void connect() {
		Log.i(TAG, "---->connect");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					client.connect(options);// 建立连接
					client.subscribe(myTopics, myQos);// 订阅主题
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, e.getMessage());
				}
			}
		}).start();
	}

}
