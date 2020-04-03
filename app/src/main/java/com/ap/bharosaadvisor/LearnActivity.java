package com.ap.bharosaadvisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ap.bharosaadvisor.fragments.Learn;
import com.ap.bharosaadvisor.helper.Utils;


public class LearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        Utils.FetchLearnContent(false,this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container1, new Learn())
                .commit();
    }
}
