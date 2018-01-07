package com.triggertrap.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyTimer.OnTimeChangeListener, MyTimer.OnSecondChangListener,MyTimer.OnMinChangListener,MyTimer.OnHourChangListener {

    MyTimer timer;
    Button btn_start,btn_stop,btn_reset;
    int h=0,m=0,s=0;
    private ToggleButton toggle;
    private TextView knobvalue;
    private BluetoothGattCharacteristic characteristicTX;
    private String knob;
    private boolean startthnstop=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggle = (ToggleButton) findViewById(R.id.togglebutton);
        knobvalue=(TextView)findViewById(R.id.knobvalue);

        timer = (MyTimer) findViewById(R.id.timer);
        timer.setOnTimeChangeListener(this);
        timer.setSecondChangListener(this);
        timer.setMinChangListener(this);
        timer.setHourChangListener(this);
        timer.setModel(Model.Timer);

        Calendar cal = Calendar.getInstance();

        int millisecond = cal.get(Calendar.MILLISECOND);
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        //12 hour format
        int hour = cal.get(Calendar.HOUR);
        //24 hour format
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
//        timer.setStartTime(hourofday,minute,second);

        Intent intent = getIntent();
         knob = intent.getExtras().getString("knob");
        knobvalue.setText(knob);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
                if (isCheck) {

                } else {

                    if(knob!=null && knob.equalsIgnoreCase("knob one"))
                        MainActivity.this.setResult(1);
                    else if(knob!=null && knob.equalsIgnoreCase("knob two"))
                        MainActivity.this.setResult(2);
                    else if(knob!=null && knob.equalsIgnoreCase("knob three"))
                        MainActivity.this.setResult(3);
                    MainActivity.this.finish();
                }
                }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                timer.start();
                sendnotification("Your Recepie Started", "Start", 456);
                startthnstop=true;
                break;
            case R.id.btn_stop:
                timer.stop();
                break;
            case R.id.btn_reset:
                timer.reset();
                break;
        }
    }

    @Override
    public void onTimerStart(long timeStart) {
        Log.e("pheynix","onTimerStart "+timeStart);
    }

    @Override
    public void onTimeChange(long timeStart, long timeRemain) {
//        Log.e("pheynix","onTimeChange timeStart "+timeStart);
//        Log.e("pheynix","===> timeRemain "+timeRemain);
    }

    @Override
    public void onTimeStop(long timeStart, long timeRemain) {
        Log.e("--->","onTimeStop timestart "+timeStart);
        Log.e("--> ","onTimeStop timeRemain "+timeRemain);
    }

    @Override
    public void onSecondChange(int second) {
//        Log.e("swifty","===> second change to "+second);
        s=second;
        Log.e("notify",h+" - "+m+" - "+s+"===> send notification "+second);
        if(h==0&&m==0&& s==0){

            if(startthnstop) {
                sendnotification("Your Recepie Done", "END", 123);
                toggle.setChecked(false);
                if (knob != null && knob.equalsIgnoreCase("knob one"))
                    MainActivity.this.setResult(1);
                else if (knob != null && knob.equalsIgnoreCase("knob two"))
                    MainActivity.this.setResult(2);
                else if (knob != null && knob.equalsIgnoreCase("knob three"))
                    MainActivity.this.setResult(3);
                MainActivity.this.finish();
                startthnstop=false;
            }

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
    public void onHourChange(int hour) {
//        Log.e("swifty","hour change to "+hour);
        h=hour;
    }

    @Override
    public void onMinChange(int minute) {
//        Log.e("swifty", "minute change to "+minute);
        m=minute;
    }

}
