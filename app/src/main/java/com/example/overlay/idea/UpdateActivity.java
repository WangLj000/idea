package com.example.overlay.idea;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author WangLongjiang
 * @Date 2023/4/13 9:07
 * @Version 1.0
 */
public class UpdateActivity extends Activity {
    public ListView mListView;
    public ProgressBar mProgressBar;
    PackageInfo packageInfo = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update2);
        mListView = findViewById(R.id.list);
        mProgressBar = findViewById(R.id.progress);

        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        List<Map<String,String>> mapList = new ArrayList<Map<String, String>>();
        Map<String,String> map = new HashMap<>();
        map.put("key", "版本信息");
        map.put("value",packageInfo.versionName );
        mapList.add(map);

        Map<String,String> map1 = new HashMap<>();
        map1.put("key", "版本号");
        map1.put("value",packageInfo.versionCode + "" );
        mapList.add(map1);
//        mapList.add(Map.of("版本信息", "版本更新"));
//        mapList.add(Map.of("版本信息", "版本更新"));

//        mListView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_2,android.R.id.text1, list));
//        mListView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_2,android.R.id.text2, list));
        mListView.setAdapter(new SimpleAdapter(this, mapList, R.layout.list_item, new String[]{"key", "value"}, new int[]{R.id.key, R.id.value}));
    }

    public void checkUpdate(View view) {
        Log.e("TAG","checkUpdate: ");
        UpdateTask updateTask = new UpdateTask(packageInfo.versionName);
        updateTask.setListener(new UpdateTask.OnUpdateListener() {
            @Override
            public void onSuccess(UpdateInfo info) {
                if (info != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    startUpdate(info);
                }else {
                    //已经是最新版本
                    mProgressBar.setVisibility(View.GONE);
                }

            }
        });
        updateTask.execute();
    }

    private void startUpdate(UpdateInfo info) {
        //TODO
        Log.e("UpdateTask", "startUpdate: " + info.getUrl() + " " + info.getMd5() + " " + info.isDiffUpdate());
        Bundle bundle = new Bundle();
        //bundle.putString("url", "http://    ");
        bundle.putString(Constants.APK_DOWNLOAD_URL, info.getUrl());
        bundle.putString(Constants.APK_MD5, info.getMd5());
        bundle.putBoolean(Constants.APK_DIFF_UPDATE, info.isDiffUpdate());
        startService(new Intent(this, DownloadService.class).putExtras(bundle));
    }
}
