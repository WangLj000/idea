package com.example.overlay.idea;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author WangLongjiang
 * @Date 2023/4/13 13:54
 * @Version 1.0
 */
public class DownloadService extends IntentService {

    String TAG = "DownloadService";
    private static final int BUFFER_SIZE = 1024 * 1024; // 8k ~ 32K
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        assert intent != null;
//        Bundle bundle = intent.getExtras();
        Log.e(TAG, "onHandleIntent: " + intent.getStringExtra(Constants.APK_DOWNLOAD_URL));
        String urlStr = Constants.OTA_SERVER_IP + intent.getStringExtra(Constants.APK_DOWNLOAD_URL);
        String md5 = intent.getStringExtra(Constants.APK_MD5);
        boolean isDiff = intent.getBooleanExtra(Constants.APK_DIFF_UPDATE, false);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);

            String downloadName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File downloadFile = new File(dir, downloadName);
            out = new FileOutputStream(downloadFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;
            Intent sendIntent = new Intent();
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
//                    updateProgress(progress);
                    sendIntent.putExtra(Constants.UPDATE_DOWNLOAD_PROGRESS, progress);
                    getApplicationContext().sendBroadcast(sendIntent);
                }
                oldProgress = progress;
            }
            // 下载完成
            Log.i(TAG, "Download finished!!..");

            File apkFile = downloadFile;
            if (isDiff) {
                // 增量式升级，先将patch合成新apk
                String oldApkPath = InfoUtils.getBaseApkPath(getApplicationContext());
                String newApkName = "update.apk";
                String newApkPath = dir.getPath() + "/" + newApkName;
                String patchPath = downloadFile.getPath();

                Log.i(TAG, "MD5:");
                Log.i(TAG, "old apk md5: " + SignUtils.getMd5ByFile(new File(oldApkPath)));
                Log.i(TAG, "new apk md5: " + SignUtils.getMd5ByFile(new File(newApkPath)));
                Log.i(TAG, "patch md5: " + SignUtils.getMd5ByFile(new File(patchPath)));

                Log.i(TAG, "Patch diff...");
                int patchResult = PatchUtils.patch(oldApkPath, newApkPath, patchPath);
                if (patchResult == 0) {
                    apkFile = new File(newApkPath);
                }
            }

            installAPk(apkFile);
            //installApp(getPackageName(),apkFile.getPath());
//
//            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error"+e.toString());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    private void installAPk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
            Log.e(TAG,ignored.toString());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e(TAG, "installAPk: " + apkFile.getAbsolutePath());
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.example.overlay.idea.fileprovider",apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            //startInstallPermissionSettingActivity();
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);

    }

    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public boolean installApp(String packageName,String apkPath) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = new ProcessBuilder("pm", "install", "-i", packageName, "-r", apkPath).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
            }
            if (process != null) {
                process.destroy();
            }
        }
        Log.e("result", "" + errorMsg.toString());
        //如果含有“success”认为安装成功
        return successMsg.toString().equalsIgnoreCase("success");
    }
}
