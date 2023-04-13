package com.example.overlay.idea;

/**
 * @Author WangLongjiang
 * @Date 2023/4/13 14:01
 * @Version 1.0
 */
public class UpdateInfo {

    private String url;
    private String updateMessage;
    private String versionName;
    private String md5;
    private boolean diffUpdate;

    public UpdateInfo(String updateMessage, String apkUrl, String versionName, String md5,
            boolean diffUpdate) {
        this.url = apkUrl;      // apk下载地址
        this.updateMessage = updateMessage;  // 更新内容
        this.versionName = versionName;     // 版本号
        this.md5 = md5;                     // md5
        this.diffUpdate = diffUpdate;       // 是否是增量更新

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean isDiffUpdate() {
        return diffUpdate;
    }

    public void setDiffUpdate(boolean diffUpdate) {
        this.diffUpdate = diffUpdate;
    }
}
