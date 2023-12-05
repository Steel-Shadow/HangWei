package com.example.hangwei.other;

/**
 *    desc   : App 配置管理类
 */
public final class AppConfig {
    public static final String VERSION_NAME = "1.0";
    public static final int VERSION_CODE = 10;

    /**
     * 当前是否为调试模式
     */
    public static boolean isDebug() {
        return Boolean.parseBoolean("true");
    }

    /**
     * 获取当前构建的模式
     */
    public static String getBuildType() {
        return "debug";
    }

    /**
     * 当前是否要开启日志打印功能
     */
    public static boolean isLogEnable() {
        return true;
    }

    /**
     * 获取当前应用的包名
     */
    public static String getPackageName() {
        return "com.example.hangwei";
    }

    /**
     * 获取当前应用的版本名
     */
    public static String getVersionName() {
        return VERSION_NAME;
    }

    /**
     * 获取当前应用的版本码
     */
    public static int getVersionCode() {
        return VERSION_CODE;
    }
}