package com.example.sensors;


import android.os.Bundle;
import android.support.v4.app.Fragment;


public interface NavigationHost {

    void navigateTo(Fragment fragment, int layout, boolean addToBackStack, String tag, Bundle data);
}

