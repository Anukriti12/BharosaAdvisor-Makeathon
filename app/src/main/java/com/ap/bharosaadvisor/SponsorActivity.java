package com.ap.bharosaadvisor;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ap.bharosaadvisor.helper.LocaleHelper;

import static com.ap.bharosaadvisor.helper.Utils.language;

public class SponsorActivity extends AppCompatActivity implements View.OnClickListener
{
    long startingTime;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor);

        startingTime = System.currentTimeMillis();

        findViewById(R.id.sponsor_button).setOnClickListener(this);
        findViewById(R.id.sponsor_cancel).setOnClickListener(this);

        //TODO - Send server that ad has been shown
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case (R.id.sponsor_button):
            {
                //TODO - Send server recorded time
                long duration = System.currentTimeMillis() - startingTime;
                Toast.makeText(this, "Sponsor Ad clicked! It ran for " + duration + " ms", Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
            case (R.id.sponsor_cancel):
            {
                //TODO - Send server recorded time
                long duration = System.currentTimeMillis() - startingTime;
                Toast.makeText(this, "Sponsor Ad closed! It ran for " + duration + " ms", Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
    }
}
