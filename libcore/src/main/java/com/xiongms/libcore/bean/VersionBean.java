package com.xiongms.libcore.bean;

import java.io.Serializable;

/**
 *
 */
public class VersionBean implements Serializable {

    private String ver;
    private String downloadUrl;
    private String description;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
