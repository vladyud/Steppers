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

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kubatatami.steppers.R;

public class InternalSteppersAdapter extends RecyclerView.Adapter<SteppersViewHolder> {

    private SteppersView steppersView;
    private FragmentManager fragmentManager;
    private int currentStep = 0;
    private StepperAdapter adapter;

    public InternalSteppersAdapter(SteppersView steppersView, FragmentManager fragmentManager) {
        this.steppersView = steppersView;
        this.fragmentManager = fragmentManager;
        setHasStableIds(true);
    }

    @Override
    public SteppersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SteppersViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_steppers, parent, false));
    }

    @Override
    public void onBindViewHolder(final SteppersViewHolder holder, int position) {
        holder.setChecked(position < currentStep);
        if (holder.isChecked()) {
            holder.roundedView.setChecked(true);
        } else {
            holder.roundedView.setChecked(false);
            holder.roundedView.setText(position + 1 + "");
        }

        if (position == currentStep) {
            holder.roundedView.setCircleColor(steppersView.getCircleActiveColor());
            holder.textViewLabel.setTextColor(steppersView.getLabelActiveTextColor());
            holder.textViewLabel.setTypeface(holder.textViewLabel.getTypeface(), Typeface.BOLD);
            holder.textViewSubLabel.setTextColor(steppersView.getSubLabelActiveTextColor());
        } else if (holder.isChecked()) {
            holder.roundedView.setCircleColor(steppersView.getCircleDoneColor());
            holder.textViewLabel.setTextColor(steppersView.getLabelDoneTextColor());
            holder.textViewLabel.setTypeface(holder.textViewLabel.getTypeface(), Typeface.NORMAL);
            holder.textViewSubLabel.setTextColor(steppersView.getSubLabelDoneTextColor());
        } else {
            holder.roundedView.setCircleColor(steppersView.getCircleInactiveColor());
            holder.textViewLabel.setTextColor(steppersView.getLabelInactiveTextColor());
            holder.textViewLabel.setTypeface(holder.textViewLabel.getTypeface(), Typeface.NORMAL);
            holder.textViewSubLabel.setTextColor(steppersView.getSubLabelInactiveTextColor());
        }
        if (position < currentStep && steppersView.isBackByTap()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStep(holder.getAdapterPosition());
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }

        if (holder.getAdapterPosition() == 0) {
            holder.viewLineAbove.setVisibility(View.INVISIBLE);
        } else {
            holder.viewLineAbove.setVisibility(View.VISIBLE);
        }

        if (position == getItemCount() - 1) {
            holder.viewLineBottom.setVisibility(View.INVISIBLE);
        } else {
            holder.viewLineBottom.setVisibility(View.VISIBLE);
        }

        holder.textViewLabel.setText(adapter.getLabel(position));
        holder.textViewSubLabel.setText(adapter.getSubLabel(position));
        holder.textViewLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, steppersView.getLabelTextSize());
        holder.textViewSubLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, steppersView.getSubLabelTextSize());

        holder.frameLayout.setVisibility(position == currentStep ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewAttachedToWindow(SteppersViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        initFragment(holder, holder.getAdapterPosition());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return adapter.getStepCount();
    }

    private void initFragment(SteppersViewHolder holder, int position) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        String name = makeFragmentName(steppersView.getId(), position);
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (position != currentStep && fragment != null) {
            ft.remove(fragment);
        } else if (position == currentStep) {
            ft.replace(holder.frameLayout.getId(), adapter.getFragment(position), name);
        }
        ft.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    public Fragment getStepFragment(int step) {
        String name = makeFragmentName(steppersView.getId(), step);
        return fragmentManager.findFragmentByTag(name);
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setStep(int step) {
        if (isValidStep(currentStep) && step != currentStep) {
            int len = Math.abs(step - currentStep) + 1;
            int start = Math.min(step, currentStep);
            currentStep = step;
            notifyItemRangeChanged(start, len);
        }
    }

    public void nextStep() {
        if (isValidStep(currentStep + 1)) {
            currentStep++;
            notifyItemRangeChanged(currentStep - 1, 2);
        }
    }

    public void prevStep() {
        if (isValidStep(currentStep - 1)) {
            this.currentStep--;
            notifyItemRangeChanged(currentStep, 2);
        }
    }

    private boolean isValidStep(int step) {
        return step >= 0 && step < adapter.getStepCount();
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:steppers:" + viewId + ":" + id;
    }

    public void setAdapter(StepperAdapter adapter) {
        this.adapter = adapter;
        currentStep = 0;
        notifyDataSetChanged();
    }
}
