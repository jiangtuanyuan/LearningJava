package cn.ccsu.learning.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpParams;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.net.NetApi;
import cn.ccsu.learning.net.NetUtil;
import cn.ccsu.learning.utils.ToastUtil;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 注册
 */
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_user_pwd1)
    EditText etUserPwd1;
    @BindView(R.id.et_user_pwd2)
    EditText etUserPwd2;
    @BindView(R.id.et_user_tel)
    EditText etUserTel;
    @BindView(R.id.et_user_school)
    EditText etUserSchool;
    @BindView(R.id.bt_register)
    Button btRegister;
    @BindView(R.id.rb_sex_nv)
    RadioButton rbSexNv;
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_register;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("学生注册");

    }

    /**
     * 校验密码
     *
     * @param pwd
     * @param pwd1
     * @return
     */
    private boolean checkPWD(String pwd, String pwd1) {
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showToast("密码不能为空");
            return false;
        }
        if (pwd.length() < 6) {
            ToastUtil.showToast("密码长度不能少于6位");
            return false;
        }
        if (TextUtils.isEmpty(pwd1)) {
            ToastUtil.showToast("请再次输入密码");
            return false;
        }
        if (!pwd.equals(pwd1)) {
            ToastUtil.showToast("两次密码不一致");
            return false;
        }
        return true;
    }

    @OnClick(R.id.bt_register)
    public void onViewClicked() {
        if (!TextUtils.isEmpty(etUserName.getText().toString()) && !TextUtils.isEmpty(etUserPwd1.getText().toString()) && !TextUtils.isEmpty(etUserPwd2.getText().toString())) {
            if (checkPWD(etUserPwd1.getText().toString(), etUserPwd2.getText().toString())) {
                Register();
            }
        } else {
            ToastUtil.showToast("请输入账号密码！");
        }
    }

    private void Register() {
        Map<String, Object> httpParams = new HashMap<>();

        httpParams.put("userName", etUserName.getText().toString());
        httpParams.put("userPassword", etUserPwd1.getText().toString());

        //httpParams.put("userRealname", etUserPwd1.getText().toString());
        if (rbSexNv.isChecked()) {
            httpParams.put("userSex", "女");
        } else {
            httpParams.put("userSex", "男");
        }

        httpParams.put("userSubordinate", etUserSchool.getText().toString());
        httpParams.put("userTel", etUserTel.getText().toString());

        Gson gson = new Gson();
        String str = gson.toJson(httpParams);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), str);

        NetUtil mNetUtil = new NetUtil();
        mNetUtil.postJsonNetData(NetApi.REGISER, body, new NetUtil.DataListener() {
            @Override
            public void showDialogLoading() {
                showProgressDialog("注册中", false);

            }

            @Override
            public void onSubScribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();

            }

            @Override
            public void onFaild() {
                closeProgressDialog();
            }

            @Override
            public void dissDialogmissLoad() {
                closeProgressDialog();
            }

            @Override
            public void onSuccess(String info, String data) {
                ToastUtil.showToast(info);
            }
        });
    }

}
