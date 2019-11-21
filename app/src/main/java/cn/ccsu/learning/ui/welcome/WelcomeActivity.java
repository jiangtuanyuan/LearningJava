package cn.ccsu.learning.ui.welcome;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.hd.commonmodule.utils.FastJsonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.entity.UserBean;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetResultCode;
import cn.ccsu.learning.ui.login.LoginActivity;
import cn.ccsu.learning.ui.main.MainActivity;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.LogUtil;
import cn.ccsu.learning.utils.SPUtil;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WelcomeActivity extends BaseActivity {
    private String loginName = SPUtil.getInstance().getString(SPUtil.USER_NAME);
    private String loginPwd = SPUtil.getInstance().getString(SPUtil.USER_PWD);

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        HiddenWindosStatus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(loginPwd)) {
                    Login(loginName, loginPwd);
                } else {
                    LoginActivity.actionStart(WelcomeActivity.this);
                    finish();
                }
            }
        }, 2000);
    }

    private void Login(String loginName, String loginPwd) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("userName", loginName);
        httpParams.put("userPassword", loginPwd);

        OkGo.<String>post(NetApi.LOGIN)
                .params(httpParams)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                       // showProgressDialog("登录中..");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        try {
                            closeProgressDialog();
                            if (stringResponse.isSuccessful()) {
                                String s = stringResponse.body();
                                LogUtil.e("请求信息", s);
                                Logger.json(s);
                                if (!TextUtils.isEmpty(s)) {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int code = jsonObject.optInt(Constant.CODE);
                                    String data = jsonObject.optString(Constant.DATA);
                                    if (code == NetResultCode.CODE200.getCode()) {
                                        UserBean userBean = FastJsonUtils.toBean(data, UserBean.class);
                                        if (userBean != null) {
                                            SPUtil.getInstance().putString(SPUtil.USER_NAME, loginName);
                                            SPUtil.getInstance().putString(SPUtil.USER_PWD, loginPwd);
                                            User.getInstance().savaUserData(userBean);

                                            String token = stringResponse.headers().get("Token");
                                            if (!TextUtils.isEmpty(token)) {
                                                User.getInstance().savaToken(token);
                                                MainActivity.actionStart(WelcomeActivity.this);
                                                finish();
                                            } else {
                                                ToastUtil.showToast("未获取到Token,请重试..");
                                                LoginActivity.actionStart(WelcomeActivity.this);
                                                finish();
                                            }
                                        }
                                    } else {
                                        ToastUtil.showToast(jsonObject.optString("info"));
                                        LoginActivity.actionStart(WelcomeActivity.this);
                                        finish();
                                    }
                                } else {
                                    ToastUtil.showToast("未获取到返回数据!");
                                    LoginActivity.actionStart(WelcomeActivity.this);
                                    finish();
                                }
                            } else {
                                ToastUtil.showToast("请求失败!");
                                LoginActivity.actionStart(WelcomeActivity.this);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LoginActivity.actionStart(WelcomeActivity.this);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeProgressDialog();
                        LogUtil.e("请求信息", "---------------onError-----------" + e.getMessage());
                        LoginActivity.actionStart(WelcomeActivity.this);
                        finish();
                    }

                    @Override
                    public void onComplete() {
                        closeProgressDialog();
                    }
                });
    }
}
