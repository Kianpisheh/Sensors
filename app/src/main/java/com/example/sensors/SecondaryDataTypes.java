package com.example.sensors;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class SecondaryDataTypes extends Fragment {

    private int AUDIO_RECORD_PERMISSION = 100;
    private int LOCATION_FINE_ACCESS_PERMISSION = 200;
    private int LOCATION_COARSE_ACCESS_PERMISSION = 300;
    private int PERMISSIONS_REQUEST_CODE = 123;
    private boolean recordAudio = false;
    private CheckBox audioCheckBox, wifiCheckBox, batteryCheckbox, locationCheckbox;
    private boolean logBattery, logWifi, logLocation;
    private Bundle args;


    public SecondaryDataTypes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.secondary_data_types, container, false);

        MaterialButton nextButton = view.findViewById(R.id.secondary_cnt_next);
        audioCheckBox = view.findViewById(R.id.audio_cb);
        wifiCheckBox = view.findViewById(R.id.wifi_cb);
        batteryCheckbox = view.findViewById(R.id.battery_cb);
        locationCheckbox = view.findViewById(R.id.location_cb);
        audioCheckBox.setChecked(recordAudio);
        args = getArguments();


        audioCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                recordAudio = isChecked;
            }
        });

        // next button onclick listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logWifi = wifiCheckBox.isChecked();
                logBattery = batteryCheckbox.isChecked();
                logLocation = locationCheckbox.isChecked();
                recordAudio = audioCheckBox.isChecked();

                List<String> neededPermissions = new ArrayList<>();
                if ((logLocation) && (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))) {
                    neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                } else if ((recordAudio) && (!checkPermission(Manifest.permission.RECORD_AUDIO))) {
                    neededPermissions.add(Manifest.permission.RECORD_AUDIO);
                }

                if (!neededPermissions.isEmpty()) {
                    ActivityCompat.requestPermissions(getActivity(),
                            neededPermissions.toArray(new String[neededPermissions.size()]), PERMISSIONS_REQUEST_CODE);
                } else {
                    logWifi = wifiCheckBox.isChecked();
                    logBattery = batteryCheckbox.isChecked();
                    logLocation = locationCheckbox.isChecked();
                    recordAudio = audioCheckBox.isChecked();

                    if (args != null) {
                        args.putBoolean("log_wifi", logWifi);
                        args.putBoolean("log_battery", logBattery);
                        args.putBoolean("log_location", logLocation);
                        args.putBoolean("record_audio", recordAudio);
                    }

                    ((NavigationHost) getActivity()).navigateTo(new RecordingFragment(),
                            R.id.sensors_list_container, true, "RecFrag", args);
                }
            }
        });

        return view;

    }

    private boolean checkPermission(String permission) {
        return (ContextCompat.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void explainAndRequestPermission(String title, String rationale,
                                             String permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            showExplanation(title, rationale, permission, requestCode);
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{permission}, requestCode);
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{permission}, permissionRequestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                audioCheckBox.setChecked(false);
            }
        });
        builder.create().show();
    }
}
