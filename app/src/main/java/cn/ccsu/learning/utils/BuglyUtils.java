package cn.ccsu.learning.utils;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.beta.ui.UILifecycleListener;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

import java.util.Locale;

import cn.ccsu.learning.ui.main.MainActivity;
import cn.ccsu.learning.R;

/**
 * Bugly相关高级配置
 */
public class BuglyUtils {
    public static final String TAG = "BuglyUtils";

    /**
     * app升级更新配置
     */
    public static void BuglyAppConfig() {
        //全量更新配置
        /* Beta更新高级设置 *****/
        //自动初始化开关 是否自动初始化SDK
        Beta.autoInit = true;
        //自动检查更新开关 是否自动检测APP更新 false:手动调用Beta.checkUpgrade()方法;
        Beta.autoCheckUpgrade = true;
        //设置升级检查周期为20s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
        Beta.upgradeCheckPeriod = 20 * 1000;
        //设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
        Beta.initDelay = 5 * 1000;
        //设置通知栏大图标
        Beta.largeIconId = R.mipmap.ic_launcher;
        //设置状态栏小图标
        Beta.smallIconId = R.mipmap.ic_launcher;
        //设置更新弹窗默认展示的banner
        Beta.defaultBannerId = R.mipmap.ic_launcher;
        //设置sd卡的Download为更新资源存储目录
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //设置开启显示打断策略
        Beta.showInterruptedStrategy = true;
        //添加可显示弹窗的Activity
        Beta.canShowUpgradeActs.add(MainActivity.class);//主界面 登录界面
        //设置自定义升级对话框UI布局
        // Beta.upgradeDialogLayoutId = R.layout.upgrade_dialog;
        //设置自定义tip弹窗UI布局
        //Beta.tipsDialogLayoutId = R.layout.tips_dialog;
        //设置是否显示消息通知 如果你不想在通知栏显示下载进度，你可以将这个接口设置为false，默认值为true。
        Beta.enableNotification = true;
        //如果你想在Wifi网络下自动下载，可以将这个接口设置为true，默认值为false。
        Beta.autoDownloadOnWifi = true;
        //设置是否显示弹窗中的apk信息
        Beta.canShowApkInfo = true;

        //设置升级对话框生命周期回调接口
        Beta.upgradeDialogLifecycleListener = new UILifecycleListener<UpgradeInfo>() {
            @Override
            public void onCreate(Context context, View view, UpgradeInfo upgradeInfo) {
                LogUtil.e(TAG, "Bugly onCreate");
            }

            @Override
            public void onStart(Context context, View view, UpgradeInfo upgradeInfo) {
                LogUtil.e(TAG, "Bugly onStart");
            }

            @Override
            public void onResume(Context context, View view, UpgradeInfo upgradeInfo) {
                LogUtil.e(TAG, "Bugly onResume");
                // 注：可通过这个回调方式获取布局的控件，如果设置了id，可通过findViewById方式获取，如果设置了tag，可以通过findViewWithTag，具体参考下面例子:
                // 通过id方式获取控件，并更改imageview图片
                ImageView imageView = (ImageView) view.findViewById(R.id.icon);
                imageView.setImageResource(R.mipmap.ic_launcher);
                // 通过tag方式获取控件，并更改布局内容
                TextView textView = (TextView) view.findViewWithTag("textview");
                // 更多的操作：比如设置控件的点击事件
            }

            @Override
            public void onPause(Context context, View view, UpgradeInfo upgradeInfo) {
                LogUtil.e(TAG, "Bugly onPause");
            }

            @Override
            public void onStop(Context context, View view, UpgradeInfo upgradeInfo) {
                LogUtil.e(TAG, "Bugly onStop");
            }

            @Override
            public void onDestroy(Context context, View view, UpgradeInfo upgradeInfo) {
                LogUtil.e(TAG, "Bugly onDestory");
            }
        };

        /*在application中初始化时设置监听，监听策略的收取*/
        Beta.upgradeListener = new UpgradeListener() {
            /**
             * 接收到更新策略
             * @param ret  0:正常 －1:请求失败
             * @param strategy 更新策略
             * @param isManual true:手动请求 false:自动请求
             * @param isSilence true:不弹窗 false:弹窗
             * @return 是否放弃SDK处理此策略，true:SDK将不会弹窗，策略交由app自己处理
             */
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                if (strategy != null) {
                    LogUtil.e(TAG, "Bugly onUpgrade 策略有更新");
                } else {
                    LogUtil.e(TAG, "Bugly onUpgrade 策略没有有更新");
                }
                UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
                if (upgradeInfo != null) {
                    LogUtil.e(TAG, "Bugly onUpgrade 本地策略版本:code" + upgradeInfo.versionCode + "," + upgradeInfo.versionName);
                }
            }
        };

        /* 设置更新状态回调接口 */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            /**
             * 更新成功
             * @param isManual
             */
            @Override
            public void onUpgradeSuccess(boolean isManual) {
                LogUtil.e(TAG, "Bugly onUpgradeSuccess 更新成功");
            }

            /**
             * 更新失败
             * @param isManual  true:手动检查 false:自动检查
             */
            @Override
            public void onUpgradeFailed(boolean isManual) {
                LogUtil.e(TAG, "Bugly onUpgradeFailed 更新失败");
            }

            /**
             * 正在更新
             * @param isManual
             */
            @Override
            public void onUpgrading(boolean isManual) {
                LogUtil.e(TAG, "Bugly onUpgrading 正在更新");
            }

            /**
             * 下载完成
             * @param isManual
             */
            @Override
            public void onDownloadCompleted(boolean isManual) {
                LogUtil.e(TAG, "Bugly onDownloadCompleted 下载完成");
            }

            /**
             * 没有更新
             * @param isManual
             */
            @Override
            public void onUpgradeNoVersion(boolean isManual) {
                LogUtil.e(TAG, "Bugly onUpgradeNoVersion 没有更新");
            }
        };
    }
    /**
     * APP 热更新配置
     */
    public static void BuglyHotConfig() {
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁，默认为true
        Beta.canAutoDownloadPatch = true;
        // 设置是否自动合成补丁，默认为true
        Beta.canAutoPatch = true;
        // 设置是否提示用户重启，默认为false
        Beta.canNotifyUserRestart = false;
        // 补丁回调接口
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFile) {
                LogUtil.e(TAG, "补丁下载地址:" + patchFile);

            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                LogUtil.e(TAG, "补丁下载中:" + String.format(Locale.getDefault(), "%s %d%%",
                        Beta.strNotificationDownloading,
                        (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)));

            }

            @Override
            public void onDownloadSuccess(String msg) {
                LogUtil.e(TAG, "补丁下载成功");
            }

            @Override
            public void onDownloadFailure(String msg) {
                LogUtil.e(TAG, "补丁下载失败:" + msg);
            }

            @Override
            public void onApplySuccess(String msg) {
                LogUtil.e(TAG, "补丁应用成功");
            }

            @Override
            public void onApplyFailure(String msg) {
                LogUtil.e(TAG, "补丁应用失败:" + msg);
            }

            @Override
            public void onPatchRollback() {

            }
        };
    }
}
