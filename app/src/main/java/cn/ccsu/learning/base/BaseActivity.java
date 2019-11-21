package cn.ccsu.learning.base;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.annotation.MenuRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.hd.commonmodule.utils.StatusBarUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;

import butterknife.ButterKnife;
import cn.ccsu.learning.R;
import cn.ccsu.learning.utils.ActivityCollector;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private KProgressHUD kProgressHUD;
    private CompositeDisposable mCompositeDisposable;//Rxjava订阅

    //自己修改
    public int pageNum = 0;//分页要查询的数据当前页码，默认为1
    public String pageSize = "10";//分页每页查询的数据量，默认10

    public String mIdStr = "mId";
    public String pIdStr = "pId";
    public String pageNumStr = "pageNum";
    public String pageSizeStr = "pageSize";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(setLayoutResourceID());
        ButterKnife.bind(this);
        initVariables();
        initViews(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        OkGo.getInstance().cancelTag(this);
        //解除所有未完成的订阅 防止内存泄漏
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    //Toolbar 设置
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setToolbarTitle(CharSequence title) {
        if (mToolbar == null) {
            initToolbar();
        }
        mToolbar.setTitle(title);
    }

    /**
     * 添加返回按键
     */
    public void initToolbarWhiteNav() {
        if (mToolbar == null) {
            initToolbar();
        }
        mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    /**
     * 添加返回按键
     */
    public void initToolbarBlackNav() {
        if (mToolbar == null) {
            initToolbar();
        }
        mToolbar.setNavigationIcon(R.drawable.ic_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    /**
     * 设置菜单
     *
     * @param resId
     */
    public void setInflateMenu(@MenuRes int resId) {
        if (mToolbar == null) {
            initToolbar();
        }
        mToolbar.inflateMenu(resId);
    }

    /**
     * 加载一个资源图标到Toolbar
     *
     * @param resID
     */
    public void initToolbarIcon(int resID) {
        if (mToolbar == null) {
            try {
                initToolbar();
            } catch (Exception e) {
                return;
            }
        }
        mToolbar.setNavigationIcon(resID);
    }

    /**
     * 设置菜单
     *
     * @param resId
     */
    public void setInflateMenu(@MenuRes int resId, Toolbar.OnMenuItemClickListener itemClickListener) {
        if (mToolbar == null) {
            initToolbar();
        }
        mToolbar.inflateMenu(resId);
        mToolbar.setOnMenuItemClickListener(itemClickListener);
    }

    public CompositeDisposable getmCompositeDisposable() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        return mCompositeDisposable;
    }

    /**
     * 添加订阅
     *
     * @param disposable
     */
    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    public void dispose() {
        if (mCompositeDisposable != null) mCompositeDisposable.dispose();
    }

    /**
     * 隐藏状态栏的高度
     */
    public void setStatusBarTrans() {
        //StatusBarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.transparent));
        //侵入状态栏
        StatusBarUtils.setStatusBarColor(this, Color.argb(0, 0, 0, 0), true);
    }

    /**
     * 隐藏状态栏的高度
     */
    public void setStatusBarTrans(final boolean isDecor) {
        //StatusBarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.transparent));
        //侵入状态栏
        StatusBarUtils.setStatusBarColor(this, Color.argb(0, 0, 0, 0), isDecor);
    }

    /**
     * 保留状态栏高度,状态栏设置为透明
     */
    public void setStatusBarShowHeight() {
        StatusBarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent));
    }

    /**
     * 保留状态栏高度,状态栏设置为透明
     */
    public void setStatusBarShowHeight(@ColorRes int id) {
        StatusBarUtils.setStatusBarColor(this, ContextCompat.getColor(this, id));
    }

    /**
     * 状态栏黑白模式
     * true 白色 false 黑
     */
    public void setStatusBarLightMode(boolean isLightMode) {
        StatusBarUtils.setStatusBarLightMode(this, isLightMode);
    }

    /**
     * 隐藏状栏 全屏
     */
    public void HiddenWindosStatus() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 隐藏状栏和虚拟按键 全屏
     */
    public void HiddenWindosAndButtons() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    protected abstract int setLayoutResourceID();

    protected abstract void initVariables();//上一个界面传过来的相关参数

    protected abstract void initViews(Bundle savedInstanceState);

    //加载对话框相关
    public KProgressHUD getProgressDialog() {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        } else {
            kProgressHUD = new KProgressHUD(this);
        }
        return kProgressHUD;
    }

    public void showProgressDialog(String message) {
        if (kProgressHUD == null) {
            kProgressHUD = new KProgressHUD(this);
        }
        kProgressHUD.setLabel(message);
        if (!isFinishing() && !kProgressHUD.isShowing()) {
            kProgressHUD.show();
        }
    }

    public void showProgressDialog(String message, boolean cancelable) {
        if (kProgressHUD == null) {
            kProgressHUD = new KProgressHUD(this);
        }
        kProgressHUD.setCancellable(cancelable);
        kProgressHUD.setLabel(message);
        if (!isFinishing() && !kProgressHUD.isShowing()) {
            kProgressHUD.show();
        }
    }

    public void closeProgressDialog() {
        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }

}
