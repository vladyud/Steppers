/*
 * Copyright (C) 2016 Jakub Bogacki, Krystian Drożdżyński
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kubatatami;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class SteppersView extends LinearLayout {

    private InternalSteppersAdapter internalSteppersAdapter;

    private FragmentManager fragmentManager;

    private List<OnStepChangedListener> onStepChangedListeners = new ArrayList<>();

    public SteppersView(Context context) {
        super(context);
    }

    public SteppersView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SteppersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SteppersView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void notifyDataSetChanged() {
        internalSteppersAdapter.notifyDataSetChanged();
    }

    public void setAdapter(StepperAdapter adapter) {
        if (fragmentManager != null && internalSteppersAdapter == null) {
            build();
        }
        internalSteppersAdapter.setAdapter(adapter);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        if (internalSteppersAdapter == null) {
            build();
        }
    }

    public void addOnStepChangedListener(OnStepChangedListener onStepChangedListener) {
        onStepChangedListeners.add(onStepChangedListener);
    }

    public void removeOnStepChangedListener(OnStepChangedListener onStepChangedListener) {
        onStepChangedListeners.remove(onStepChangedListener);
    }

    public Fragment getStepFragment(int step) {
        return internalSteppersAdapter.getStepFragment(step);
    }

    public Fragment getCurrentStepFragment() {
        return getStepFragment(getCurrentStep());
    }

    public int getCurrentStep() {
        return internalSteppersAdapter.getCurrentStep();
    }

    public void setStep(int step) {
        internalSteppersAdapter.setStep(step);
    }

    public void nextStep() {
        internalSteppersAdapter.nextStep();
    }

    public void prevStep() {
        internalSteppersAdapter.prevStep();
    }

    public int getStepCount() {
        return internalSteppersAdapter.getItemCount();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getCurrentStep());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setStep(savedState.getCurrentStep());
    }

    private void build() {
        setOrientation(LinearLayout.HORIZONTAL);
        RecyclerView recyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(layoutParams);
        addView(recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        internalSteppersAdapter = new InternalSteppersAdapter(this, fragmentManager);
        recyclerView.setAdapter(internalSteppersAdapter);
        internalSteppersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                invokeStepChangedListeners(getCurrentStep());
            }
        });
    }

    private void invokeStepChangedListeners(int step) {
        for(OnStepChangedListener onStepChangedListener : onStepChangedListeners) {
            onStepChangedListener.onStepChanged(step);
        }
    }

    protected static class SavedState extends BaseSavedState {

        int currentStep;

        public SavedState(Parcel source) {
            super(source);
            currentStep = source.readInt();
        }

        public SavedState(Parcelable superState, int currentStep) {
            super(superState);
            this.currentStep = currentStep;
        }

        public int getCurrentStep() {
            return currentStep;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentStep);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnStepChangedListener {

        void onStepChanged(int step);
    }

}
