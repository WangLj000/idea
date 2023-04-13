package com.example.overlay.idea;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author WangLongjiang
 * @Date 2023/4/13 10:53
 * @Version 1.0
 */
/*
    进行网络请求，获取服务器端的版本信息
 */
public class UpdateTask extends AsyncTask<Void, Integer, String> {

    private String TAG = "UpdateTask";
    public OnUpdateListener mListener = null;
//    public UpdateActivity mActivity;
    String versionName;
    public UpdateTask(String versionName) {
        this.versionName = versionName;
    }

    public void setListener(OnUpdateListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection uRLConnection = null;
        InputStream is = null;
        BufferedReader buffer = null;
        String result = null;
        String urlStr = Constants.UPDATE_REQUEST_URL + "?" + Constants.APK_VERSION_NAME + "=" + versionName;
        Log.e(TAG, "urlStr: " + urlStr);
        try {
            URL url = new URL(urlStr);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("GET");

            is = uRLConnection.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            result = strBuilder.toString();
            Log.e(TAG, result);
        } catch (Exception e) {
            Log.e(TAG, "http post error");
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {

                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }

        if (result != null) {
            Log.i(TAG, result);
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        mActivity.mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        UpdateInfo info = parseJson(result);
        if (this.mListener != null) {
            this.mListener.onSuccess(info);
        }
    }



    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
//        mActivity.mProgressBar.setProgress(values[0]);
    }

    private UpdateInfo parseJson(String result) {
        UpdateInfo info = null;
        try {
            JSONObject obj = new JSONObject(result);
            String updateMessage = obj.getString(Constants.APK_UPDATE_CONTENT);
            String apkUrl = obj.getString(Constants.APK_DOWNLOAD_URL);
            String versionName = obj.getString(Constants.APK_VERSION_NAME);
            String md5 = obj.getString(Constants.APK_MD5);
            boolean diffUpdate = obj.getBoolean(Constants.APK_DIFF_UPDATE);
            if (!this.versionName.equals(versionName)) {
                info = new UpdateInfo(updateMessage, apkUrl, versionName, md5, diffUpdate);
            }
        } catch (JSONException e) {
            Log.e(TAG, "parse json error");
        }
        return info;
    }

    public interface OnUpdateListener {
        void onSuccess(UpdateInfo info);
    }
}
