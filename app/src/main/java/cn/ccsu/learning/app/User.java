package cn.ccsu.learning.app;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.ccsu.learning.entity.UserBean;
import cn.ccsu.learning.utils.SPUtil;

/**
 * 当前用户信息
 */
public class User {
    private User mUser;

    private String rid;//1 学生 2 教师  3管理员
    //
    private String userId;
    private String userName;
    private String userPassword;
    private String userRealname;
    private String userSex;
    private String userSubordinate;
    private String userTel;
    private String userEmail;

    public User getmUser() {
        return mUser;
    }

    public String getRid() {
        if (TextUtils.isEmpty(rid)) {
            return "1";
        } else {
            return rid;
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserRealname() {
        return userRealname;
    }

    public String getUserSex() {
        return userSex;
    }

    public String getUserSubordinate() {
        return userSubordinate;
    }

    public String getUserTel() {
        return userTel;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void savaUserData(UserBean bean) {
        this.rid = bean.getRole().getRid();
        this.userId = bean.getMesUser().getUserId();
        this.userName = bean.getMesUser().getUserName();
        this.userEmail = bean.getMesUser().getUserEmail();
        this.userPassword = bean.getMesUser().getUserPassword();
        this.userRealname = bean.getMesUser().getUserRealname();
        this.userSex = bean.getMesUser().getUserSex();
        this.userTel = bean.getMesUser().getUserTel();
        this.userSubordinate = bean.getMesUser().getUserSubordinate();
    }


    public void savaToken(String token) {
        Logger.e("token:" + token);
        SPUtil.getInstance().putString(SPUtil.TOKEN, token);
    }

    /**
     * 清空缓存信息
     */
    public void clerUserInfos() {
        this.rid = "";
        this.userId = "";
        this.userName = "";
        this.userEmail = "";
        this.userPassword = "";
        this.userRealname = "";
        this.userSex = "";
        this.userTel = "";
        this.userSubordinate = "";
        //SPUtil.getInstance().putString(SPUtil.TOKEN, "");
        SPUtil.getInstance().putString(SPUtil.USER_PWD, "");
    }

    public String getUserToken() {
        return SPUtil.getInstance().getString(SPUtil.TOKEN);
    }

    public static User getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final User sInstance = new User();
    }
}
