package com.example.sharing_app;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  private static final String CHANNEL = "samples.flutter.io/battery";
  private List<PackageInfo> packageInfoList;
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
              }else if(methodCall.method.equals("share")){
                Log.e("123456", "onCreate: 成功调用到java" );
                share(methodCall.argument("item"));
              }else {
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
    packageInfoList = packageManager.getInstalledPackages(0);
    for (int i = 0; i < packageInfoList.size(); i++){
      ApplicationInfo info = null;
      try {
        info = packageManager.getApplicationInfo(packageInfoList.get(i).packageName,0);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
      arrayList.add(packageInfoList.get(i).packageName + " " + packageManager.getApplicationLabel(info));
    }
    return arrayList;
  }
  /**
   * 分享apk文件
   *
   * @param item 需要分享的app下标
   */
  private void share(int item){
    File apkFile = new File(packageInfoList.get(item).applicationInfo.sourceDir);
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.setType("*/*");
    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFile));
    startActivity(intent);
  }
}
