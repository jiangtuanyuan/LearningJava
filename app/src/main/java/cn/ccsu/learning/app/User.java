package cn.ccsu.learning.app;

import com.orhanobut.logger.Logger;

import cn.ccsu.learning.entity.UserBean;
import cn.ccsu.learning.utils.SPUtil;

/**
 * 当前用户类型
 */
public class User {
    private User mUser;
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
        this.userId = bean.getUserId();
        this.userName = bean.getUserName();
        this.userEmail = bean.getUserEmail();
        this.userPassword = bean.getUserPassword();
        this.userRealname = bean.getUserRealname();
        this.userSex = bean.getUserSex();
        this.userTel = bean.getUserTel();
        this.userSubordinate = bean.getUserSubordinate();
    }


    public void savaToken(String token) {
        Logger.e("token:"+token);
        SPUtil.getInstance().putString(SPUtil.TOKEN, token);
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
