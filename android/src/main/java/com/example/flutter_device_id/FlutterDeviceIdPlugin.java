package com.example.flutter_device_id;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** FlutterDeviceIdPlugin */
public class FlutterDeviceIdPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware,PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {
  private static final int REQUEST_PERMISSION_CODE = 99;
  private static boolean hasRequestPermission = false;
  private MethodChannel channel;
  private static Activity activity;
  private static Context mContext;
  private Result mResult;
  private static String secretKey = "FlutterDeviceIdPlugin";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_device_id");
    channel.setMethodCallHandler(this);
    mContext = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if (call.method.equals("requestPermissions")){
      mResult = result;
      requestPermissions(REQUEST_PERMISSION_CODE);
    }else if(call.method.equals("setSecretKey")){
      setSecretKey(call.argument("secretKey").toString());
    }else if(call.method.equals("getUniqueId")){
      UniqueId uniqueId = new UniqueId(secretKey);
      result.success(uniqueId.getUniqueId(mContext));
    }else {
      result.notImplemented();
    }
  }

  private void setSecretKey(@NonNull String argument) {
    secretKey = argument;
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    mContext = null;
    mResult = null;
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    return false;
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (REQUEST_PERMISSION_CODE == requestCode){
      hasRequestPermission = false;
      for (int i = 0; i < permissions.length; i++) {
        if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
          hasRequestPermission = true;
        }
      }
      mResult.success(hasRequestPermission);
    }
    return false;
  }

  // 请求权限
  public void requestPermissions(int requestCode) {
    try {
      if (Build.VERSION.SDK_INT >= 23) {
        ArrayList<String> requestPerssionArr = new ArrayList<>();

        int hasSdcardRead = mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasSdcardRead != PackageManager.PERMISSION_GRANTED) {
          requestPerssionArr.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        int hasSdcardWrite = mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasSdcardWrite != PackageManager.PERMISSION_GRANTED) {
          requestPerssionArr.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // 是否应该显示权限请求
        if (requestPerssionArr.size() >= 1) {
          String[] requestArray = new String[requestPerssionArr.size()];
          for (int i = 0; i < requestArray.length; i++) {
            requestArray[i] = requestPerssionArr.get(i);
          }
          activity.requestPermissions(requestArray, requestCode);
        }
      }
    } catch (Exception e) {
      ;
    }
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
    binding.addRequestPermissionsResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
