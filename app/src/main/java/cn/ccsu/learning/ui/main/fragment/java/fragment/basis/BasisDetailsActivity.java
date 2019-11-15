package cn.ccsu.learning.ui.main.fragment.java.fragment.basis;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;

/**
 * 详情
 */
public class BasisDetailsActivity extends BaseActivity {
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BasisDetailsActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_basis_details;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolbarBlackNav();
        setToolbarTitle("详情");

    }
}
