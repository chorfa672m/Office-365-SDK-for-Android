/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.assetmanagement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.microsoft.assetmanagement.adapters.DisplayCarAdapter;
import com.microsoft.assetmanagement.files.BitmapResizer;
import com.microsoft.assetmanagement.tasks.DeleteCarTask;
import com.microsoft.assetmanagement.tasks.RetieveCarImageTask;
import com.microsoft.assetmanagement.tasks.SaveCarTask;
import com.microsoft.assetmanagement.tasks.UpdateCarTask;
import com.microsoft.assetmanagement.viewmodel.CarListViewItem;
import com.microsoft.office365.lists.SPListItem;

public class DisplayCarActivity extends FragmentActivity {

	private DisplayCarAdapter mListAdapter;
	private CarListViewItem mCarViewItem;
	private AssetApplication mApplication;
	private boolean mIsNew;

	private EditText mCarTitle;
	private EditText mCarDescription;

	final static int CAMARA_REQUEST_CODE = 1000;
	final static int SELECT_PHOTO = 1001;
	
	public void setCarViewItem(CarListViewItem carListViewItem) {
		mCarViewItem = carListViewItem;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_display_car);

        mApplication = (AssetApplication) getApplication();
        mCarTitle = (EditText) findViewById(R.id.textCarTitle);
        mCarDescription = (EditText) findViewById(R.id.textCarDescription);
        
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String data = bundle.getString("data");
                if (data != null) {
                    JSONObject payload = new JSONObject(data);

                    SPListItem listItem = new SPListItem();
                    listItem.loadFromJson(payload.getJSONObject("item"));
                    
                    final String id = String.valueOf(Integer.parseInt(listItem.getData("ID").toString()));
                    new RetieveCarImageTask(this, listItem).execute(id);                    
                }
            } else {
                setTitle("New Car");
                mIsNew = true;
                mCarViewItem = new CarListViewItem();
            }
        } catch (Throwable t) {
            mApplication.handleError(t);
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			return true;
		}
		case R.id.menu_save_car: {
			hideSoftPad();
			saveAction();
			return true;
		}
		case R.id.menu_car_picture: {
			selectPicture();
			return true;
		}
		case R.id.menu_delete_car: {
			deleteCar();
			return true;
		}
		default:
			return true;
		}
	}

	private void deleteCar() {
		new DeleteCarTask(this).execute(mCarViewItem);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_delete_car).setVisible(!mIsNew);
		if (!mIsNew) {
			menu.findItem(R.id.menu_car_picture).setTitle("Change Picture");
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.car_view_menu, menu);
		return true;
	}

	private void selectPicture() {
		final Activity that = this;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				CharSequence[] sources = { "From Library", "From Camera" };
				AlertDialog.Builder builder = new AlertDialog.Builder(that);
				builder.setTitle("Select an option:").setSingleChoiceItems(sources, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								dialog.dismiss();
								openPhotoSource(item);
							}

							private void openPhotoSource(int itemSelected) {
								switch (itemSelected) {
								case 0:
									invokePhotoLibrayIntent();
									break;
								case 1:
									invokeFromCameraIntent();
									break;
								default:
									break;
								}
							}

							private void invokeFromCameraIntent() {
								dispatchTakePictureIntent();
							}

							private void invokePhotoLibrayIntent() {
								Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
								photoPickerIntent.setType("image/*");
								startActivityForResult(photoPickerIntent, SELECT_PHOTO);
							}
						});
				builder.create().show();
			}
		});
	}

	String mCurrentPhotoPath;

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				Log.e("Asset", ex.getMessage());
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, CAMARA_REQUEST_CODE);
			}
		}
	}

	private void saveAction() {
		hideSoftPad();
		setCarData();

		if (getStringFromEdit(mCarTitle).length() == 0
				|| getStringFromEdit(mCarDescription).length() == 0
				|| mCarViewItem.getPicture() == null) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information");
			builder.setMessage("All fields and a photo are required");
			builder.create().show();
			return;
		}

		if (mIsNew) {
			new SaveCarTask(this).execute(mCarViewItem);
		} else {
			new UpdateCarTask(this).execute(mCarViewItem);
		}
	}

	private void setCarData() {
		hideSoftPad();
		mCarViewItem.setCarTitle(getStringFromEdit(mCarTitle));
		mCarViewItem.setCarDescription(getStringFromEdit(mCarDescription));
	}

	private String getStringFromEdit(EditText text) {
		return text.getText().toString().trim();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		final byte[] bytes = getImageData(requestCode, resultCode, data);
		if (bytes != null) {

			mCarViewItem.setPicture(bytes);
			mListAdapter = new DisplayCarAdapter(this, mCarViewItem);
			ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
			viewPager.setAdapter(mListAdapter);
		}
	}

	private final byte[] getImageData(int requestCode, int resultCode, Intent data) {

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		BitmapResizer resizer = new BitmapResizer(displayMetrics);
		switch (requestCode) {
		case SELECT_PHOTO: {
			if (resultCode == RESULT_OK) {

				try {
					Uri selectedImage = data.getData();

					InputStream imageStream = getContentResolver().openInputStream(selectedImage);
					Bitmap bitmap = resizer.getBitmapFrom(imageStream);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					return stream.toByteArray();
				} catch (Throwable t) {
					mApplication.handleError(t);
				}
			}
		}
		case CAMARA_REQUEST_CODE: {
			if (resultCode == RESULT_OK) {
				try {
					if (mCurrentPhotoPath != null) {
						Bitmap bitmap = resizer.getBitmapFrom(mCurrentPhotoPath);
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						return stream.toByteArray();
					}
				} catch (Throwable t) {
					mApplication.handleError(t);
				}
			}
		}
		default:
			break;
		}
		return null;
	}

	private void hideSoftPad() {
		((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(
				InputMethodManager.SHOW_IMPLICIT, 0);
	}
}
