package com.cofe.solution.base;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.security.GeneralSecurityException;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;


public class SharedPreference {

    String tag = getClass().getName();

    SharedPreferences sharedPreference;


    /**
     * initialing sharedPreference  and sharedPreference_lang
     */
    public SharedPreference(Context cxt) {
        try {
            sharedPreference = EncryptedSharedPreferences.create(
                    "mypreferencesCookies",
                    getMasterKey(),
                    cxt,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    String getMasterKey() {
        String masterKeyAlias = "";
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return masterKeyAlias;
    }


    public void saveLoginStatus(int loginStatus) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt("login_status",loginStatus);
        editor.apply();
    }

    public Integer retrievLoginStatus() {
        return sharedPreference.getInt("login_status",-1);
    }

    public void deleteLoginStatus() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.remove("login_status");
        editor.apply();
    }


    public void saveDevName(String dev_name) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("dev_name",dev_name);
        editor.apply();
    }

    public String retrievDevName() {
        return sharedPreference.getString("dev_name","");
    }

    public void deleteDevName() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.remove("dev_name");
        editor.apply();
    }

    public void saveDevPreviewReloadCount(int count) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt("dev_preview_load_count",count);
        editor.apply();
    }

    public Integer retrievPreviewReloadCount() {
        return sharedPreference.getInt("dev_preview_load_count",0);
    }

    public void deletePreviewReloadCount() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.remove("dev_preview_load_count");
        editor.apply();
    }


}