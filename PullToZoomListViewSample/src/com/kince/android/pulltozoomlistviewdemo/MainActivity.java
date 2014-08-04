package com.kince.android.pulltozoomlistviewdemo;

import com.kince.android.pulltozoomlistview.PullToZoomListView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity{

	private PullToZoomListView mListView;
	private String[] adapterData;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListView = (PullToZoomListView) findViewById(R.id.detail_list_view);
		mListView.setShadow(R.drawable.detail_pic_shadow);
		this.mListView.getHeaderView().setScaleType(
				ImageView.ScaleType.CENTER_CROP);

	
		adapterData = new String[] { "Activity", "Service", "View",
				"Intent", "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient",
				"DDMS", "Android Studio", "Fragment", "Loader", "NDK",
				"Volley", "ListView", "ActionBar", "WindowManager", "Launcher",
				"App", "Lock", "Screen" };

		mListView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_1, adapterData));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position!=0){
					Toast.makeText(getApplicationContext(), adapterData[position-1]+"", 0).show();
				}
				
			}
		});
		
	}


}
