package com.deone.xpro;

import static com.deone.xpro.tools.Constants.APP_LANGUAGE;
import static com.deone.xpro.tools.Constants.CONF_APP;
import static com.deone.xpro.tools.Constants.THEME_MODE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String myUID;
    private String checkLanguage;
    private boolean checkMode;
    private SwitchCompat sw_page_language;
    private SwitchCompat sw_page_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configApp();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.your_settings));
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    private void configApp() {
        SharedPreferences mPrefs = getSharedPreferences(""+CONF_APP, Context.MODE_PRIVATE);
        checkMode = mPrefs.getBoolean(""+THEME_MODE, false);

        if (checkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "DARK MODE", Toast.LENGTH_SHORT).show();
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "LIGHT MODE", Toast.LENGTH_SHORT).show();
        }

        //checkLanguage = mPrefs.getString(""+APP_LANGUAGE, "");
        //setLocale(""+checkLanguage);
    }

    private void setLocale(String language) {
        //Locale locale = new Locale(language.toLowerCase());
    }

    private void checkUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
            initUI();
        }else {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initUI() {
        sw_page_language = findViewById(R.id.sw_page_language);
        sw_page_mode = findViewById(R.id.sw_page_mode);
        sw_page_mode.setChecked(checkMode);
        sw_page_mode.setText(checkMode?getResources().getString(R.string.app_mode_darck)
                :getResources().getString(R.string.app_mode_ligth));
        sw_page_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveThemePreferences(true);
                Toast.makeText(this, "DARK MODE activé", Toast.LENGTH_SHORT).show();
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveThemePreferences(false);
                Toast.makeText(this, "DARK MODE désactivé", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveThemePreferences(boolean isChecked){
        SharedPreferences preferences = getSharedPreferences(""+CONF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(""+THEME_MODE, isChecked);
        editor.apply();
    }

}