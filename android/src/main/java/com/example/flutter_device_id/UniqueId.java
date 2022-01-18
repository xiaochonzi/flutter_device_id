package com.example.flutter_device_id;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class UniqueId {
    private static String TAG = "FlutterDeviceIdPlugin";
    private static final String fileName = "FlutterDeviceId";

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private String secretKey = "FlutterDeviceIdPlugin";

    public UniqueId(String secretKey){
        this.secretKey = secretKey;
    }

    public boolean checkPermission(Context context){
        boolean checked = false;
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            int hasPermission = ContextCompat.checkSelfPermission(context,PERMISSIONS_STORAGE[i]);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                checked = true;
            }
        }
        return checked;
    }

    public String getUniqueId(Context context) {
        // 如果没有获取权限，则返回AndroidId
        if (!checkPermission(context)){
            return getAndroidId(context);
        }
        File file = getFilePath();
        String uuid = FileUtils.readFile2String(file);
        if (uuid==null || uuid.isEmpty()){
            uuid = uuid(16);
            FileUtils.writeFileFromString(file,uuid);
        }
        return uuid;
    }

    private File getFilePath(){
        String filePath = null;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/.uuid/";
        }else{
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/.uuid/";
        }

        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        return new File(dir, fileName);
    }

    private String uuid(int length){
        String str = UUID.randomUUID().toString().replace("-", "");
        if (length < 1 || length >= 32) {
            return str;
        } else {
            return str.substring(str.length() - length);
        }
    }

    private String getAndroidId(Context mContext){
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID).toString();
    }
}
