package cn.ccsu.learning.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hd.commonmodule.utils.StatusBarUtils;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.bugly.beta.Beta;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;
import cn.ccsu.learning.ui.main.fragment.java.JavaFragment;
import cn.ccsu.learning.ui.main.fragment.mine.MineFragment;
import cn.ccsu.learning.ui.main.fragment.test.TestFragment;
import cn.ccsu.learning.ui.main.fragment.works.WorksFragment;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {
    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.radio_java)
    RadioButton radioTask;
    @BindView(R.id.radio_mine)
    RadioButton radioLine;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    private boolean mPermissionsOK = false;//是否同意了所有申请的权限 默认false
    private List<Fragment> mFragmensts = new ArrayList<>();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatusBarTrans();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        checkPermissions();
        Beta.checkUpgrade(false, true);
        initView();
    }

    private void initView() {
        //将Fragment对象添加到list中
        mFragmensts.add(JavaFragment.newInstance("java"));
        mFragmensts.add(WorksFragment.newInstance("works"));
        mFragmensts.add(TestFragment.newInstance("test"));
        mFragmensts.add(MineFragment.newInstance("mine"));
        //设置RadioGroup开始时设置的按钮，设置第一个按钮为默认值
        radioGroup.check(R.id.radio_java);
        //初始时向容器中添加第一个Fragment对象
        switchFragment(mFragmensts.get(0)).commit();
    }

    @SuppressLint("CheckResult")
    private void checkPermissions() {
        RxPermissions mPermissions = new RxPermissions(this);
        mPermissions.request(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.REQUEST_INSTALL_PACKAGES)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mPermissionsOK = aBoolean;
                        if (!mPermissionsOK) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage("必须同意所有权限才能使用本程序!");
                            builder.setCancelable(false);
                            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                    dialog.dismiss();
                                }
                            });
                            builder.setPositiveButton("再次请求", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkPermissions();
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }
                });
    }


    @OnClick({R.id.radio_java, R.id.radio_mine, R.id.radio_works, R.id.radio_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_java:
                switchFragment(mFragmensts.get(0)).commit();
                break;
            case R.id.radio_works:
                switchFragment(mFragmensts.get(1)).commit();
                break;
            case R.id.radio_test:
                switchFragment(mFragmensts.get(2)).commit();
                break;
            case R.id.radio_mine:
                switchFragment(mFragmensts.get(3)).commit();
                break;
            default:
                break;
        }
    }

    /**
     * 按返回键,增加退出提示
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Fragment currentFragment = new Fragment();//（全局）

    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.framelayout, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }
}