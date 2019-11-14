package com.example.sensors;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MenuFragment extends Fragment {

    private int EXTERNAL_STORAGE_PERMISSION = 300;
    private HashMap<String, Boolean> resourcePermissions = new HashMap<>();


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_fragment, container, false);

        MaterialButton startButton = view.findViewById(R.id.start_btn);


        // check/ask for permissions
        checkResourcePermissions();

        // set onclick listener for next start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                // check microphone permission
//                if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    explainAndRequestPermission("External Storage Access",
//                            "To record audio data, the app needs " +
//                                    "to access your phone microphone", Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            EXTERNAL_STORAGE_PERMISSION);
                // no need for permission

                ((NavigationHost) getActivity()).navigateTo(new SensorsListFragment(),
                        R.id.sensors_list_container,true, "SenListFrag", null);
            }
        });

        return view;
    }

    private void checkResourcePermissions() {

        List<String> manifestPermissions = new ArrayList<>();
        manifestPermissions.add(Manifest.permission.RECORD_AUDIO);
        manifestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        manifestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        manifestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listOfNeededPermissions = new ArrayList<>();
        for (String permission : manifestPermissions) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                listOfNeededPermissions.add(permission);
            } else {
                resourcePermissions.put(permission, true);
            }
        }

        if(!listOfNeededPermissions.isEmpty()) {
            // request for for non-granted permissions
            ActivityCompat.requestPermissions(getActivity(),
                    listOfNeededPermissions.toArray(new String[listOfNeededPermissions.size()]),
                    117);
        }

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
            }
        });
        builder.create().show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

