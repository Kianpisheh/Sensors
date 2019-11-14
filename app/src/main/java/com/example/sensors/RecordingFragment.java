package com.example.sensors;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecordingFragment extends Fragment {

    private MaterialButton startButton;
    private TextView afterTime, forTime, start_, startInMinTv, recordForMinTv, recordForTv;
    private TextView samplingDuration, samplingPeriod, everyWord, sampleWord, sampleSecTv, sampleMinTv;
    private Switch customTimingSwitch, customSamplingSwitch;
    private TimeSettings timeSettings;
    private List<String> sensorNames;
    private List<Integer> sensorStatus;
    private static Map<String, Integer> timeValues = new HashMap<>();
    private TimePickerFragment durationPickerFragment, delayPickerFragment,
                                periodPickerFragment, endTimePickerFragment;
    private NoteDialogFragment noteDialogFragment;
    private Bundle sensorsData = new Bundle();
    private EditText noteEditText;


    public RecordingFragment() {
        timeSettings = new TimeSettings(getActivity());
        sensorStatus = new ArrayList<>();
        sensorNames = new ArrayList<>();
    }

    public void fillTimeValues() {
        try {
            timeValues.put("delay", Integer.parseInt(afterTime.getText().toString()));
            timeValues.put("period", Integer.parseInt(samplingPeriod.getText().toString()));
            timeValues.put("duration", Integer.parseInt(samplingDuration.getText().toString()));
            timeValues.put("endTime", 24*60);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recording_fragment, container, false);

        startButton = view.findViewById(R.id.start_recording_btn);
        afterTime = view.findViewById(R.id.after_time_tv);
        start_ = view.findViewById(R.id.start_);
        forTime = view.findViewById(R.id.for_time_tv);
        recordForTv = view.findViewById(R.id.for_);
        startInMinTv = view.findViewById(R.id.start_in_min);
        recordForMinTv = view.findViewById(R.id.record_for_min);
        sampleSecTv = view.findViewById(R.id.sample_sec);
        sampleMinTv = view.findViewById(R.id.sample_min);
        samplingDuration = view.findViewById(R.id.sampling_duration);
        samplingPeriod = view.findViewById(R.id.sampling_period);
        fillTimeValues();
        everyWord = view.findViewById(R.id.every_word);
        sampleWord = view.findViewById(R.id.sample_word);
        customSamplingSwitch = view.findViewById(R.id.sampling_sw);
        customTimingSwitch = view.findViewById(R.id.timing_sw);

        sensorsData = getArguments();

//        customSamplingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                setCustomSampling(isChecked);
//            }
//        });
//
//        customTimingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                setTimingSampling(isChecked);
//            }
//        });
//        customTimingSwitch.setChecked(true);

//        afterTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                delayPickerFragment = new TimePickerFragment();
//                Bundle arg = new Bundle();
//                arg.putString("param", "delay");
//                arg.putString("title", "Start after (min)");
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                delayPickerFragment.setTargetFragment(fm.findFragmentByTag("RecFrag"), 1);
//                delayPickerFragment.setArguments(arg);
//                delayPickerFragment.setValueChangeListener(delayPickerFragment);
//                delayPickerFragment.show(getActivity().getSupportFragmentManager(), "time picker");
//            }
//        });

//        forTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                endTimePickerFragment = new TimePickerFragment();
//                Bundle arg = new Bundle();
//                arg.putString("param", "endTime");
//                arg.putString("title", "Record for (min)");
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                endTimePickerFragment.setTargetFragment(fm.findFragmentByTag("RecFrag"), 2);
//                endTimePickerFragment.setArguments(arg);
//                endTimePickerFragment.setValueChangeListener(endTimePickerFragment);
//                endTimePickerFragment.show(getActivity().getSupportFragmentManager(), "time picker");
//            }
//        });

//        samplingDuration.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                durationPickerFragment = new TimePickerFragment();
//                Bundle arg = new Bundle();
//                arg.putString("param", "duration");
//                arg.putString("title", "Sampling time (sec)");
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                durationPickerFragment.setTargetFragment(fm.findFragmentByTag("RecFrag"), 3);
//                durationPickerFragment.setArguments(arg);
//                durationPickerFragment.setValueChangeListener(durationPickerFragment);
//                durationPickerFragment.show(getActivity().getSupportFragmentManager(), "time picker");
//            }
//        });

//        samplingPeriod.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                periodPickerFragment = new TimePickerFragment();
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                periodPickerFragment.setTargetFragment(fm.findFragmentByTag("RecFrag"), 4);
//                Bundle arg = new Bundle();
//                arg.putString("param", "period");
//                arg.putString("title", "Sampling period (min)");
//                periodPickerFragment.setArguments(arg);
//                periodPickerFragment.setValueChangeListener(periodPickerFragment);
//                periodPickerFragment.show(getActivity().getSupportFragmentManager(), "time picker");
//            }
//        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorNames = sensorsData.getStringArrayList("name");
                sensorStatus = sensorsData.getIntegerArrayList("status");

                startSensorDataCollection(sensorNames, sensorStatus);

            }
        });

        if (customSamplingSwitch.isChecked()) {
            setCustomSampling(true);
        } else {
            setCustomSampling(false);
        }

        if (customTimingSwitch.isChecked()) {
            setTimingSampling(true);
        } else {
            setTimingSampling(false);
        }

        // set initial time settings
        timeSettings.updateValues(timeValues);

        return view;
    }

    private void startSensorDataCollection(List<String> sensorNames,
                                           List<Integer> sensorStatus) {
        Context context = getContext();
        try {
            if (context != null) {
                System.out.println("Service scheduled at: " + System.currentTimeMillis());
                Bundle timingValues = new Bundle();
                timingValues.putInt("delay", timeSettings.getTimeParameter("delay"));
                timingValues.putInt("endTime", timeSettings.getTimeParameter("endTime"));
                timingValues.putInt("duration", timeSettings.getTimeParameter("duration"));
                timingValues.putInt("period", timeSettings.getTimeParameter("period"));
                timingValues.putBoolean("customTiming", timeSettings.isCustomTiming());
                timingValues.putBoolean("customSampling", timeSettings.isCustomSampling());
                timingValues.putStringArrayList("sensor_names", (ArrayList<String>) sensorNames);
                timingValues.putIntegerArrayList("status", (ArrayList<Integer>) sensorStatus);
                timingValues.putBoolean("log_battery", sensorsData.getBoolean("log_battery"));
                timingValues.putBoolean("log_wifi", sensorsData.getBoolean("log_wifi"));
                timingValues.putBoolean("log_location", sensorsData.getBoolean("log_location"));
                timingValues.putBoolean("record_audio", sensorsData.getBoolean("record_audio"));

                ((NavigationHost) getActivity()).navigateTo(new RecordingStatusFragment(),
                        R.id.sensors_list_container,true, "RecStatusFrag", timingValues);

            }
        } catch (NullPointerException e) {
            Toast.makeText(context, "Error in the service\n" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String param = data.getStringExtra("param");
        Integer value = data.getIntExtra("value", 0);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode != 5) {
                timeSettings.setTimeParameter(param, value);
            }

            if (requestCode == 1) {
                afterTime.setText(String.valueOf(value));
            } else if (requestCode == 3) {
                samplingDuration.setText(String.valueOf(value));
            } else if (requestCode == 4) {
                samplingPeriod.setText(String.valueOf(value));
            } else if (requestCode == 2) {
                forTime.setText(String.valueOf(value));
            } else if (requestCode == 5) {
                startSensorDataCollection(sensorNames, sensorStatus);
            }
        }
    }

    private void setCustomSampling(boolean isChecked) {
        samplingPeriod.setClickable(isChecked);
        samplingDuration.setClickable(isChecked);
        if (isChecked) {
            try {
                timeSettings.setTimeParameter("period", Integer.parseInt(samplingPeriod.getText().toString()));
                timeSettings.setTimeParameter("duration", Integer.parseInt(samplingDuration.getText().toString()));
                timeSettings.setCustomSampling(true);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            samplingPeriod.setTextColor(0xFF197AB9);
            samplingDuration.setTextColor(0xFF197AB9);
            everyWord.setTextColor(0xFF535353);
            sampleSecTv.setTextColor(0xFF535353);
            sampleMinTv.setTextColor(0xFF535353);
            sampleWord.setTextColor(0xFF535353);
        } else {
            samplingPeriod.setTextColor(0xFFB9B9B9);
            samplingDuration.setTextColor(0xFFB9B9B9);
            everyWord.setTextColor(0xFFB9B9B9);
            sampleWord.setTextColor(0xFFB9B9B9);
            sampleSecTv.setTextColor(0xFFB9B9B9);
            sampleMinTv.setTextColor(0xFFB9B9B9);
            timeSettings.setCustomSampling(false);
        }
    }

    private void setTimingSampling(boolean isChecked) {
        samplingPeriod.setClickable(isChecked);
        samplingDuration.setClickable(isChecked);
        if (isChecked) {
            try {
                timeSettings.setTimeParameter("endTime", Integer.parseInt(forTime.getText().toString()));
                timeSettings.setTimeParameter("delay", Integer.parseInt(afterTime.getText().toString()));
                timeSettings.setCustomTiming(true);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            afterTime.setTextColor(0xFF197AB9);
            forTime.setTextColor(0xFF197AB9);
            start_.setTextColor(0xFF535353);
            startInMinTv.setTextColor(0xFF535353);
            recordForMinTv.setTextColor(0xFF535353);
            recordForTv.setTextColor(0xFF535353);
        } else {
            afterTime.setTextColor(0xFFB9B9B9);
            forTime.setTextColor(0xFFB9B9B9);
            start_.setTextColor(0xFFB9B9B9);
            startInMinTv.setTextColor(0xFFB9B9B9);
            recordForTv.setTextColor(0xFFB9B9B9);
            recordForMinTv.setTextColor(0xFFB9B9B9);
            timeSettings.setCustomTiming(false);
        }
    }
}
