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

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kubatatami.steppers.R;

import java.util.ArrayList;
import java.util.List;

public class SteppersAdapter extends RecyclerView.Adapter<SteppersViewHolder> {

    private SteppersView steppersView;
    private Context context;
    private List<SteppersItem> items = new ArrayList<>();
    private FragmentManager fragmentManager;

    private int currentStep = 0;

    public SteppersAdapter(SteppersView steppersView, FragmentManager fragmentManager) {
        this.steppersView = steppersView;
        this.context = steppersView.getContext();
        this.fragmentManager = fragmentManager;
        setHasStableIds(true);
    }

    @Override
    public SteppersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SteppersViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_steppers, parent, false));
    }

    @Override
    public void onBindViewHolder(final SteppersViewHolder holder, final int position) {
        final SteppersItem steppersItem = items.get(position);

        holder.setChecked(position < currentStep);
        if (holder.isChecked()) {
            holder.roundedView.setChecked(true);
        } else {
            holder.roundedView.setChecked(false);
            holder.roundedView.setText(position + 1 + "");
        }

        if (position == currentStep || holder.isChecked()) {
            holder.roundedView.setCircleColor(R.color.circle_color_light_blue);
        } else {
            holder.roundedView.setCircleColor(R.color.circle_color_dark_blue);
        }
        if (position < currentStep) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStep(position);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }

        holder.textViewLabel.setTextColor(ContextCompat.getColor(context, position == currentStep ? android.R.color.black : R.color.circle_color_dark_blue));
        holder.textViewLabel.setText(steppersItem.getLabel());
        holder.textViewSubLabel.setText(steppersItem.getSubLabel());

        holder.frameLayout.setVisibility(position == currentStep ? View.VISIBLE : View.GONE);

        initFragment(holder, position, steppersItem);
    }

    private void initFragment(SteppersViewHolder holder, int position, SteppersItem steppersItem) {
        if (fragmentManager != null && steppersItem.getFragment() != null) {
            holder.frameLayout.setTag(frameLayoutName());

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            String name = makeFragmentName(steppersView.getId(), position);
            Fragment fragment = fragmentManager.findFragmentByTag(name);

            if (position != currentStep) {
                if (fragment != null) {
                    fragmentTransaction.detach(fragment);
                }
            } else {
                if (fragment != null) {
                    fragmentTransaction.attach(fragment);
                } else {
                    fragment = steppersItem.getFragment();
                    fragmentTransaction.add(steppersView.getId(), fragment,
                            name);
                }
            }

            if (fragmentTransaction != null) {
                fragmentTransaction.commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }

            if (fragmentManager.findFragmentByTag(name) != null &&
                    fragmentManager.findFragmentByTag(name).getView() != null) {

                View fragmentView = fragmentManager.findFragmentByTag(name).getView();

                if (fragmentView.getParent() != null && frameLayoutName() != ((View) fragmentView.getParent()).getTag()) {
                    steppersView.removeViewInLayout(fragmentView);

                    holder.frameLayout.removeAllViews();
                    holder.frameLayout.addView(fragmentView);
                }
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    public void setItems(List<SteppersItem> items) {
        this.items = items;
        currentStep = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private boolean isValidStep(int step) {
        return step >= 0 && step < items.size();
    }

    private static String frameLayoutName() {
        return "android:steppers:framelayout";
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:steppers:" + viewId + ":" + id;
    }
}
