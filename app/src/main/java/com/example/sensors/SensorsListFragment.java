package com.example.sensors;


import android.hardware.Sensor;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SensorsListFragment extends Fragment {

    private ListView sensorsListView;
    private MaterialButton nextButton, cancelButton;
    private CheckBox selectallCheckbox;
    private ArrayList<SensorItem> sensorsStatusList;
    private SensorsListAdapter sensorsListAdapter;
    private List<Sensor> sensors;


    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sensors_list_fragment, container, false);

        System.out.println(getActivity().getSupportFragmentManager().getBackStackEntryCount());

        // connect layouts with views
        sensorsListView = view.findViewById(R.id.sensors_list_lv);
        nextButton = view.findViewById(R.id.sensor_cnt_next);
        cancelButton = view.findViewById(R.id.sensor_cnt_cancel);
        selectallCheckbox = view.findViewById(R.id.sensor_cnt_select_all);

        // setup sensors list adapter
        SensorsManager sensorsManager = new SensorsManager(this.getContext());
        sensors = sensorsManager.getSensors();
        sensorsStatusList = initializeSensorStatus(false);
        sensorsListAdapter = new SensorsListAdapter(this.getContext(),sensorsStatusList);
        sensorsListView.setAdapter(sensorsListAdapter);

        // select all cb onclick listener
        selectallCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    sensorsStatusList = initializeSensorStatus(true);
                    SensorsListAdapter.sensorsList = sensorsStatusList;
                    sensorsListAdapter.notifyDataSetChanged();
                }
            }
        });

        // next button onclick listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putIntegerArrayList("status", (ArrayList<Integer>) getSensorsList().get("status"));
                data.putStringArrayList("name", (ArrayList<String>) getSensorsList().get("name"));

                ((NavigationHost) getActivity()).navigateTo(new SecondaryDataTypes(),
                        R.id.sensors_list_container,true, "SecList", data);
            }
        });

        return view;
    }

    private ArrayList<SensorItem> initializeSensorStatus(boolean isSelect){
        ArrayList<SensorItem> sensorsStatusList = new ArrayList<>();
        for(Sensor s: sensors){
            SensorItem sensorItem = new SensorItem();
            sensorItem.setSelected(isSelect);
            sensorItem.setSensorName(s.getName());
            sensorsStatusList.add(sensorItem);
        }
        return sensorsStatusList;
    }

    private Map<String, List> getSensorsList() {
        List<String> sensorNames = new ArrayList<>();
        List<Integer> sensorsCheckStatus = new ArrayList<>();
        for (SensorItem sItem: sensorsStatusList) {
            sensorNames.add(sItem.getSensorName());
            int myInt = sItem.getSelected() ? 1 : 0;
            sensorsCheckStatus.add(myInt);
        }
        Map<String, List> sensorsData = new HashMap<>();
        sensorsData.put("name", sensorNames);
        sensorsData.put("status", sensorsCheckStatus);
        return  sensorsData;
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("leaving");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
