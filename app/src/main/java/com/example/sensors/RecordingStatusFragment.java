package com.example.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;


public class RecordingStatusFragment extends Fragment {

    private AlarmManager alarmManager, batteryLogAlarmManager, wifiLogManager, locationAlarmManager;
    private TextView statusTexView;
    private CountDownTimer timer;
    private Bundle timingValues;
    private MaterialButton stopButton;
    private PendingIntent scheduledTask, batteryLogScheduledTask, wifiLogScheduledTask, locationLogScheduledTask;
    private Intent serviceIntent, batteryLogServiceIntent, wifiLogServiceIntent, locationServiceIntent;

    public RecordingStatusFragment() {
        // Required empty public constructor
        alarmManager = null;
        batteryLogAlarmManager = null;
        timingValues = new Bundle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timingValues = getArguments();
        int endTime = 24*60;

        serviceIntent = new Intent(getContext(), SensorDataCollectorService.class);
        batteryLogServiceIntent = new Intent(getContext(), BatteryLogService.class);
        wifiLogServiceIntent = new Intent(getContext(), WifiLogService.class);
        locationServiceIntent = new Intent(getContext(), LocationLogService.class);

        batteryLogServiceIntent.putExtra("endTime", endTime);
        batteryLogServiceIntent.putExtra("log_battery", true);

        locationServiceIntent.putExtra("endTime", endTime);
        locationServiceIntent.putExtra("log_location", true);

        wifiLogServiceIntent.putExtra("endTime", endTime);
        wifiLogServiceIntent.putExtra("log_wifi", true);

        // inform the service about the selected (not selected) sensors
        serviceIntent.putIntegerArrayListExtra("status", timingValues.getIntegerArrayList("status"));
        serviceIntent.putStringArrayListExtra("sensor_names", timingValues.getStringArrayList("sensor_names"));
        // send the time property of the recording task
        serviceIntent.putExtra("delay", timingValues.getInt("delay"));
        serviceIntent.putExtra("duration", timingValues.getInt("duration"));
        serviceIntent.putExtra("period", timingValues.getInt("period"));
        serviceIntent.putExtra("customTiming", timingValues.getBoolean("customTiming"));
        serviceIntent.putExtra("customSampling", timingValues.getBoolean("customSampling"));
        serviceIntent.putExtra("endTime", timingValues.getInt("endTime"));
        serviceIntent.putExtra("record_audio", timingValues.getBoolean("record_audio"));
        serviceIntent.putExtra("note", timingValues.getString("note"));
        System.out.println(timingValues.getString("note") + "rec status");

        String folderName = DataManager.getFolderName();
        serviceIntent.putExtra("folder_name", folderName);
        batteryLogServiceIntent.putExtra("folder_name", folderName);
        wifiLogServiceIntent.putExtra("folder_name", folderName);
        locationServiceIntent.putExtra("folder_name", folderName);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recrding_status_fragment, container, false);

        statusTexView = view.findViewById(R.id.rec_status_tv);
        stopButton = view.findViewById(R.id.stop_recording_btn);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("msg");
                if (message.equals("recording")) {
                    setTimer("count_up", 200000000);
                } else if (message.equals("done")) {
                    setTimer("done", 0);
                    stopButton.setText(getString(R.string.done));
                }
            }
        }, new IntentFilter("ACTION_STATUS_DATA"));


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // start sensor collection + audio
                alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                scheduledTask = PendingIntent.getService(getContext(), 1, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        timingValues.getInt("delay") * 60000, scheduledTask);


                // start battery log
                if (timingValues.getBoolean("log_battery")) {
                    batteryLogAlarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    batteryLogScheduledTask = PendingIntent.getService(getContext(), 0, batteryLogServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    batteryLogAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            timingValues.getInt("delay") * 60000, batteryLogScheduledTask);
                }

                // start wifi log
                if (timingValues.getBoolean("log_wifi")) {
                    wifiLogManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    wifiLogScheduledTask = PendingIntent.getService(getContext(), 2, wifiLogServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    wifiLogManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            timingValues.getInt("delay") * 60000, wifiLogScheduledTask);
                }

                // start location log
                if (timingValues.getBoolean("log_location")) {
                    locationAlarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    locationLogScheduledTask = PendingIntent.getService(getContext(), 3, locationServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    locationAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            timingValues.getInt("delay") * 60000, locationLogScheduledTask);
                }
            }
        }, timingValues.getInt("delay") * 60000);

        setTimer("count_down", timingValues.getInt("delay") * 60000);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopButton.getText() != "Done") {
                    if (alarmManager != null) {
                        getActivity().stopService(new Intent(getContext(), SensorDataCollectorService.class));
                    }
                    if (batteryLogAlarmManager != null) {
                        getActivity().stopService(new Intent(getContext(), BatteryLogService.class));
                    }
                    if (wifiLogManager != null) {
                        getActivity().stopService(new Intent(getContext(), WifiLogService.class));
                    }
                    if (locationAlarmManager != null) {
                        getActivity().stopService(new Intent(getContext(), LocationLogService.class));
                    }
                }
            }
        });
        return view;
    }

    public void setTimer(String type, final int time) {
        if (timer != null) {
            timer.cancel();
        }
        if (type.equals("count_up")) {
            timer = new CountDownTimer(time, 1000) {
                int time_ = time;
                public void onTick(long millisUntilFinished) {
                    statusTexView.setText(timeFormat((time_ - millisUntilFinished) / 1000));
                }
                public void onFinish() {statusTexView.setText("_");}
            };
            timer.start();
        } else if (type.equals("count_down")) {
            timer = new CountDownTimer(time, 1000) {
                public void onTick(long millisUntilFinished) {
                    statusTexView.setText(String.format("Starting in: %s", timeFormat((millisUntilFinished) / 1000)));
                }
                public void onFinish() { statusTexView.setText("_"); }
            };
            timer.start();
        } else if (type.equals("done")) {
            statusTexView.setText(getString(R.string.task_completed));
        }
    }

    private String timeFormat(long time) {
        String text;
        int minute = (int) (time / 60.0);
        long second = time - minute * 60;
        text = String.valueOf(second);
        if (second < 10) {text = "0" + text;}
        if (minute != 0) {
            text = String.valueOf(minute) + ":" + text;
            if (minute < 10) {text = "0" + text;}
        }
        return text;
    }
}
