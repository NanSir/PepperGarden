package com.space.app.chili.download;


import java.io.Serializable;

/**
 * 下载文件信息
 */
public class DownloadInfo implements Serializable {
    /**
     * 文件大小
     */
    private long total;
    /**
     * 已下载大小
     */
    private long progress;
    private int logo;
    private String title;
    private String name;
    private String content;
    private String loadUrl;
    private String saveUrl;
    private int code;
    private String versionName;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
    }

    public long getProgress() {
        return progress;
    }

    public long getTotal() {
        return total;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    public DownloadInfo(long total, long progress) {
        this.total = total;
        this.progress = progress;
    }
}
