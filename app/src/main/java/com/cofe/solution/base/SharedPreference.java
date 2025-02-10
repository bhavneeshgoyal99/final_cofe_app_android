package com.cofe.solution.base;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


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


    public void savePreviewPageTabSelection(String tabname) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("preview_page_tab",tabname);
        editor.apply();
    }
    public String retrievPreviewPageTabSelection() {
        return sharedPreference.getString("preview_page_tab","");
    }


    public void saveDevicePreviewHeightandWidth(int width, int height) {
        Log.d("saveDevicePreviewHeightandWidth view ", "height > "+ height +" | width "+ width);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("width_height",width  +";;;"+height);
        editor.apply();
    }

    public String retrievDevicePreviewHeightandWidth() {
        Log.d("saveDevicePreviewHeightandWidth view ", " return height > "+ sharedPreference.getString("width_height",""));
        return sharedPreference.getString("width_height","");
    }

    public void saveisDeviceAOV(boolean isDeviceAOV) {
        Log.d("saveisDeviceAOV ","device > " + isDeviceAOV );
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean("isDeviceAOV",isDeviceAOV);
        editor.apply();
    }

    public Boolean retrievisDeviceAOV() {
        Log.d("retrievisDeviceAOV  ", " device "+ sharedPreference.getBoolean("isDeviceAOV",false));
        return sharedPreference.getBoolean("isDeviceAOV",false);
    }


    public List<HashMap<String, Object>> getDevList() {
        String json = sharedPreference.getString("DEV_LIST", null);
        Log.d("retrievijsonsDeviceAOV  ", " json  "+ json);

        if (json == null) {
            return null; // No data found
        }

        // Convert JSON to List<HashMap<String, Object>>
        Gson gson = new Gson();
        Type type = new TypeToken<List<HashMap<String, Object>>>() {}.getType();

        return gson.fromJson(json, type);
    }

    public  void saveDevList(List<HashMap<String, Object>> newDevList) {
        SharedPreferences.Editor editor = sharedPreference.edit();

        Gson gson = new Gson();
        String json = gson.toJson(newDevList);

        // Save the new list, replacing the old one
        editor.putString("DEV_LIST", json);
        editor.apply();
    }



}