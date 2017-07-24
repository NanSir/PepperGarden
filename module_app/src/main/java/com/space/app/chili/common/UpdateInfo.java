package com.space.app.chili.common;

/**
 * APK版本更新信息
 * Created by zhuyinan on 2017/5/5.
 */

public class UpdateInfo {

    private String name;

    private String version;

    private String changelog;

    private int updated_at;

    private String versionShort;

    private String build;

    private String installUrl;

    private String install_url;

    private String direct_install_url;

    private String update_url;

    private Binary binary;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getChangelog() {
        return this.changelog;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public int getUpdated_at() {
        return this.updated_at;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getVersionShort() {
        return this.versionShort;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getBuild() {
        return this.build;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getInstallUrl() {
        return this.installUrl;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    public String getInstall_url() {
        return this.install_url;
    }

    public void setDirect_install_url(String direct_install_url) {
        this.direct_install_url = direct_install_url;
    }

    public String getDirect_install_url() {
        return this.direct_install_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public String getUpdate_url() {
        return this.update_url;
    }

    public void setBinary(Binary binary) {
        this.binary = binary;
    }

    public Binary getBinary() {
        return this.binary;
    }


    public class Binary {

        private int fsize;

        public void setFsize(int fsize) {
            this.fsize = fsize;
        }

        public int getFsize() {
            return this.fsize;
        }
    }
}
