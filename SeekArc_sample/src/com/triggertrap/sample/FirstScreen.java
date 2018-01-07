/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Triggertrap Ltd
 * Author Neil Davies
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.triggertrap.sample;


import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * FirstScreen.java
 * @author Neil Davies
 * 
 */
public class FirstScreen extends Activity {
	private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Intent intent = new Intent(this, DeviceScanActivity.class);
//		startActivity(intent);

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//				(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//			requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
//			//After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//		} else {
			// Android version is lesser than 6.0 or the permission is already granted.
			Intent intent = new Intent(this, DeviceScanActivity.class);
		startActivity(intent);
//		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission is granted
				Toast.makeText(FirstScreen.this, "granted", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this, DeviceScanActivity.class);
		startActivity(intent);
//                ContactScreenActivity.langHasChanged=false;
			} else {
				Toast.makeText(FirstScreen.this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
