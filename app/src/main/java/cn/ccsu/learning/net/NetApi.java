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

    public static String HIP;

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
    //专题学习 mId pId
    // "pageNum": 0,
    // "pageSize": 10,
    //  createUser  = userId --->
    public static final String RESOURCE_LIST = HIP + "/resource/content";
    //删除
    public static final String RESOURCE_DELETE = HIP + "/resource/delres";
    //文件上传
    public static final String RESOURCE_FILE_UPLOAD = HIP + "/file/fileUpload";
    //文件下载
    public static final String RESOURCE_FILE_DOWNLOAD = HIP + "/file/download";
    //查看文件相关
    public static final String RESOURCE_FILE_SHOW = HIP + "/file/findByResId";
    //新增资源
    public static final String RESOURCE_ADD = HIP + "/resource/save";
    //修改资源 需要 resID
    public static final String RESOURCE_UPDATE = HIP + "/resource/update";
    //获取测试Title列表
    public static final String EXAM_TEST = HIP + "/exam/mum";
    //测试详情
    public static final String EXAM_TYPE = HIP + "/exam/typeExam";
    //查询所有用户
    public static final String USER_ALL = HIP + "/user/findAll";
    //删除
    public static final String USER_DELATE = HIP + "/user/delete";
    //退出登录
    public static final String USER_LOGOUT = HIP + "/back/logout";
    //我的下载
    public static final String RECORD_FINDALL = HIP + "/record/findAll";//我的下载
    //删除我的下载
    public static final String RECORD_DELETE = HIP + "/record/delete";//删除
    //修改信息
    public static final String USER_INFOS_UPDATE = HIP + "/user/update";//修改信息

}
