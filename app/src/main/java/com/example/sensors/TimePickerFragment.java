package com.example.sensors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.NumberPicker;
import android.support.v4.app.DialogFragment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TimePickerFragment extends DialogFragment implements NumberPicker.OnValueChangeListener{

    public NumberPicker.OnValueChangeListener valueChangeListener;
    private int value;
    private String param = "";
    private String title = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        param = (String) getArguments().get("param");
        title = (String) getArguments().get("title");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setValue(0);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(60);

        // create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
                notifyToTarget(Activity.RESULT_OK);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
                notifyToTarget(Activity.RESULT_CANCELED);
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    private void notifyToTarget(int code) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent data = new Intent();
            data.putExtra("param", param);
            data.putExtra("value", value);
            targetFragment.onActivityResult(getTargetRequestCode(), code, data);
        }
    }

    final NumberPicker.Formatter twoDigitFormatter = new NumberPicker.Formatter() {
        final StringBuilder mBuilder = new StringBuilder();
        final java.util.Formatter mFmt = new java.util.Formatter(mBuilder, Locale.CANADA);
        final Object[] mArgs = new Object[1];
        public String format(int value) {
            mArgs[0] = value;
            if (value == 0) {
                return "now";
            }
            mBuilder.delete(0, mBuilder.length());
            mFmt.format("%d min", mArgs);
            return mFmt.toString();
        }
    };

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int value, int i1) {
        this.value = value;
    }
}
