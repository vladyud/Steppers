package com.github.kubatatami;

import android.support.v4.app.Fragment;

public interface StepperAdapter {

    String getLabel(int step);

    String getSubLabel(int step);

    Fragment getFragment(int step);

    int  getStepCount();

}
