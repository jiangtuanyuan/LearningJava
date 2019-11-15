package cn.ccsu.learning.net;

import cn.ccsu.learning.BuildConfig;
import cn.ccsu.learning.utils.SPUtil;

/**
 * API地址管理
 */
public class NetApi {
    private static String HTTP = "http://";//协议

    private static String IP = "192.168.43.85";//IP地址

    private static String PORT = "8081";//端口

    private static String HIP;

    static {
        if (BuildConfig.DEBUG) {
            IP = SPUtil.getInstance().getString(SPUtil.IP, IP);
            PORT = SPUtil.getInstance().getString(SPUtil.PORT, PORT);
            HIP = HTTP + IP + ":" + PORT;
        } else {
            IP = SPUtil.getInstance().getString(SPUtil.IP, IP);
            PORT = SPUtil.getInstance().getString(SPUtil.PORT, PORT);
            HIP = HTTP + IP + ":" + PORT;
        }
    }

    //登录
    public static final String LOGIN = HIP + "/index/login";

    //注册
    public static final String REGISER = HIP + "/user/register";

}
