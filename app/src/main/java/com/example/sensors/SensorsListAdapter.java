package com.example.sensors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SensorsListAdapter extends BaseAdapter {

    public Context context;
    public static ArrayList<SensorItem> sensorsList;


    private class ViewHolder {
        protected CheckBox checkBox;
        private TextView nameTv;

    }

    public SensorsListAdapter(Context context, ArrayList<SensorItem> sensorsList) {
        this.context = context;
        this.sensorsList = sensorsList;
    }

    @Override
    public int getCount() {
        return sensorsList.size();
    }

    @Override
    public Object getItem(int position) {
        return sensorsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sensor_item, null, true);

            viewHolder.checkBox = convertView.findViewById(R.id.sensor_item_cb);
            viewHolder.nameTv = convertView.findViewById(R.id.sensor_item_tv);

            convertView.setTag(viewHolder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameTv.setText(sensorsList.get(position).getSensorName());

        viewHolder.checkBox.setChecked(sensorsList.get(position).getSelected());

        viewHolder.checkBox.setTag(R.integer.btnplusview, convertView);
        viewHolder.checkBox.setTag( position);
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tempview = (View) viewHolder.checkBox.getTag(R.integer.btnplusview);
                TextView tv = tempview.findViewById(R.id.sensor_item_tv);
                Integer pos = (Integer)  viewHolder.checkBox.getTag();

                if (sensorsList.get(pos).getSelected()){
                    sensorsList.get(pos).setSelected(false);
                } else {
                    sensorsList.get(pos).setSelected(true);
                }

            }
        });

        return convertView;
    }
}
