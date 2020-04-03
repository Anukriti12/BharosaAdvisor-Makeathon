package com.ap.bharosaadvisor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ap.bharosaadvisor.adapters.LanguageAdapter;
import com.ap.bharosaadvisor.data.Language;
import com.ap.bharosaadvisor.helper.Utils;

public class LanguageActivity extends AppCompatActivity
{
    SharedPreferences sharedPreferences;

    public static final Language[] SUPPORTED_LANGUAGES =
            {
                    new Language("English", "en"),
                    new Language("Hindi", "hi"),
                    new Language("Gujarati", "gu"),
                    new Language("Marathi", "mr"),
                    new Language("Urdu", "ur"),
                    new Language("Telgu", "te"),
                    new Language("Kannada", "kn")
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final boolean calledFromMainNavigation = bundle.getBoolean("calledFromMainNavigation", false);

        RecyclerView recyclerView = findViewById(R.id.language_list);
        sharedPreferences = getSharedPreferences(Utils.PREFERENCE_LABEL, Context.MODE_PRIVATE);

        final LanguageAdapter languageAdapter = new LanguageAdapter(this, SUPPORTED_LANGUAGES);
        String lang = sharedPreferences.getString("language", "en");
        for (int i = 0; i < SUPPORTED_LANGUAGES.length; i++)
        {
            if (SUPPORTED_LANGUAGES[i].languageCode.equals(lang))
            {
                languageAdapter.selectedLanguage = i;
                break;
            }
        }

        recyclerView.setAdapter(languageAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        findViewById(R.id.language_proceed).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sharedPreferences.edit()
                        .putString("language", SUPPORTED_LANGUAGES[languageAdapter.selectedLanguage].languageCode)
                        .apply();
                finish();
                if (calledFromMainNavigation)
                {
                    Intent intent = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                    assert intent != null;
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}
