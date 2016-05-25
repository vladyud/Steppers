package com.github.kubatatami.steppers.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.kubatatami.SteppersItem;
import com.github.kubatatami.SteppersView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SteppersView steppersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        steppersView = (SteppersView) findViewById(R.id.steppersView);
        ArrayList<SteppersItem> steps = new ArrayList<>();
        int i = 0;
        while (i <= 10) {
            final SteppersItem item = new SteppersItem();
            item.setLabel("Step nr " + i);

            BlankFragment blankFragment = new BlankFragment();
            item.setSubLabel("Fragment: " + blankFragment.getClass().getSimpleName());
            item.setFragment(blankFragment);
            steps.add(item);
            i++;
        }
        steppersView.setFragmentManager(getSupportFragmentManager());
        steppersView.setItems(steps);
    }

    public void nextStep() {
        steppersView.nextStep();
    }

    public void prevStep() {
        steppersView.prevStep();
    }

    public void setStep(int i) {
        steppersView.setStep(i);
    }
}
