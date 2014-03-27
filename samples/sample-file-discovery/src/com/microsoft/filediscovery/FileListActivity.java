/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.filediscovery;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.assetmanagement.R;
import com.microsoft.filediscovery.adapters.FileItemAdapter;
import com.microsoft.filediscovery.tasks.DownloadFileTask;
import com.microsoft.filediscovery.tasks.RetrieveFilesTask;
import com.microsoft.filediscovery.viewmodel.FileItem;
import com.microsoft.filediscovery.viewmodel.FileViewItem;

// TODO: Auto-generated Javadoc
/**
 * The Class FileListActivity.
 */
public class FileListActivity extends FragmentActivity {

	/** The m list view. */
	private ListView mListView;

	private String resourseId;
	private String endpoint;
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_lists);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String data = bundle.getString("data");
			if (data != null) {
				JSONObject payload;
				try {
					payload = new JSONObject(data);
					resourseId = payload.getString("resourseId");
					endpoint = payload.getString("endpoint");

					new RetrieveFilesTask(FileListActivity.this).execute(resourseId, endpoint);
				} 
				catch (JSONException e) {
					Log.e("Asset", e.getMessage());
				}
			}
		}
		
		mListView = (ListView) findViewById(R.id.list);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				final FileViewItem serviceItem = (FileViewItem) mListView.getItemAtPosition(position);

				
				FileItem file = new FileItem();
				file.Id = serviceItem.Id;
				file.Endpoint = endpoint;
				file.ResourceId = resourseId;
				new DownloadFileTask(FileListActivity.this).execute(file);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home: {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		case R.id.menu_new_file: {
			Intent intent = new Intent(FileListActivity.this, FileItemActivity.class);
			JSONObject payload = new JSONObject();
			try {
				payload.put("resourseId", resourseId);
				payload.put("endpoint", endpoint);
				payload.put("isShareUri", false);
				intent.putExtra("data", payload.toString());
				startActivity(intent);
			} catch (Throwable t) {
				Log.e("Asset", t.getMessage());
			}
			
			return true;
		}
		case R.id.menu_refresh: {
			new RetrieveFilesTask(FileListActivity.this).execute(resourseId, endpoint);
			return true;
		}
		default:
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_list_menu, menu);
		return true;
	}

	/**
	 * Sets the list adapter.
	 *
	 * @param adapter the new list adapter
	 */
	public void setListAdapter(FileItemAdapter adapter) {
		mListView.setAdapter(adapter);
	}
}
