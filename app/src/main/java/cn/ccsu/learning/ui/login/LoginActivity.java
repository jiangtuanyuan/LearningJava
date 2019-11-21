package cn.ccsu.learning.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hd.commonmodule.utils.FastJsonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.beta.Beta;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.app.User;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.entity.UserBean;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetResultCode;
import cn.ccsu.learning.ui.SetIPActivity;
import cn.ccsu.learning.ui.main.MainActivity;
import cn.ccsu.learning.ui.register.RegisterActivity;
import cn.ccsu.learning.utils.Constant;
import cn.ccsu.learning.utils.ETChangedUtlis;
import cn.ccsu.learning.utils.LogUtil;
import cn.ccsu.learning.utils.SPUtil;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.iv_user_name_cler)
    ImageView ivUserNameCler;
    @BindView(R.id.et_user_pwd)
    EditText etUserPwd;
    @BindView(R.id.cb_user_pwd_sh)
    CheckBox cbUserPwdSh;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.rb_type_students)
    RadioButton rbTypeStudents;
    @BindView(R.id.rb_type_teacher)
    RadioButton rbTypeTeacher;
    @BindView(R.id.rb_type_admin)
    RadioButton rbTypeAdmin;
    @BindView(R.id.rg_type)
    RadioGroup rgType;
    @BindView(R.id.bt_login)
    Button btLogin;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Beta.checkUpgrade(false, true);
        ETChangedUtlis.EditTextChangedListener(etUserName, ivUserNameCler);
        ETChangedUtlis.EditTextChangedListener(etUserPwd, cbUserPwdSh);

        etUserName.setText(SPUtil.getInstance().getString(SPUtil.USER_NAME));
        etUserPwd.setText(SPUtil.getInstance().getString(SPUtil.USER_PWD));

    }

    @OnClick({R.id.iv_head_image, R.id.tv_register, R.id.rb_type_students, R.id.rb_type_teacher, R.id.rb_type_admin, R.id.bt_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_head_image:
                SetIPActivity.actionStart(this);
                break;
            case R.id.tv_register:
                RegisterActivity.actionStart(this);
                break;
            case R.id.rb_type_students://学生
                //User.getInstance().setmUserType(User.UserType.STUDENTS);
                break;
            case R.id.rb_type_teacher://老师
                //User.getInstance().setmUserType(User.UserType.TEACHER);
                break;
            case R.id.rb_type_admin://管理员
                // User.getInstance().setmUserType(User.UserType.ADMIN);
                break;
            case R.id.bt_login:
                //MainActivity.actionStart(this);
                // finish();
                if (!TextUtils.isEmpty(etUserName.getText().toString()) && !TextUtils.isEmpty(etUserPwd.getText().toString())) {
                    Login(etUserName.getText().toString(), etUserPwd.getText().toString());
                } else {
                    ToastUtil.showToast("账号密码不能为空!");
                }
                break;
            default:
                break;
        }
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
                        showProgressDialog("登录中..");
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
                                    String info = jsonObject.optString(Constant.INFO);
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
                                                MainActivity.actionStart(LoginActivity.this);
                                                finish();
                                            } else {
                                                ToastUtil.showToast("未获取到Token,请重试..");
                                            }
                                        }
                                    } else {
                                        ToastUtil.showToast(jsonObject.optString("info"));
                                    }
                                } else {
                                    ToastUtil.showToast("未获取到返回数据!");
                                }
                            } else {
                                ToastUtil.showToast("请求失败!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast("解析异常!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeProgressDialog();
                        LogUtil.e("请求信息", "---------------onError-----------" + e.getMessage());
                        ToastUtil.showToast("连接服务器失败，请稍候再试。");
                    }

                    @Override
                    public void onComplete() {
                        closeProgressDialog();
                    }
                });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
