package com.example.sharing_app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  private static final String CHANNEL = "samples.flutter.io/battery";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    new MethodChannel(getFlutterView(),CHANNEL).setMethodCallHandler(
            (methodCall, result) -> {
              if(methodCall.method.equals("getConfig")){
                ArrayList<String> arrayList= getConfig();
                if (arrayList.size() != 0){
                  result.success(arrayList);
                }else {
                  result.error("UNAVAILABLE", "get application config error.", null);
                }
              }else{
                result.notImplemented();
              }
            }
    );
  }
  /**
   * 获取已安装应用包名以及应用名
   *
   * @return arrayList
   */
  private ArrayList<String> getConfig(){
    ArrayList<String> arrayList = new ArrayList<>();
    PackageManager packageManager = getPackageManager();
    List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
    for (int i = 0; i < packageInfoList.size(); i++){
      ApplicationInfo info = null;
      try {
        info = packageManager.getApplicationInfo(packageInfoList.get(i).packageName,0);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
      arrayList.add(packageInfoList.get(i).packageName + " " + packageManager.getApplicationLabel(info));
      //Log.e("123456", "getConfig: " + arrayList.get(i));
    }
    return arrayList;
  }
  /**
   * 获取电池电量
   *
   * @return
   */
  private int getBatteryLevel() {

    int batteryLevel = -1;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
      batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    } else {
      Intent intent = new ContextWrapper(getApplicationContext()).
              registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
      batteryLevel = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
              intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }
    return batteryLevel;
  }
}
