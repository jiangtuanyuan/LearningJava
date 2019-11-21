package cn.ccsu.learning.utils;

import android.os.Environment;

public class Constant {


    public static String flie = Environment.getExternalStorageDirectory() + "";


    //提示
    public static String CODE = "code";//code
    public static String INFO = "info";//info
    public static String DATA = "data";//info

    public static String DATA_ERROR_SERVER = "请求服务器数据失败!";
    public static String DATA_ERROR_PARSING = "数据解析异常!";
    public static String DATA_ERROR_NET = "请求服务器失败,请检查网络设置!";
    public static String DATA_ERROR_SERVER_RETRY = "请求服务器失败,请稍后再试!";
}
