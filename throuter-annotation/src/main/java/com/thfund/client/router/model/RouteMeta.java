package com.thfund.client.router.model;

/**
 * @author WayneWang
 * @since 2017/6/1 11:00
 */

public class RouteMeta {
    private String bundleID;

    private String className;

    public RouteMeta(String bundleID, String className) {
        this.bundleID = bundleID;
        this.className = className;
    }

    public String getBundleID() {
        return bundleID;
    }

    public void setBundleID(String bundleID) {
        this.bundleID = bundleID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public static RouteMeta build(String bundleID, String className) {
        return new RouteMeta(bundleID, className);
    }
}
