package com.xjtu.vedio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {	
	private Button mButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(MainActivity.this,PushService.class);
		startService(intent);	
		mButton = (Button) findViewById(R.id.picbutton);
		mButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent();				
				intent.setClass(MainActivity.this,com.xjtu.vedio.PictureView.class);
				startActivity(intent);
				Toast.makeText(MainActivity.this, "ͼƬ�鿴", 0).show();
			}
		});
	     
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
}
