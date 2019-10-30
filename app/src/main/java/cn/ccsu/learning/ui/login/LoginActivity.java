package cn.ccsu.learning.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.ui.main.MainActivity;
import cn.ccsu.learning.utils.ETChangedUtlis;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.iv_user_name_cler)
    ImageView ivUserNameCler;
    @BindView(R.id.et_user_pwd)
    EditText etUserPwd;
    @BindView(R.id.cb_user_pwd_sh)
    CheckBox cbUserPwdSh;
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
        ETChangedUtlis.EditTextChangedListener(etUserName, ivUserNameCler);
        ETChangedUtlis.EditTextChangedListener(etUserPwd, cbUserPwdSh);
    }

    @OnClick(R.id.bt_login)
    public void onViewClicked() {
        MainActivity.actionStart(this);
        finish();
    }
}
