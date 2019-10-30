package cn.ccsu.learning.ui.welcome;

import android.os.Bundle;
import android.os.Handler;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.ui.login.LoginActivity;

public class WelcomeActivity extends BaseActivity {
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
                LoginActivity.actionStart(WelcomeActivity.this);
                finish();
            }
        }, 2000);
    }
}
