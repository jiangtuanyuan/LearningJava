package cn.ccsu.learning.net;


public enum NetResultCode {
    CODE404(404, "接口地址不存在"),
    CODE500(500, "服务器错误"),
    CODE200(200, "正常响应"),


    CODE10000(10000, "正常响应"),
    CODE1001(1001, "参数错误"),
    CODE1002(1002, "系统异常"),
    CODE1003(1003, "账户或者密码错误"),
    CODE1004(1004, "删除出错"),
    CODE1005(1005, "token验证错误"),
    CODE1006(1006, "手机号码错误"),
    CODE1007(1007, "修改出错"),
    CODE1008(1008, "添加出错"),
    CODE1009(1009, "验证码错误"),
    CODE1010(1010, "退出错误"),
    CODE1011(1011, "账户已锁定，请找管理员或技术支持解锁"),
    CODE1012(1012, "手机验证码错误"),
    CODE1013(1013, "用户不存在"),
    CODE1014(1014, "没有权限访问"),
    CODE1015(1015, "用户未登录，请先登录"),
    CODE1016(1016, "参数为空"),
    CODE1017(1017, "数据已存在"),
    CODE1018(1018, "无数据"),
    CODE1019(1019, "第三方接口调用异常"),
    CODE1020(1020, "状态异常"),
    CODE1021(1021, "数据异常"),
    CODE1022(1022, "用户未分配任何角色"),
    CODE1023(1023, "摄像头未开启"),
    CODE1024(1024, "今天已签到"),
    CODE1025(1025, "轨迹不存在或已结束"),
    CODE1026(1026, "文件上传错误"),
    CODE1027(1027, "名称重复");


    private int code;
    private String desc;

    NetResultCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NetResultCode getCode(int status) {
        for (NetResultCode code : values()) {
            if (status == code.code) {
                return code;
            }
        }
        return CODE404;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
