/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.triggertrap.sample;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.triggertrap.seekarc.SeekArc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {

    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private int[] RGBFrame = {0,0,0};
    private TextView isSerial;
    private TextView mConnectionState;
    private TextView mDataField;
    private ToggleButton toggle,toggle1,toggle2;
    private Button timerbtn;
    private SeekBar mGreen,mBlue;
    private SeekArc mRed,mRed1,mRed2;
    private int knobvalue=0;
    private String mDeviceName;
    private String mDeviceAddress;
  //  private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
     private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;


    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private TextView mSeekArcProgress,mSeekArcProgress1,mSeekArcProgress2;
public boolean ischecked=false,ischecked1=false,ischecked2=false;

    MyTimer timer;
    Button btn_start,btn_stop,btn_reset;
    int h=0,m=0,s=0;


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void clearUI() {
//        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
       toggle = (ToggleButton) findViewById(R.id.togglebutton);
       toggle1 = (ToggleButton) findViewById(R.id.togglebutton1);
       toggle2= (ToggleButton) findViewById(R.id.togglebutton2);
       timerbtn = (Button) findViewById(R.id.timer);

//        mSeekArc.setClockwise(false);
        // Sets up UI references.
//        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
//         mConnectionState = (TextView) findViewById(R.id.connection_state);
        // is serial present?
//        isSerial = (TextView) findViewById(R.id.isSerial);
   
//        mDataField = (TextView) findViewById(R.id.data_value);
        mRed = (SeekArc) findViewById(R.id.seekRed);
        mRed1 = (SeekArc) findViewById(R.id.seekRed1);
        mRed2 = (SeekArc) findViewById(R.id.seekRed2);
//        mGreen = (SeekBar) findViewById(R.id.seekGreen);
//        mBlue = (SeekBar) findViewById(R.id.seekBlue);
        mSeekArcProgress = (TextView) findViewById(R.id.seekArcProgress);
        mSeekArcProgress1 = (TextView) findViewById(R.id.seekArcProgress1);
        mSeekArcProgress2= (TextView) findViewById(R.id.seekArcProgress2);

        readSeek(mRed,mRed1,mRed2,0);
//        readSeek(mGreen,1);
//        readSeek(mBlue,2);
     
        getActionBar().setTitle("Controls");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        timerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(ischecked) {
//                    toggle.setChecked(false);
                Dialog dialog = new Dialog(DeviceControlActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.customdiag);
                dialog.setCancelable(true);
                // there are a lot of settings, for dialog, check them all out!
                // set up radiobutton
                final RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.radioButton);
                final RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.radioButton1);
                final RadioButton rd3 = (RadioButton) dialog.findViewById(R.id.radioButton2);
                Button next = (Button) dialog.findViewById(R.id.next);

                // now that the dialog is set up, it's time to show it
                dialog.show();
//                    startActivity(new Intent(DeviceControlActivity.this, MainActivity.class));
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(DeviceControlActivity.this,MainActivity.class);

                        if(rd1.isChecked()){
                            i.putExtra("knob","knob one");
                            ischecked1=false;

                        }
                        else if(rd2.isChecked()){
                            i.putExtra("knob","knob two");

                            ischecked=false;
                        }else if(rd3.isChecked()){
                            i.putExtra("knob", "knob three");


//                            ischecked=false;
//                            final byte[] tx = ("" + 2000).getBytes();
//                            Log.e("value ", "=============> else");
//                            if (mConnected) {
//                                try {
//                                    characteristicTX.setValue(tx);
//                                    mBluetoothLeService.writeCharacteristic(characteristicTX);
//                                    mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
//                                }catch(Exception e){
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            mRed.setProgress(0);
//                            // The toggle is disabled
//                            ischecked1=false;
//                            final byte[] tx2 = ("" + 3000).getBytes();
//                            Log.e("value ", "=============> else");
//                            if (mConnected) {
//                                try {
//                                    characteristicTX.setValue(tx2);
//                                    mBluetoothLeService.writeCharacteristic(characteristicTX);
//                                    mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
//                                }catch(Exception e){
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            mRed1.setProgress(0);

                        }

                        startActivityForResult(i, 11);

                    }
                });


            }
        });
        if(toggle.isChecked())
            ischecked=true;
        else
            ischecked=false;

        if(toggle1.isChecked())
            ischecked1=true;
        else
            ischecked1=false;

        if(toggle2.isChecked())
            ischecked2=true;
        else
            ischecked2=false;



        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
                if (isCheck) {
                    // The toggle is enabled
                    ischecked=true;
                } else {
                    ischecked=false;
                    final byte[] tx = ("" + 2000).getBytes();
                    Log.e("value ", "=============> else");
                    if (mConnected) {
                        try {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    mRed.setProgress(0);

                    // The toggle is disabled
                }
            }
        });
        toggle1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
                if (isCheck) {
                    // The toggle is enabled
                    ischecked1=true;
                } else {
                    ischecked1=false;
                    final byte[] tx = ("" + 3000).getBytes();
                    Log.e("value ", "=============> else");
                    if (mConnected) {
                        try {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    mRed1.setProgress(0);

                    // The toggle is disabled
                }
            }
        });
        toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
                if (isCheck) {
                    // The toggle is enabled
                    ischecked2=true;
                } else {
                    ischecked2=false;
                    final byte[] tx = ("" + 4000).getBytes();
                    Log.e("value ", "=============> else");
                    if (mConnected) {
                        try {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    mRed2.setProgress(0);
                    // The toggle is disabled
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {

        if (data != null) {
//            mDataField.setText(data);
            //Neo-G
            /**
             * Algo to setup the data from device on the display
             * 1.Get the data and print it on the log
             * 2.Parse the data to represent key value of command and control data
             * 3.Put on a Switch case to determine the status of the Stove and knobs
             * 4.The Switch case's each case will modify each of the attributes of the  App.
             * 5.First know the Knob position. Switch on or Off Toggle will change the on of button
             * 6.Parse the exact position of the knob and toggle or change the seekark position and do not fire an event for that.
             * 7.Ignore rest of the data.
             * 8.Make a quiry to fetch the data of the current positions of the knob when the stove gets connected.
             */

            String str[] = data.split("\n");
            for (String string : str) {
                int indexOfKnob =-1;
                indexOfKnob = string.indexOf("pos");
                if(indexOfKnob >= 0){
                    String valueString  = string.substring(indexOfKnob+3,data.indexOf(";"));
                    String[] splitValue = valueString.split(":");
                    switch(splitValue[0]){

                        case "1":mRed.setProgress(Integer.parseInt(splitValue[1]));
                            break;

                        case "2":mRed1.setProgress(Integer.parseInt(splitValue[1]));
                            break;

                        case "3":mRed2.setProgress(Integer.parseInt(splitValue[1]));
                            break;
                        default:
                            break;
                    }
                }
            }

        }

    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

 
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            
            // If the service exists for HM 10 Serial, say so.
            if(SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
//                isSerial.setText("Yes, serial :-)");
            } else {
//                isSerial.setText("No, serial :-(");
            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

     		// get characteristic when UUID matches RX/TX UUID
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
                characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);

            }
        }
        
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
    private void readSeek(SeekArc seekBar,SeekArc seekBar1,SeekArc seekBar2,final int pos) {

        seekBar.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

                if (ischecked) {
                    try {
                        String str = (knobvalue * 2) + "," + 0 + "," + 0 + "\n";
                        final byte[] tx = ("" + str).getBytes();
                        Log.e("value ", "=============> " + str + " - " + tx + " - " + knobvalue);
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Excepetetion ", "=============> " + e.toString());

                    }
                } else {
                    Toast.makeText(DeviceControlActivity.this, "Turn ON first", Toast.LENGTH_SHORT).show();
                   /* final byte[] tx = ("" + 2000).getBytes();
                    Log.e("value ", "=============> else");
                    if (mConnected) {
                        try {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }*/
                    mRed.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                mSeekArcProgress.setText(String.valueOf(progress * 2));

                knobvalue = progress;

            }
        });



        seekBar1.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

                if (ischecked1) {
                    try {
                        String str = "" + 0 + "," + (knobvalue * 2) + "," + 0 + "\n";
                        final byte[] tx = ("" + str).getBytes();
                        Log.e("value ", "=============> " + str + " - " + tx + " - " + knobvalue);
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Excepetetion ", "=============> " + e.toString());

                    }
                } else {
                    Toast.makeText(DeviceControlActivity.this, "Turn ON first", Toast.LENGTH_SHORT).show();
                  /*  final byte[] tx = ("" + 3000).getBytes();
                    Log.e("value ", "=============> else");
                    if (mConnected) {
                        try {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }*/
                    mRed1.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                mSeekArcProgress1.setText(String.valueOf(progress * 2));

                knobvalue = progress;

            }
        });
        seekBar2.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

                if (ischecked2) {
                    try {
                        String str = "" + 0 + "," + 0 + "," + (knobvalue * 2) + "\n";
                        final byte[] tx = ("" + str).getBytes();
                        Log.e("value ", "=============> " + str + " - " + tx + " - " + knobvalue);
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Excepetetion ", "=============> " + e.toString());

                    }
                } else {
                    Toast.makeText(DeviceControlActivity.this, "Turn ON first", Toast.LENGTH_SHORT).show();
                    /*final byte[] tx = ("" + 4000).getBytes();
                    Log.e("value ", "=============> else");
                    if (mConnected) {
                        try {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }*/
                    mRed2.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                mSeekArcProgress2.setText(String.valueOf(progress * 2));

                knobvalue = progress;

            }
        });
       /* seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                RGBFrame[pos] = progress;
                Log.e("----> ", "Son progress change=" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                makeChange();
            }
        });*/
    }
    // on change of bars write char 
    private void makeChange() {
    	 String str = RGBFrame[0] + "," + RGBFrame[1] + "," + RGBFrame[2] + "\n";
         Log.d(TAG, "Sending result=" + str);
        Log.e("----> ", "Sending result=" + str);
		 final byte[] tx = str.getBytes();
		 if(mConnected) {
		    characteristicTX.setValue(tx);
			mBluetoothLeService.writeCharacteristic(characteristicTX);
			mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
		 }
    }


    private void sendnotification(String str1,String str2,int x) {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(str1)
                .setContentText(str2)
                .setAutoCancel(true)
                .setContentIntent(null)
                .build();

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationManager.notify(x, notification);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==11)
        {
            if(resultCode == 1) {
                ischecked=false;
                final byte[] tx = ("" + 2000).getBytes();
                if (mConnected) {
                    try {
                        characteristicTX.setValue(tx);
                        mBluetoothLeService.writeCharacteristic(characteristicTX);
                        mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                toggle.setChecked(false);
                mRed.setProgress(0);

            }

            else if(resultCode == 2) {
                ischecked1=false;
                final byte[] tx = ("" + 3000).getBytes();
                if (mConnected) {
                    try {
                        characteristicTX.setValue(tx);
                        mBluetoothLeService.writeCharacteristic(characteristicTX);
                        mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                toggle1.setChecked(false);

                mRed1.setProgress(0);
            }

           else if(resultCode == 3) {
                ischecked2=false;
                final byte[] tx = ("" + 4000).getBytes();
                if (mConnected) {
                    try {
                        characteristicTX.setValue(tx);
                        mBluetoothLeService.writeCharacteristic(characteristicTX);
                        mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                toggle2.setChecked(false);

                mRed2.setProgress(0);
            }

        }
    }
}