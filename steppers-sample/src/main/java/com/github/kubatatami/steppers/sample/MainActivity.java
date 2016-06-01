package com.github.kubatatami.steppers.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.github.kubatatami.StepperAdapter;
import com.github.kubatatami.SteppersView;

public class MainActivity extends AppCompatActivity {

    private SteppersView steppersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        steppersView = (SteppersView) findViewById(R.id.steppersView);
        steppersView.setFragmentManager(getSupportFragmentManager());
        steppersView.setAdapter(new StepperAdapter() {
            @Override
            public String getLabel(int step) {
                return "Step nr " + (step + 1);
            }

            @Override
            public String getSubLabel(int step) {
                return steppersView.getCurrentStep() > step ? "Done" : "sublabel nr " + (step + 1);
            }

            @Override
            public Fragment getFragment(int step) {
                Fragment fragment = new BlankFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BlankFragment.KEY_NR, step + 1);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getStepCount() {
                return 10;
            }
        });
        steppersView.addOnStepChangedListener(new SteppersView.OnStepChangedListener() {
            @Override
            public void onStepChanged(int step) {
                Toast.makeText(MainActivity.this, "Step changed", Toast.LENGTH_SHORT).show();
            }
        });
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
